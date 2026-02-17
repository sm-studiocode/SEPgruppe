package kr.or.ddit.works.subscription.service;

public interface RoleGrantService {

    public void grantAdminRole(String userId);

    public void revokeRole(String userId);
}
