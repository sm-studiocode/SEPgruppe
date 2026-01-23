package kr.or.ddit.works.mybatis.mappers;

import org.apache.ibatis.annotations.Mapper;

import kr.or.ddit.works.organization.vo.EmployeeVO;

@Mapper
public interface EmployeeMapper {

	// PaymentServiceImpl.insertEmpAdminIfNeeded()에서 관리자 직원이 없으면 
	// 관리자 직원 Insert하는 MAPPER
	public int insertEmployee(EmployeeVO member);
}
