package kr.or.ddit.works.subscription.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.ddit.works.mybatis.mappers.SubscriptionMapper;
import kr.or.ddit.works.subscription.vo.SubscriptionPlansVO;
import kr.or.ddit.works.subscription.vo.SubscriptionsVO;

/**
 * SubScriptionServiceImpl
 *
 * 역할:
 * - SubScriptionService에서 정의한 기능을
 *   실제로 DB와 연결해서 실행하는 구현 클래스
 *
 * 구조 설명(초보용):
 * - Controller → Service(interface) → ServiceImpl → Mapper → DB
 * - 이 클래스는 "Mapper 호출만 담당"하는 중간 다리 역할
 */
@Service
public class SubScriptionServiceImpl implements SubScriptionService {

    /**
     * SubscriptionMapper 주입
     *
     * - MyBatis Mapper
     * - 실제 SQL(planList, planOne, insertSubscription)을 실행함
     */
    @Autowired
    private SubscriptionMapper mapper;

    /**
     * [1] 구독 플랜 전체 조회 서비스 로직
     *
     * 동작 설명:
     * - mapper.planList()를 그대로 호출
     * - 추가 가공 없이 DB 결과를 그대로 반환
     *
     * 호출 흐름:
     * - Controller → readPlanList() → mapper.planList() → DB
     */
    @Override
    public List<SubscriptionPlansVO> readPlanList() {
        return mapper.planList();
    }

    /**
     * [2] 구독 플랜 단건 조회 서비스 로직
     *
     * 동작 설명:
     * - planType을 받아서 mapper.planOne(planType) 호출
     * - DB에서 해당 플랜 정보 1건 조회
     *
     * 중요 포인트:
     * - 결제 처리 시에도 이 메서드를 다시 호출해서
     *   "프론트에서 넘어온 금액을 믿지 않고"
     *   DB 기준 가격을 사용함
     */
    @Override
    public SubscriptionPlansVO planOne(String planType) {
        return mapper.planOne(planType);
    }

    /**
     * [3] 구독 정보 등록 서비스 로직
     *
     * 동작 설명:
     * - SubscriptionsVO 객체를 받아서
     * - mapper.insertSubscription(subscription) 호출
     *
     * 주의:
     * - 이 insert는 selectKey를 사용해서
     *   subscriptionNo(구독번호)를 미리 생성함
     *
     * 호출 흐름:
     * - PaymentServiceImpl.scheduleAndPersist()
     *   → subService.insertSubscription()
     *   → mapper.insertSubscription()
     */
    @Override
    public int insertSubscription(SubscriptionsVO subscription) {
        return mapper.insertSubscription(subscription);
    }

    // 마이페이지 사용자정보 조회 - 구독정보 조회
	@Override
	public SubscriptionsVO selectSubscription(String contactId) {
		return mapper.selectSubscription(contactId);
	}

}
