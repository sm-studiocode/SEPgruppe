package kr.or.ddit.works.subscription.service;

import java.util.List;

import kr.or.ddit.works.subscription.vo.SubscriptionPlansVO;
import kr.or.ddit.works.subscription.vo.SubscriptionsVO;

/**
 * SubScriptionService (구독 서비스 인터페이스)
 *
 * 역할:
 * - "구독 관련 기능 목록"을 정의하는 인터페이스
 * - 컨트롤러/다른 서비스에서는 구현체가 아니라
 *   이 인터페이스를 통해 기능을 호출함
 *
 * 쉽게 말하면:
 * - 구독 관련 기능의 '설계도'
 * - 실제 동작은 SubScriptionServiceImpl에서 수행됨
 */
public interface SubScriptionService {

    /**
     * [1] 구독 플랜 전체 목록 조회
     *
     * 언제 호출되나?
     * - 구독 상품 소개 페이지
     * - 관리자 플랜 관리 화면
     *
     * 반환값:
     * - DB에 등록된 모든 구독 플랜 리스트
     *   (BASIC / PRO / ENTERPRISE 등)
     */
    public List<SubscriptionPlansVO> readPlanList();

    /**
     * [2] 구독 플랜 단건 조회
     *
     * 파라미터:
     * - planType : 플랜 식별자 (예: BASIC, PRO)
     *
     * 언제 호출되나?
     * 1) 결제 화면 진입 시 (플랜 정보 보여주기)
     * 2) 결제 처리 시(scheduleAndPersist) → 가격 조작 방지용
     *
     * 반환값:
     * - 해당 planType에 대한 플랜 정보 1건
     */
    public SubscriptionPlansVO planOne(String planType);

    /**
     * [3] 구독 정보 등록
     *
     * 언제 호출되나?
     * - 정기결제 스케줄 등록 시
     * - SUBSCRIPTIONS 테이블에 구독 1건을 생성할 때
     *
     * 반환값:
     * - insert 결과 (보통 1이면 성공)
     */
    public int insertSubscription(SubscriptionsVO subscription);
    
    // 마이페이지 사용자정보 조회 - 구독정보 조회
	public SubscriptionsVO selectSubscription(String contactId);


}
