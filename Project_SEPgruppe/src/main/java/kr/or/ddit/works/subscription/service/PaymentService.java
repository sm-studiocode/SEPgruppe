package kr.or.ddit.works.subscription.service;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import kr.or.ddit.works.subscription.vo.BillingKeyVO;
import kr.or.ddit.works.subscription.vo.PaymentsVO;

public interface PaymentService {

	// 포트원(아임포트) API를 호출하기 위한 access token 발급 서비스로직
    public String getAccessToken() throws IOException;

    // 실제 외부 결제 서버에 정기결제 스케줄을 등록하는 API 호출 서비스로직
    public JsonNode requestSchedulePayment(
        String customerUid,		// billing key (카드토큰)
        String merchantUid,		// 주문번호
        long scheduleTimestamp,	// 예약 결제 시간
        long amount,			// 결제금액
        String planType,		// 상품 타입
        PaymentsVO payment		// 상품 정보
    ) throws IOException;

    // 카드 등록 후 발급받은 billingKey(customerUid)를 DB에 저장하는 서비스로직
    public int saveBilling(BillingKeyVO billingKey);

    // 회사가 카드 등록했는지 확인하는 서비스로직
    public BillingKeyVO selectBilling(String contactId);

    // 결제내역 목록 조회 서비스 로직
    public List<PaymentsVO> paymentList();

    // “구독하기 버튼” 누르면 실행되는 전체 결제 + 구독 생성 서비스로직
    public JsonNode scheduleAndPersist(String planType, String contactId) throws IOException;
    
    // 고객사 관리의 고객사 결제 이력 조회
    public List<PaymentsVO> paymentListByContactId(String contactId);

}
