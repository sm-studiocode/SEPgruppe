package kr.or.ddit.works.mybatis.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.works.organization.vo.DepartmentVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import kr.or.ddit.works.organization.vo.OrganizationVO;

@Mapper
public interface OrganizationMapper {

    List<DepartmentVO> selectParentDep(@Param("companyNo") String companyNo);

    List<DepartmentVO> selectChildDep(@Param("parentDeptCd") String parentDeptCd,
                                      @Param("companyNo") String companyNo);

    List<EmployeeVO> selectEmployee(@Param("deptCd") String deptCd,
                                    @Param("companyNo") String companyNo);

    List<OrganizationVO> searchEmployees(@Param("keyword") String keyword,
                                         @Param("companyNo") String companyNo);
    
    List<DepartmentVO> selectChildDepartments(@Param("companyNo") String companyNo,
            @Param("parentDeptCd") String parentDeptCd);
}
