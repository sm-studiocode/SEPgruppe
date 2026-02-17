package kr.or.ddit.works.organization.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.ddit.works.mybatis.mappers.OrganizationMapper;
import kr.or.ddit.works.organization.vo.DepartmentVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import kr.or.ddit.works.organization.vo.OrganizationVO;

@Service
public class OrganizationServiceImpl implements OrganizationService {
	
	@Autowired
	private OrganizationMapper mapper;

	@Override
    public List<DepartmentVO> selectParentDep(String companyNo) {
		return mapper.selectParentDep(companyNo);
	
    }

    @Override
    public List<DepartmentVO> selectChildDep(String deptCd, String companyNo) {
        return mapper.selectChildDep(deptCd, companyNo);
    }

    @Override
    public List<EmployeeVO> selectEmployee(String deptCd, String companyNo) {
        return mapper.selectEmployee(deptCd, companyNo);
    }

	@Override
    public List<OrganizationVO> searchEmployees(String keyword, String companyNo) {
        return mapper.searchEmployees(keyword, companyNo);
    }

    @Override
    public List<OrganizationVO> searchByDepartment(String deptName, String keyword) {
        return mapper.searchByDepartment(deptName, keyword);
    }

	@Override
	public List<EmployeeVO> selectAllEmployees(String companyNo) {
		return mapper.selectAllEmployees(companyNo);
	}

	@Override
	public List<DepartmentVO> selectChildDepartments(String companyNo, String parentDeptCd) {
		return mapper.selectChildDepartments(companyNo, parentDeptCd);
	}
}
