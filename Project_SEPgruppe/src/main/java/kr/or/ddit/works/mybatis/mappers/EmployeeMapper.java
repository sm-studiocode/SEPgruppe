package kr.or.ddit.works.mybatis.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.paging.SimpleCondition;
import kr.or.ddit.works.organization.vo.EmployeeVO;

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
}
