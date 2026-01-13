package kr.or.ddit.works.mybatis.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.works.subscription.vo.BillingKeyVO;
import kr.or.ddit.works.subscription.vo.PaymentsVO;

/**
 * PaymentMapper
 *
 * 역할:
 * - Java 코드 ↔ DB SQL 을 연결해주는 인터페이스
 * - 여기에는 "무슨 SQL을 실행할지"가 아니라
 *   "어떤 이름으로 SQL을 호출할지"만 정의한다
 *
 * 실제 SQL 내용은 PaymentMapper.xml에 있음
 */
@Mapper
public interface PaymentMapper {

    /**
     * PAYMENTS 테이블에 결제 내역 1건 INSERT
     *
     * 언제 호출되냐?
     * - 정기결제 스케줄이 포트원에서 성공했을 때
     * - PaymentServiceImpl.scheduleAndPersist() 7단계
     */
    int insertPayment(PaymentsVO payment);

    /**
     * BILLING_KEY 테이블에 billingKey(customerUid) 저장
     *
     * 언제 호출되냐?
     * - 카드 등록 성공 후
     * - /payment/saveBillingKey 컨트롤러에서 호출
     */
    int saveBilling(BillingKeyVO billingKey);

    /**
     * contactId(회사 아이디)로 billingKey 조회
     *
     * ⚠️ 주의:
     * - BILLING_KEY 테이블은 contactId가 UNIQUE가 아님
     * - 그래서 "최신 1건만" 가져오도록 XML에서 처리함
     *
     * 언제 호출되냐?
     * - 정기결제 예약 전에 카드 등록 여부 확인할 때
     */
    BillingKeyVO selectBilling(@Param("contactId") String contactId);

    /**
     * 전체 결제 내역 조회
     *
     * 언제 호출되냐?
     * - 관리자 결제 내역 화면
     */
    List<PaymentsVO> paymentList();

    /**
     * 특정 구독(subscriptionNo)에 해당하는 결제 내역 조회
     *
     * 언제 호출되냐?
     * - 구독 상세 화면
     * - "이 구독으로 어떤 결제들이 있었는지" 볼 때
     */
    List<PaymentsVO> selectPaymentsBySubscriptionNo(
        @Param("subscriptionNo") Long subscriptionNo
    );
}
