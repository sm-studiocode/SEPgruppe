package kr.or.ddit.works.mybatis.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.works.organization.vo.DepartmentVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import kr.or.ddit.works.organization.vo.OrganizationVO;

import kr.or.ddit.works.organization.vo.OrganizationVO;

/**
 * 조직관리 매퍼
 * @author JYS
 * @since 2025. 3. 16.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 3. 16.     	JYS	          최초 생성
 *
 * </pre>
 */
@Mapper
public interface OrganizationMapper {
	
	/**
	 * 최상위 부서 조회
	 * @return
	 */
	public List<DepartmentVO> selectParentDep(@Param("companyNo") String companyNo);
	
	
	/**
	 * 하위 부서 조회
	 * @return
	 */
	public List<DepartmentVO> selectChildDep(@Param("deptCd")String deptCd, @Param("companyNo") String companyNo);
	
	/**
	 * 부서 내 사원 조회
	 * @return
	 */
	public List<EmployeeVO> selectEmployee(@Param("deptCd")String deptCd, @Param("companyNo") String companyNo); 
	
	List<OrganizationVO> searchEmployees(@Param("keyword") String keyword, @Param("companyNo") String companyNo);
    
	List<OrganizationVO> searchByDepartment(
        @Param("deptName") String deptName,
        @Param("keyword") String keyword
    );

	public OrganizationVO selectOrganization(String empId);
	
	// 그냥 사원 조회
	public List<EmployeeVO> selectAllEmployees(@Param("companyNo") String companyNo);
	
	public List<DepartmentVO> selectChildDepartments(@Param("companyNo") String companyNo, @Param("parentDeptCd") String parentDeptCd);
	
}
