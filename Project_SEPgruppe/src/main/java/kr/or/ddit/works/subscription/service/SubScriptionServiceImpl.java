package kr.or.ddit.works.subscription.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.mybatis.mappers.CompanyMapper;
import kr.or.ddit.works.mybatis.mappers.PaymentMapper;
import kr.or.ddit.works.mybatis.mappers.SubscriptionMapper;
import kr.or.ddit.works.subscription.client.PortOneClient;
import kr.or.ddit.works.subscription.vo.BillingKeyVO;
import kr.or.ddit.works.subscription.vo.SubscriptionPlansVO;
import kr.or.ddit.works.subscription.vo.SubscriptionsVO;


@Service
public class SubScriptionServiceImpl implements SubScriptionService {


    @Autowired
    private SubscriptionMapper subMapper;
    
    @Autowired
    private CompanyMapper comMapper;

    @Autowired 
    private PaymentMapper payMapper;
    
    @Autowired 
    private PortOneClient portOneClient;
    
    @Autowired
    private RoleGrantService roleGrantService;
    
	// 구독 플랜 전체 조회
    @Override
    public List<SubscriptionPlansVO> readPlanList() {
        return subMapper.planList();
    }

    // 구독 플랜 단건 조회
    @Override
    public SubscriptionPlansVO planOne(String planType) {
        return subMapper.planOne(planType);
    }

    // 구독 정보 INSERT
    @Override
    public int insertSubscription(SubscriptionsVO subscription) {

        SubscriptionsVO active = subMapper.selectActiveSubscriptionByContactId(subscription.getContactId());

        // 1차 방어: 서비스 레벨 체크 (유저에게 친절한 메시지)
        if (active != null) {
            throw new IllegalStateException("이미 활성 구독이 존재합니다.");
        }

        // 2차 방어: DB 유니크 인덱스가 최종적으로 막음(레이스 컨디션 대비)
        try {
            return subMapper.insertSubscription(subscription);

        } catch (DataIntegrityViolationException e) {
            // ORA-00001 (unique constraint) 같은 케이스
            throw new IllegalStateException("이미 활성 구독이 존재합니다.");
        }
    }
    
    // 마이페이지 사용자정보 조회 - 구독정보 조회
	@Override
	public SubscriptionsVO selectSubscription(String contactId) {
		return subMapper.selectSubscription(contactId);
	}

	// 관리자 페이지에서 모든 회사의 현재 구독 현황 조회
	@Override
	public List<SubscriptionsVO> subscriptionList() {
		return subMapper.subscriptionList();
	}

	// 관리자 페이지 구독 플랜 정보 수정
	@Override
	public void updatePlanInfo(SubscriptionPlansVO plan) {
		subMapper.updatePlan(plan);
		
	}
	
	// contactId 기준으로 활성화된 구독을 가지고 있는지 확인
	@Override
	public Map<String, Object> getPaymentFormData(String planType, String contactId) {
		
		// 1. 구독 플랜 단건 조회
		SubscriptionPlansVO plan = subMapper.planOne(planType);
		if(plan == null) throw new IllegalArgumentException("존재하지 않는 planType: " + planType);
		
		// 2. 회원 정보 조회
		CompanyVO company = comMapper.selectCompanyByContactId(contactId);
	    if (company == null) throw new IllegalStateException("회사 정보 없음: " + contactId);

		return Map.of("plan", plan, "company", company);
	}

	// 고객사 구독 해지
	@Override
	@Transactional
	public void cancelSubscription(String contactId) throws IOException {

	    // 1) billingKey 조회 (customer_uid)
	    BillingKeyVO billing = payMapper.selectBilling(contactId); // PaymentMapper 주입 필요

	    if (billing != null && billing.getBillingKey() != null) {
	        // 2) 포트원 토큰 + 예약취소
	        String token = portOneClient.getAccessToken();
	        JsonNode result = portOneClient.unscheduleAll(token, billing.getBillingKey());

	        int code = result.get("code").asInt();
	        if (code != 0) {
	            String msg = result.has("message") ? result.get("message").asText() : "포트원 예약취소 실패";
	            throw new IllegalStateException(msg);
	        }
	        
	    }

	    // 3) DB 구독 해지 update
	    int updated = subMapper.cancelActiveSubscriptionByContactId(contactId);
	    if (updated == 0) {
	        throw new IllegalStateException("활성 구독이 없어서 해지할 수 없습니다: " + contactId);
	    }
	    
	    // 구독 해지 시 관리자와 직원 삭제
	    comMapper.deleteEmployeesByContactId(contactId);
	    comMapper.clearAdminId(contactId);
	    roleGrantService.revokeRole(contactId);

	}

	// CustomAuthenticationSuccessHanlder에서 사용할 활성 구독 체크
	@Override
	public boolean hasActiveSubscription(String contactId) {
	    SubscriptionsVO active = subMapper.selectActiveSubscriptionByContactId(contactId);
	    return active != null;
	}

}
