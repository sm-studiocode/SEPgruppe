package kr.or.ddit.works.organization.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.works.organization.vo.DepartmentVO;

public interface DepartmentService {

	public List<DepartmentVO> selectListAllDepartment(String companyNo);

	public int addDepartment(DepartmentVO dept);

	public int deleteDepartment(String companyNo, String deptCd);

	public int updateDepartmentField(DepartmentVO dept);

	public List<DepartmentVO> parseExcel(MultipartFile file, String companyNo) throws Exception;

	public int bulkInsertDepartments(List<DepartmentVO> deptList);
}
