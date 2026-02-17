package kr.or.ddit.works.subscription.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import kr.or.ddit.works.company.service.CompanyService;
import kr.or.ddit.works.mybatis.mappers.CompanyMapper;
import kr.or.ddit.works.mybatis.mappers.PaymentMapper;
import kr.or.ddit.works.subscription.client.PortOneClient;
import kr.or.ddit.works.subscription.vo.BillingKeyVO;
import kr.or.ddit.works.subscription.vo.PaymentsVO;
import kr.or.ddit.works.subscription.vo.SubscriptionPlansVO;
import kr.or.ddit.works.subscription.vo.SubscriptionsVO;

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

    // 구독 완료 시 회사 기본 세팅 서비스
    @Autowired
    private CompanyService companyService;

    // 구독 완료 시 ROLE 권한 부여
    @Autowired
    private RoleGrantService roleGrantService;

    // ✅ (추가) 회사의 ADMIN_ID 조회용
    @Autowired
    private CompanyMapper companyMapper;

    // 포트원 토큰 발급을 PortOneClient에 위임해서 가져옴
    // 공통 사용 비즈니스 로직
    @Override
    public String getAccessToken() throws IOException {
        return portOneClient.getAccessToken();
    }


    // 실제 외부 결제 서버에 정기결제 스케줄을 등록하는 API 호출 서비스로직
    @Override
    public JsonNode requestSchedulePayment(
        String customerUid,     // billing key
        String merchantUid,     // 주문번호
        long scheduleTimestamp, // 예약 결제 시간
        long amount,            // 결제 금액
        String planType,        // 상품 타입
        PaymentsVO payment      // 상품 정보
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

    // 카드 등록 후 발급받은 billingKey(customerUid)를 DB에 저장하는 서비스로직
    @Override
    public int saveBilling(BillingKeyVO billingKey) {
        return paymentMapper.saveBilling(billingKey);
    }

    // 회사가 카드 등록했는지 확인하는 서비스로직
    @Override
    public BillingKeyVO selectBilling(String contactId) {
        return paymentMapper.selectBilling(contactId);
    }

    // 결제내역 목록 조회 서비스 로직
    @Override
    public List<PaymentsVO> paymentList() {
        return paymentMapper.paymentList();
    }

    // “구독하기 버튼” 누르면 실행되는 전체 결제 + 구독 생성 서비스로직
    @Transactional(rollbackFor = Exception.class)
    @Override
    public JsonNode scheduleAndPersist(String planType, String contactId) throws IOException {

        // 1. 플랜 재 조회 -> 가격 조작 방지
        SubscriptionPlansVO plan = subService.planOne(planType);
        if (plan == null) throw new IllegalArgumentException("존재하지 않는 planType: " + planType);

        // 1-1. 금액 검증
        Long price = plan.getMonthlyPrice();
        if (price == null || price <= 0) throw new IllegalStateException("플랜 금액이 올바르지 않음");

        // 2. billingKey 확인 -> 카드 등록이 되어있는지 확인
        BillingKeyVO billing = paymentMapper.selectBilling(contactId);
        if (billing == null || billing.getBillingKey() == null) {
            throw new IllegalStateException("billingKey 없음. 먼저 saveBillingKey 해야 함");
        }

        // 3. 스케줄 시간 생성
        LocalDate today = LocalDate.now();
        ZoneId seoul = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(seoul);

        // 3-1. 오늘 17시 40분 기준
        ZonedDateTime target = now
            .withHour(17)
            .withMinute(40)
            .withSecond(0)
            .withNano(0);

        // 3-2. 만약 이미 오늘 17:40이 지났으면(= 과거 시간) 내일 17:40으로 넘김
        if (!target.isAfter(now)) {
            target = target.plusDays(1);
        }
        long scheduleAt = target.toEpochSecond();

        // 4. merchantUid 생성 - 포트원에 넘길 주문번호화 거래식별자 생성
        String merchantUid = "order_" + System.currentTimeMillis();

        // 5. SUBSCRIPTION INSERT
        SubscriptionsVO sub = new SubscriptionsVO();

        // 5-1. 사용자 ID
        sub.setContactId(contactId);

        // 5-2. 구독 기간: 오늘 ~ 한 달 뒤
        sub.setSubscriptionStart(today.toString());
        sub.setSubscriptionEnd(today.plusMonths(1).toString());

        // 5-3. 어떤 플랜인지
        sub.setPlanType(planType);

        // 5-4. 청구일 표시용 문자열 (예: 매월 12일)
        sub.setBillingDate("매월 " + today.getDayOfMonth() + "일");

        // 5-5 어떤 billingKey를 쓸지 FK로 연결
        sub.setBillingKeyId(billing.getBillingKeyId());

        // 5-6. 실제 DB insert 실행
        subService.insertSubscription(sub);

        // 6. 포트원 스케줄 등록
        // 6-1. 토큰 발급
        String token = portOneClient.getAccessToken();

        // 6-2. 스케줄 등록 요청
        JsonNode result = portOneClient.schedulePayment(
            token,
            billing.getBillingKey(),
            merchantUid,
            scheduleAt,
            price,
            planType + " 플랜 정기결제"
        );

        // 6-3. 결과 code 확인
        int code = result.get("code").asInt();
        if (code != 0) {
            // 실패 메시지
            String msg = result.has("message") ? result.get("message").asText() : "정기결제 스케줄 등록 실패";

            // 여기서 예외를 던지면 @Transactional 때문에:
            // - SUBSCRIPTIONS insert 했던 것도 롤백됨
            throw new IllegalStateException(msg);
        }

        // 7. 구독 성공 시 ROLE 권한 부여
        // ✅ 중요:
        // - EMP_ROLE.EMP_ID는 EMPLOYEE.EMP_ID(FK)를 참조함
        // - 따라서 contactId(회사아이디)를 role에 넣으면 FK(ORA-02291)로 무조건 터짐
        // - 반드시 관리자 EMPLOYEE(예: {contactId}_admin) 생성 후, 그 empId로 ROLE_ADMIN을 부여해야 함

        // 7-1. 구독 성공 후 회사 기본 세팅(관리자 EMPLOYEE 생성 포함)
        companyService.ensureAdminSetup(contactId);

        // 7-2. 회사에 연결된 ADMIN_ID 조회 (COMPANIES.ADMIN_ID)
        String adminEmpId = companyMapper.selectAdminIdByContactId(contactId);
        if (adminEmpId == null || adminEmpId.trim().isEmpty()) {
            throw new IllegalStateException("COMPANIES.ADMIN_ID가 없습니다. contactId=" + contactId);
        }

        // 7-3. 관리자 EMP_ID에게 ROLE_ADMIN 부여 (contactId 아님!)
        roleGrantService.grantAdminRole(adminEmpId);

        // 7. PAYMENTS INSERT
        PaymentsVO payment = new PaymentsVO();

        // 7-1. 결제번호
        payment.setPaymentNo(merchantUid);

        // 7-2. 구독번호 연결
        payment.setSubscriptionNo(sub.getSubscriptionNo());

        // 7-3. 고객사 아이디
        payment.setContactId(contactId);

        // 7-4. 결제 금액
        payment.setPaymentAmount(price);

        paymentMapper.insertPayment(payment);

        // 최종적으로 포트원 응답 JSON을 컨트롤러로 반환
        return result;
    }

    // 고객사 관리의 고객사 결제 이력 조회
    @Override
    public List<PaymentsVO> paymentListByContactId(String contactId) {
        return paymentMapper.paymentListByContactId(contactId);
    }

}
