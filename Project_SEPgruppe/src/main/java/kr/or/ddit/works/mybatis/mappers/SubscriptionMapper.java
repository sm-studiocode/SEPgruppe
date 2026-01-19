package kr.or.ddit.works.mybatis.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.works.subscription.vo.SubscriptionPlansVO;
import kr.or.ddit.works.subscription.vo.SubscriptionsVO;

/**
 * SubscriptionMapper
 *
 * 역할:
 * - 구독 플랜(SUBSCRIPTION_PLANS) 조회
 * - 구독 정보(SUBSCRIPTIONS) 저장
 *
 * 언제 쓰이냐?
 * - 결제 화면에서 플랜 목록/플랜 상세 보여줄 때
 * - 정기결제 예약이 성공하면 SUBSCRIPTIONS에 구독정보 저장할 때
 */
@Mapper
public interface SubscriptionMapper {

    /**
     * 구독 플랜 전체 목록 조회
     *
     * 예) BASIC / PRO / ENTERPRISE 같은 상품 리스트
     * 언제 호출?
     * - 구독 플랜 안내 페이지에서 전체 목록 뿌릴 때
     */
    List<SubscriptionPlansVO> planList();

    /**
     * 구독 플랜 1개 조회
     *
     * planType으로 해당 플랜의 가격/인원수 제한 등을 가져옴
     * 언제 호출?
     * - 결제 화면에서 선택한 플랜 정보 보여줄 때
     * - scheduleAndPersist에서 "가격 조작 방지"용으로 DB에서 가격 다시 가져올 때
     */
    SubscriptionPlansVO planOne(String planType);

    /**
     * SUBSCRIPTIONS 테이블에 구독 정보 1건 저장
     *
     * 언제 호출?
     * - PaymentServiceImpl.scheduleAndPersist()에서
     *   "subscriptionNo 확보"하려고 먼저 insert할 때
     *
     * 보통 여기 insert는 selectKey(시퀀스)로 subscriptionNo를 만들어서
     * subscription 객체에 값이 세팅된 상태가 됨
     */
    int insertSubscription(SubscriptionsVO subscription);
    
    // 마이페이지 사용자정보 조회 - 구독정보 조회
	public SubscriptionsVO selectSubscription(@Param("contactId") String contactId);

}
