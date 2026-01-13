package kr.or.ddit.works.subscription.service;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import kr.or.ddit.works.subscription.vo.BillingKeyVO;
import kr.or.ddit.works.subscription.vo.PaymentsVO;

/**
 * PaymentService (서비스 인터페이스)
 *
 * 역할:
 * - 결제/정기결제/빌링키 관련 "기능 목록(규격)"을 정의하는 곳
 * - 컨트롤러는 구현체(PaymentServiceImpl)가 아니라,
 *   이 인터페이스를 바라보고 기능을 호출함 (결합도 낮추기)
 *
 * 초보용으로 쉽게 말하면:
 * - "결제 관련 기능 메뉴판" 같은 것
 * - 실제 요리는 PaymentServiceImpl이 함
 */
public interface PaymentService {

    /**
     * [1] 포트원 Access Token 발급
     *
     * 언제 쓰냐?
     * - 포트원 API를 호출하려면 토큰이 필요해서 발급받을 때
     *
     * 현재 너 코드 흐름에서는:
     * - PaymentServiceImpl에서 PortOneClient.getAccessToken()을 그대로 호출하는 용도
     */
    String getAccessToken() throws IOException;

    /**
     * [2] 포트원 정기결제 "스케줄 등록" 요청
     *
     * 파라미터 의미(초보용):
     * - customerUid      : billingKey(카드 등록 후 발급되는 값)
     * - merchantUid      : 우리 주문번호(고유해야 함)
     * - scheduleTimestamp: 예약 결제 실행 시간(epoch second)
     * - amount           : 결제 금액
     * - planType         : 어떤 플랜 결제인지(표시용)
     * - payment          : 우리 DB에 저장할 결제 정보(PAYMENTS 테이블용)
     *
     * 언제 쓰냐?
     * - "포트원에 스케줄 등록"만 단독으로 호출하고 싶을 때
     *
     * 참고:
     * - 현재 메인 흐름은 scheduleAndPersist()가 핵심이라
     *   이 메서드는 남겨두는 경우도 있고, 나중에 정리할 수도 있음
     */
    JsonNode requestSchedulePayment(
        String customerUid,
        String merchantUid,
        long scheduleTimestamp,
        long amount,
        String planType,
        PaymentsVO payment
    ) throws IOException;

    /**
     * [3] billingKey 저장
     *
     * 언제 호출됨?
     * - 프론트에서 카드 등록 성공 후 customerUid를 서버로 보내면
     *   /payment/saveBillingKey 컨트롤러에서 이 메서드를 호출해서 DB에 저장함
     *
     * 반환값 int:
     * - MyBatis insert 결과(보통 1이면 성공)
     */
    int saveBilling(BillingKeyVO billingKey);

    /**
     * [4] billingKey 조회
     *
     * 언제 호출됨?
     * - 정기결제 예약 전, billingKey가 있는지 확인할 때
     * - scheduleAndPersist() 안에서 사용됨
     *
     * 반환값:
     * - contactId에 해당하는 billingKey 1건(보통 최신 1건)
     */
    BillingKeyVO selectBilling(String contactId);

    /**
     * [5] 전체 결제 내역 조회
     *
     * 언제 호출됨?
     * - 관리자 결제 내역 화면(/payment)에서 list 뿌릴 때
     */
    List<PaymentsVO> paymentList();

    /**
     * [6] 특정 구독번호(subscriptionNo)의 결제 내역 조회
     *
     * 언제 호출됨?
     * - 구독 상세 화면에서 해당 구독에 연결된 결제 내역을 볼 때
     */
    List<PaymentsVO> selectPaymentsBySubscriptionNo(Long subscriptionNo);

    /**
     * [7] ⭐ 핵심 기능: 스케줄 등록 + DB 반영(트랜잭션)
     *
     * 이 메서드가 하는 일(초보용 요약):
     * 1) planType으로 DB에서 플랜 가격을 다시 조회(프론트 조작 방지)
     * 2) billingKey(카드 등록 값)가 있는지 확인
     * 3) SUBSCRIPTIONS 먼저 insert 해서 subscriptionNo 확보
     * 4) 포트원 스케줄 결제 등록 API 호출
     * 5) 성공하면 PAYMENTS insert (subscriptionNo 연결)
     * 6) 회사 기본 부서/관리자 계정이 없으면 생성
     *
     * 언제 호출됨?
     * - /payment/schedule 컨트롤러에서 호출됨
     */
    JsonNode scheduleAndPersist(String planType, String contactId) throws IOException;
}
