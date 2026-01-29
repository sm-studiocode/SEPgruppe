package kr.or.ddit.works.mybatis.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.works.subscription.vo.BillingKeyVO;
import kr.or.ddit.works.subscription.vo.PaymentsVO;

@Mapper
public interface PaymentMapper {

	// “구독하기 버튼” 누르면 실행되는 구독 생성 MAPPER
    public int insertPayment(PaymentsVO payment);

    // 카드 등록 후 발급받은 billingKey(customerUid)를 DB에 저장하는 MAPPER
    public int saveBilling(BillingKeyVO billingKey);

    // 회사가 카드 등록했는지 확인하는 MAPPER
    public BillingKeyVO selectBilling(@Param("contactId") String contactId);

    // 결제내역 목록 조회 MAPPER
    public List<PaymentsVO> paymentList();
    
    // 고객사 관리의 고객사 결제 이력 조회
    public List<PaymentsVO> paymentListByContactId(String contactId);


}
