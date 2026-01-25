package kr.or.ddit.works.subscription.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import kr.or.ddit.works.company.vo.CompanyDivisionVO;
import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.mybatis.mappers.CompanyMapper;
import kr.or.ddit.works.mybatis.mappers.PaymentMapper;
import kr.or.ddit.works.organization.service.EmployeeService;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import kr.or.ddit.works.subscription.client.PortOneClient;
import kr.or.ddit.works.subscription.vo.BillingKeyVO;
import kr.or.ddit.works.subscription.vo.PaymentsVO;
import kr.or.ddit.works.subscription.vo.SubscriptionPlansVO;
import kr.or.ddit.works.subscription.vo.SubscriptionsVO;

/**
 * PaymentService 구현체(서비스 로직 담당)
 *
 * 서비스(Service) 역할:
 * - 컨트롤러가 "이거 해줘"라고 시키는 실제 일을 처리하는 곳
 * - DB 저장/조회(MyBatis Mapper 호출)
 * - 외부 API 호출(PortOneClient 호출)
 * - "하나의 기능"을 수행하기 위해 여러 작업을 묶어서 처리(= 비즈니스 로직)
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    // 결제/빌링키 관련 DB 작업을 하는 Mapper
    @Autowired
    private PaymentMapper paymentMapper;

    // 포트원(아임포트) API 통신 전용 클래스
    @Autowired
    private PortOneClient portOneClient;

    // 구독 플랜 조회/구독 등록 서비스
    @Autowired
    private SubScriptionService subService;

    // 회사 정보 관련 DB 작업
    @Autowired
    private CompanyMapper companyMapper;

    // 직원(관리자 계정) 생성 관련 DB 작업
    @Autowired
    private EmployeeService empService;

    /**
     * 포트원 access token 발급을 PortOneClient에게 위임
     * - 컨트롤러가 토큰이 필요할 수도 있어서 인터페이스에 남아있을 수 있음
     */
    @Override
    public String getAccessToken() throws IOException {

        return portOneClient.getAccessToken();
    }

    /**
     * 포트원 "스케줄 결제 등록"을 PortOneClient에게 위임
     * - 지금 프로젝트에서는 scheduleAndPersist()에서 직접 schedulePayment를 호출하므로
     *   이 메서드는 사실상 "우회용/호환용"으로 남아있는 느낌임
     *
     * customerUid = billingKey (카드 등록 후 발급받은 값)
     * merchantUid = 우리 시스템 주문번호(고유해야 함)
     * scheduleTimestamp = 예약 결제 실행 시각(초 단위 epoch)
     * amount = 결제 금액
     */
    @Override
    public JsonNode requestSchedulePayment(
        String customerUid,
        String merchantUid,
        long scheduleTimestamp,
        long amount,
        String planType,
        PaymentsVO payment
    ) throws IOException {
        // 1) 포트원 토큰 발급
        String token = portOneClient.getAccessToken();

        // 2) 스케줄 결제 등록 API 호출
        return portOneClient.schedulePayment(
            token,
            customerUid,
            merchantUid,
            scheduleTimestamp,
            amount,
            planType + " 플랜 정기결제"
        );
    }

    /**
     * billingKey를 DB에 저장
     * - 컨트롤러 /payment/saveBillingKey에서 호출됨
     */
    @Override
    public int saveBilling(BillingKeyVO billingKey) {
        return paymentMapper.saveBilling(billingKey);
    }

    /**
     * contactId(회사 로그인 아이디)로 billingKey 조회
     * - 결제 예약 전에 "카드 등록이 되어 있는지" 확인할 때 사용
     */
    @Override
    public BillingKeyVO selectBilling(String contactId) {
        return paymentMapper.selectBilling(contactId);
    }

    /**
     * 결제 내역 목록 조회
     */
    @Override
    public List<PaymentsVO> paymentList() {
        return paymentMapper.paymentList();
    }



    /**
     * ===============================
     * ⭐ 핵심 메서드: scheduleAndPersist
     * ===============================
     *
     * "구독하기 버튼"을 누르면 이 메서드가 실행됨
     *
     * 이 메서드가 하는 일(초보용 요약):
     * 1) 사용자가 고른 planType으로 DB에서 플랜 가격을 다시 조회한다(프론트 조작 방지)
     * 2) 카드 등록(billingKey)이 DB에 있는지 확인한다
     * 3) 예약 결제 시간(scheduleAt)을 만든다
     * 4) SUBSCRIPTIONS(구독) 테이블에 먼저 insert 해서 subscriptionNo를 만든다
     * 5) 포트원에 "정기결제 스케줄 등록" API 호출한다
     * 6) 성공하면 PAYMENTS(결제) 테이블에 insert 한다(구독번호 연결)
     * 7) 회사용 관리자 계정/부서가 없으면 만들어준다
     * 8) 전체 성공하면 result(JSON)를 컨트롤러로 돌려준다
     *
     * @Transactional:
     * - 중간에 하나라도 실패하면 DB에 했던 작업들을 "롤백(취소)" 시키기 위함
     * - 예를 들어 포트원 실패했는데 구독만 저장되면 데이터가 꼬이니까!
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public JsonNode scheduleAndPersist(String planType, String contactId) throws IOException {

        // =========================
        // 1) 플랜 재조회(가격 조작 방지)
        // =========================
        // 프론트에서 amount를 보내면 조작 가능하니까,
        // planType으로 DB에서 가격을 "다시" 가져와서 amount를 확정함
        SubscriptionPlansVO plan = subService.planOne(planType);
        if (plan == null) throw new IllegalArgumentException("존재하지 않는 planType: " + planType);

        // 월 결제 가격을 long으로 꺼내서 사용
        long amount = plan.getMonthlyPrice() == null ? 0L : plan.getMonthlyPrice().longValue();
        if (amount <= 0) throw new IllegalStateException("플랜 금액이 올바르지 않음");

        // =========================
        // 2) billingKey 확인
        // =========================
        // 카드 등록을 먼저 해야 billingKey(customerUid)가 DB에 저장됨
        // 없으면 예약 결제를 만들 수 없음
        BillingKeyVO billing = paymentMapper.selectBilling(contactId);
        if (billing == null || billing.getBillingKey() == null) {
            throw new IllegalStateException("billingKey 없음. 먼저 saveBillingKey 해야 함");
        }

        // =========================
        // 3) 스케줄 시간 생성
        // =========================
        // 오늘 날짜 + 17:40으로 예약 시각을 만들고
        // Asia/Seoul 시간대로 고정해서 epoch second(초)로 변환함
        // ✅ 중요: today는 아래(구독 시작/종료, 청구일)에 계속 쓰니까 반드시 선언되어 있어야 함
        LocalDate today = LocalDate.now();

        ZoneId seoul = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(seoul);

        // 오늘 17:40을 기준 시각으로 잡음
        ZonedDateTime target = now
            .withHour(17)
            .withMinute(40)
            .withSecond(0)
            .withNano(0);

        // 만약 이미 오늘 17:40이 지났으면(= 과거 시간) 내일 17:40으로 넘김
        if (!target.isAfter(now)) {
            target = target.plusDays(1);
        }

        long scheduleAt = target.toEpochSecond();

        // =========================
        // 4) merchantUid 생성
        // =========================
        // 주문번호 같은 개념. 포트원에 스케줄 등록할 때 고유해야 함
        String merchantUid = "order_" + System.currentTimeMillis();

        // =========================
        // 5) SUBSCRIPTIONS 먼저 INSERT
        // =========================
        // 이유:
        // - PAYMENTS에는 subscriptionNo(FK)가 연결될 수 있음
        // - subscriptionNo는 SUBSCRIPTIONS insert 할 때 seq로 만들어짐(selectKey)
        // 그래서 구독을 먼저 만들어서 subscriptionNo를 확보함
        SubscriptionsVO sub = new SubscriptionsVO();
        sub.setContactId(contactId);

        // 아직 실제 결제 성공이 아니라 "예약됨" 상태로 표시
        sub.setPaymentStatus("예약됨");

        // 구독 기간: 오늘 ~ 한 달 뒤
        sub.setSubscriptionStart(today.toString());
        sub.setSubscriptionEnd(today.plusMonths(1).toString());

        // 활성 상태(현재 구독 중)
        sub.setSubscriptionsActive("Y");

        // 어떤 플랜인지
        sub.setPlanType(planType);

        // 청구일 표시용 문자열 (예: 매월 12일)
        sub.setBillingDate("매월 " + today.getDayOfMonth() + "일");

        // 어떤 billingKey를 쓸지 FK로 연결
        sub.setBillingKeyId(billing.getBillingKeyId());

        // 자동결제 여부
        sub.setAutoPayment('Y');

        // 실제 DB insert 실행 (이후 sub.getSubscriptionNo()에 값이 들어있어야 함)
        subService.insertSubscription(sub);

        // =========================
        // 6) 포트원 스케줄 등록(외부 API 호출)
        // =========================
        // 6-1) 토큰 발급
        String token = portOneClient.getAccessToken();

        // 6-2) 스케줄 등록 요청
        JsonNode result = portOneClient.schedulePayment(
            token,
            billing.getBillingKey(), // customer_uid (billingKey)
            merchantUid,
            scheduleAt,
            amount,
            planType + " 플랜 정기결제"
        );

        // 6-3) 결과 code 확인
        int code = result.get("code").asInt();
        if (code != 0) {
            // 실패 메시지가 있으면 그걸 보여주고, 없으면 기본 메시지
            String msg = result.has("message") ? result.get("message").asText() : "정기결제 스케줄 등록 실패";

            // 여기서 예외를 던지면 @Transactional 때문에:
            // - SUBSCRIPTIONS insert 했던 것도 롤백됨(= DB에 남지 않음)
            throw new IllegalStateException(msg);
        }

        // =========================
        // 7) PAYMENTS INSERT
        // =========================
        // 포트원 스케줄 등록 성공했으니
        // 우리 DB에도 결제 "예약" 내역을 남김
        PaymentsVO payment = new PaymentsVO();
        payment.setPaymentNo(merchantUid);

        // 방금 만든 구독번호 연결 (FK)
        payment.setSubscriptionNo(sub.getSubscriptionNo());

        payment.setContactId(contactId);
        payment.setPaymentAmount(amount);
        payment.setPaymentMethod("CARD");
        payment.setPaymentStatus("예약됨");
        payment.setAutoPayment("Y");

        paymentMapper.insertPayment(payment);

        // =========================
        // 8) 회사/부서/관리자 계정 생성(있으면 스킵)
        // =========================
        // 구독 성공 시 회사에 기본 부서/관리자 사원을 만들어주는 로직
        // 이미 있으면 중복 생성하면 안 되므로 count로 체크해서 없을 때만 insert
        insertEmpAdminIfNeeded(contactId);

        // 최종적으로 포트원 응답 JSON을 컨트롤러로 반환
        return result;
    }

    /**
     * 회사의 기본 부서/관리자 직원 계정이 없으면 생성
     *
     * 하는 일:
     * 1) 회사 정보 조회
     * 2) COMPANY_DIVISION이 없으면 insert
     * 3) EMPLOYEE 관리자 계정이 없으면 insert
     * 4) 회사 관리자 id 업데이트
     */
    protected void insertEmpAdminIfNeeded(String contactId) {

    	// 등록된 회사가 있는지 확인
        CompanyVO company = companyMapper.selectCompany(contactId);
        if (company == null) {
            throw new IllegalStateException("회사 정보 없음: " + contactId);
        }

        // COMPANY 테이블에 등록된 회사가 있으면 DOMPANY_DIVISION INSERT
        try {
            CompanyDivisionVO div = new CompanyDivisionVO();
            div.setCompanyNo(company.getBusinessRegNo());   // 너 기존 로직 그대로
            div.setContactId(company.getContactId());
            companyMapper.insertCompanyDivision(div);
        } catch (DuplicateKeyException e) {
            // 이미 있으면 스킵
        }

        // EMPLOYEE 관리자 계정 생성
        String adminEmpId = contactId + "_admin";
        
        // 값 넣기
        EmployeeVO member = new EmployeeVO();
        member.setEmpId(adminEmpId);
        member.setCompanyNo(company.getBusinessRegNo());
        member.setEmpNo(String.valueOf(company.getBusinessRegNo()));
        member.setEmpNm(company.getCompanyName());
        member.setEmpZip(company.getCompanyZip());
        member.setEmpAdd1(company.getCompanyAdd1());
        member.setEmpAdd2(company.getCompanyAdd2());
        
        // 임시 비밀번호 발송을 위한 이메일 세팅 
        member.setEmpEmail(company.getContactEmail());

        // EMPLOYEE 테이블 insert
        boolean created = empService.createAdminWithTempPassword(member);

        // 새로 만들어졌을 때만 회사 관리자 ID 업데이트
        if (created) {
            companyMapper.updateCompanyAdmin(member.getEmpId(), contactId);
        }
    }

}
