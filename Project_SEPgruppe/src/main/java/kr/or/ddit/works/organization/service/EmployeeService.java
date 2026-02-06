package kr.or.ddit.works.organization.service;

import java.util.List;

import kr.or.ddit.paging.PaginationInfo;
import kr.or.ddit.works.organization.vo.DepartmentVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import kr.or.ddit.works.organization.vo.OrganizationVO;

public interface EmployeeService {
	
	// PaymentServiceImpl.insertEmpAdminIfNeeded()에서 관리자 직원이 없으면 
	// 관리자 직원 Insert하는 인터페이스
	// Employee에서 관리자 생성 + 임시비번 메일 발송까지 진행하는 인터페이스
	public boolean createAdminWithTempPassword(EmployeeVO member);
	
    /** 관리자 - 전사 조회 */
	public PaginationInfo<OrganizationVO> getAllEmployees(String companyNo, PaginationInfo<OrganizationVO> paging);

    /** 관리자 - 직원 등록 */
	public int insertEmployee(EmployeeVO member);

    /** 관리자 - 일괄 수정 */
	public int bulkUpdateEmployees(List<String> empIds, String fieldType, String value);

    /** 관리자 - 삭제 */
	public int deleteEmployees(List<String> empIds, String companyNo);

	public List<DepartmentVO> selectDepartments(String companyNo);


}
