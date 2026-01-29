package kr.or.ddit.works.subscription.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import kr.or.ddit.works.subscription.vo.SubscriptionPlansVO;
import kr.or.ddit.works.subscription.vo.SubscriptionsVO;


public interface SubScriptionService {

	// 구독 플랜 전체 조회
    public List<SubscriptionPlansVO> readPlanList();

    // 구독 플랜 단건 조회
    public SubscriptionPlansVO planOne(String planType);

    // 구독 정보 INSERT
    public int insertSubscription(SubscriptionsVO subscription);
    
    // 마이페이지 사용자정보 조회 - 구독정보 조회 
	public SubscriptionsVO selectSubscription(String contactId);

	// 관리자 페이지에서 모든 회사의 현재 구독 현황 조회
	public List<SubscriptionsVO> subscriptionList();
	
	// 관리자 페이지 구독 플랜 정보 수정
	public void updatePlanInfo(SubscriptionPlansVO plan); 
	
	// contactId 기준으로 활성화된 구독을 가지고 있는지 확인
	public Map<String, Object> getPaymentFormData(String planType, String contactId);
	
	// 관리자 페이지 구독 해지
	public void cancelSubscription(String contactId) throws IOException;


}
