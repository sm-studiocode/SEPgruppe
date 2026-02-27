package kr.or.ddit.works.mybatis.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.works.organization.vo.EmpRoleVO;

@Mapper
public interface EmpRoleMapper {

    /** 특정 사원의 권한 목록 */
    public List<EmpRoleVO> selectRolesByEmpId(@Param("empId") String empId);

    /** 권한 부여 (중복이면 무시) */
    public int insertRole(@Param("empId") String empId,
                   @Param("roleName") String roleName,
                   @Param("deptCd") String deptCd);

    /** 권한 회수 (특정 role + deptCd 스코프) */
    public int deleteRole(@Param("empId") String empId,
                   @Param("roleName") String roleName,
                   @Param("deptCd") String deptCd);

    /** (구독/테넌트용) 테넌트 관리자 부여 */
    public int grantTenantAdmin(@Param("empId") String empId);

    /** (구독/테넌트용) 테넌트 관리자 회수 */
    int revokeTenantAdmin(@Param("empId") String empId);
}