package kr.or.ddit.works.organization.service;

import java.util.List;

import kr.or.ddit.works.organization.vo.DepartmentVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import kr.or.ddit.works.organization.vo.OrganizationVO;

public interface OrganizationService {
	 public List<DepartmentVO> selectParentDep(String companyNo);
	 public List<DepartmentVO> selectChildDep(String deptCd, String companyNo);
	 public List<EmployeeVO> selectEmployee(String deptCd, String companyNo);
	 public List<OrganizationVO> searchEmployees(String keyword, String companyNo);
	 public List<OrganizationVO> searchByDepartment(String deptName, String keyword);
	 public List<EmployeeVO> selectAllEmployees(String companyNo);
	 
	 public List<DepartmentVO> selectChildDepartments(String companyNo, String parentDeptCd);


}
