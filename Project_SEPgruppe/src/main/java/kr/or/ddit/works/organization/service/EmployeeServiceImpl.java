package kr.or.ddit.works.organization.service;

import java.io.File;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.common.TempPasswordGenerator;
import kr.or.ddit.paging.PaginationInfo;
import kr.or.ddit.paging.SimpleCondition;
import kr.or.ddit.security.CustomUserDetailService;
import kr.or.ddit.works.mail.service.MailService;
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
	private MailService mailService;


	// PaymentServiceImpl.insertEmpAdminIfNeeded()에서 관리자 직원이 없으면 
	// 관리자 직원 Insert하는 Service
	// Employee에서 관리자 생성 + 임시비번 메일 발송까지 진행하는 Service
	@Override
	public boolean createAdminWithTempPassword(EmployeeVO member) {
		// Insert 시 패스워드는 임시 비밀번호로 생성
        String tempPw = TempPasswordGenerator.generate();

        // EMPLOYEE 테이블에 저장할 비밀번호 암호화
        member.setEmpPw(passwordEncoder.encode(tempPw));

        // EMPLOYEE INSERT 시도
        try {
            mapper.insertEmployee(member);
		} catch (DuplicateKeyException e) {
			// PK를 기준으로 중복된 값이 있으면 INSERT 취소
	        return false;
		}
        // 임시 비밀번호 메일 발송
        // try~catch (메일 발송 실패해도 DB EMPLOYEE 테이블 INSERT 볼백 안 됨)
        try {
            mailService.sendTempPasswordMail(member.getEmpEmail(), tempPw);
        } catch (Exception e) {
            log.error("관리자 임시비밀번호 메일 발송 실패: {}", member.getEmpEmail(), e);
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
        if(StringUtils.isNotBlank(member.getEmpPw())){
            member.setEmpPw(passwordEncoder.encode(member.getEmpPw()));
        }
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