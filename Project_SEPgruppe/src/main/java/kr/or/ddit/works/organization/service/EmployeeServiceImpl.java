package kr.or.ddit.works.organization.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kr.or.ddit.paging.PaginationInfo;
import kr.or.ddit.paging.SimpleCondition;
import kr.or.ddit.works.account.credential.TempPasswordIssuer;
import kr.or.ddit.works.mail.type.MailPurpose;
import kr.or.ddit.works.mybatis.mappers.EmployeeMapper;
import kr.or.ddit.works.organization.vo.DepartmentVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import kr.or.ddit.works.organization.vo.OrganizationVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeMapper mapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private TempPasswordIssuer tempPasswordIssuer;

	// PaymentServiceImpl.insertEmpAdminIfNeeded()에서 관리자 직원이 없으면 
	// 관리자 직원 Insert하는 Service
	// Employee에서 관리자 생성 + 임시비번 메일 발송까지 진행하는 Service
	@Override
	public boolean createAdminEmployeeIfAbsent(EmployeeVO member) {

		// ✅ 0. 필수값 방어 (admin 생성은 구독 플로우의 핵심이라 여기서 터지면 추적 힘듦)
		if (member == null || StringUtils.isBlank(member.getEmpId())) {
			throw new IllegalArgumentException("관리자 생성 실패: empId가 없습니다.");
		}
		if (StringUtils.isBlank(member.getCompanyNo())) {
			throw new IllegalArgumentException("관리자 생성 실패: companyNo가 없습니다.");
		}

		// ✅ 1. 먼저 존재 체크(중복 insert 방지)
		// - EMP_ROLE FK 때문에 “ROLE 부여” 전에 EMPLOYEE(admin)가 반드시 존재해야 함
		int exists = mapper.existsEmployeeByEmpId(member.getEmpId());
		if (exists > 0) {
			return false; // 이미 있으면 생성 안 함
		}

        // EMPLOYEE INSERT 시도
        try {
            mapper.insertEmployee(member);
		} catch (DuplicateKeyException e) {
			// PK를 기준으로 중복된 값이 있으면 INSERT 취소
	        return false;
		}
        return true; // 모든 항목이 통과하면 성공
    }
	
    // admin: 전사 사원 조회 
    @Override
    public PaginationInfo<OrganizationVO> getAllEmployees(String companyNo, PaginationInfo<OrganizationVO> paging) {
        SimpleCondition condition = paging.getSimpleCondition();
        paging.setTotalRecord(mapper.countAllEmployees(companyNo, condition));
        paging.setDataList(mapper.selectAllEmployees(companyNo, paging, condition));
        return paging;
    }
    
    // admin: 직원 등록 
    @Override
    public int insertEmployee(EmployeeVO member) {

        String encodedTempPw = tempPasswordIssuer.issueAndSend(
            member.getEmpEmail(),
            MailPurpose.EMPLOYEE_INVITE
        );

        member.setEmpPw(encodedTempPw);

        return mapper.insertEmployee(member);
    }
    
    // admin: 일괄 수정 
    @Override
    public int bulkUpdateEmployees(List<String> empIds, String fieldType, String value) {
        if("position".equals(fieldType)) return mapper.updateEmployeesPosition(empIds,value);
        if("department".equals(fieldType)) return mapper.updateEmployeesDepartment(empIds,value);
        return 0;
    }

    // admin: 삭제 
    @Override
    public int deleteEmployees(List<String> empIds, String companyNo) {
        return mapper.deleteEmployees(empIds, companyNo);
    }


    // admin: 부서 목록 
    @Override
    public List<DepartmentVO> selectDepartments(String companyNo) {
        return mapper.selectDepartments(companyNo);
    }

}
