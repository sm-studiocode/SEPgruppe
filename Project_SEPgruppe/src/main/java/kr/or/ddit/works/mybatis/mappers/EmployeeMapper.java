package kr.or.ddit.works.mybatis.mappers;

import org.apache.ibatis.annotations.Mapper;

import kr.or.ddit.works.organization.vo.EmployeeVO;

/**
 * EmployeeMapper
 *
 * 역할:
 * - 사원(EMPLOYEE) 테이블에 직원 insert
 * - 특정 empId가 이미 존재하는지 count로 확인
 *
 * 언제 쓰이냐?
 * - 구독 성공 시 관리자 계정(contactId_admin)을 자동 생성할 때
 * - 이미 관리자 계정 있으면 중복 생성하지 않도록 체크할 때
 */
@Mapper
public interface EmployeeMapper {

    /**
     * EMPLOYEE 테이블에 직원 1명 insert
     *
     * 언제 호출?
     * - PaymentServiceImpl.insertEmpAdminIfNeeded()에서
     *   관리자 직원이 없으면 생성할 때 호출
     */
    int insertEmployee(EmployeeVO member);

    /**
     * empId가 이미 존재하는지 확인하는 카운트 쿼리
     *
     * 언제 호출?
     * - 관리자 직원 생성 전에 "이미 있으면 생성 스킵"하기 위해 호출
     */
    int countEmployee(String empId);
}
