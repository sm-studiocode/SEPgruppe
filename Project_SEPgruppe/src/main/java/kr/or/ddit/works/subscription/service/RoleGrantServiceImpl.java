package kr.or.ddit.works.subscription.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.ddit.works.mybatis.mappers.EmpRoleMapper;

@Service
public class RoleGrantServiceImpl implements RoleGrantService {

    @Autowired
    private EmpRoleMapper empRoleMapper;

    @Override
    public void grantAdminRole(String userId) {
        empRoleMapper.upsertAdminRole(userId);
    }

    @Override
    public void revokeRole(String userId) {
        empRoleMapper.deleteRoleByEmpId(userId);
    }

}
