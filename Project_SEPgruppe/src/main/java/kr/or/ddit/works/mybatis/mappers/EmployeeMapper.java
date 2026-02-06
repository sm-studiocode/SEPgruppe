package kr.or.ddit.works.mybatis.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.paging.PaginationInfo;
import kr.or.ddit.paging.SimpleCondition;
import kr.or.ddit.works.organization.vo.DepartmentVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import kr.or.ddit.works.organization.vo.OrganizationVO;

import java.util.List;

@Mapper
public interface EmployeeMapper {

	// PaymentServiceImpl.insertEmpAdminIfNeeded()에서 관리자 직원이 없으면 
	// 관리자 직원 Insert하는 MAPPER
	public int insertEmployee(EmployeeVO member);
	
    // 관리자페이지 전체 사원 수 조회 (페이징용) 
    public int countAllEmployees(@Param("companyNo") String companyNo,
    						@Param("simple") SimpleCondition simpleCondition);
    
    // 관리자페이지 전체 사원 수 조회 (퇴사하지 않은 사원 수)
    public int countActiveEmployees(@Param("companyNo") String companyNo);
    
    /** 관리자 - 전사 사원 조회 (페이징 포함) */
    public List<OrganizationVO> selectAllEmployees(
            @Param("companyNo") String companyNo,
            @Param("paging") PaginationInfo<OrganizationVO> paging,
            @Param("simple") SimpleCondition simpleCondition
    );
    /** 관리자 - 사원(들) 직위 변경 */
    public int updateEmployeesPosition(@Param("empIds") List<String> empIds, @Param("positionCd") String positionCd);

    /** 관리자 - 사원(들) 부서 변경 */
    public int updateEmployeesDepartment(@Param("empIds") List<String> empIds, @Param("deptCd") String deptCd);

    /** 관리자 - 사원(들) 삭제 */
    public int deleteEmployees(@Param("empIds") List<String> empIds, @Param("companyNo") String companyNo);

    /** 관리자 - 신규 empNo 생성용 */
    public String selectLastEmpNo(@Param("companyNo") String companyNo);

    /** 관리자 화면에서 부서 목록 필요할 때 */
    public List<DepartmentVO> selectDepartments(@Param("companyNo") String companyNo);
    
    /** 관리자 - 부서 업데이트 */
    public int updateDeptCd(@Param("empId") String empId, @Param("deptCd") String deptCd);

    /** 관리자 - 기존 부서장 부서코드 제거 */
    public int clearDeptCd(@Param("empId") String empId);
}
