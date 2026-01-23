package kr.or.ddit.works.organization.service;

import kr.or.ddit.works.organization.vo.EmployeeVO;

public interface EmployeeService {
	
	// PaymentServiceImpl.insertEmpAdminIfNeeded()에서 관리자 직원이 없으면 
	// 관리자 직원 Insert하는 인터페이스
	// Employee에서 관리자 생성 + 임시비번 메일 발송까지 진행하는 인터페이스
	public boolean createAdminWithTempPassword(EmployeeVO member);

}
