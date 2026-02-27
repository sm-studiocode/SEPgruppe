package kr.or.ddit.works.subscription.service;

public interface RoleGrantService {

    /** 구독 완료 시 테넌트 관리자 권한 부여 */
    public void grantAdminRole(String userId);

    /** 구독 해지 시 테넌트 관리자 권한 회수 */
    public void revokeRole(String userId);
}
