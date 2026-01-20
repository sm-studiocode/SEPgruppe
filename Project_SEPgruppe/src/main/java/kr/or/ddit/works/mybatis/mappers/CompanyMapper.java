package kr.or.ddit.works.mybatis.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.works.company.vo.CompanyDivisionVO;
import kr.or.ddit.works.company.vo.CompanyVO;

/**
 * CompanyMapper
 *
 * 역할:
 * - 회사(Company) 정보 조회
 * - 회사 부서/구분(CompanyDivision) 생성
 * - 회사 관리자(empId) 업데이트
 *
 * 언제 쓰이냐?
 * - 결제 화면에서 회사 이름/아이디를 보여줄 때 (selectCompany)
 * - 구독 성공 시 회사 기본 부서 생성할 때 (insertCompanyDivision)
 * - 구독 성공 시 회사의 관리자 계정을 등록할 때 (updateCompanyAdmin)
 */
@Mapper
public interface CompanyMapper {

    /**
     * COMPANY_DIVISION 테이블에 부서/구분 정보 insert
     *
     * 언제 호출?
     * - 구독 성공 후 insertEmpAdminIfNeeded()에서
     *   회사 기본 division이 없으면 생성하기 위해 호출
     */
    int insertCompanyDivision(CompanyDivisionVO companyDivision);

    /**
     * 회사 관리자 계정(empId)을 회사 테이블에 업데이트
     *
     * empId: 새로 만든 관리자 사원 아이디 (예: contactId_admin)
     * contactId: 회사 로그인 아이디
     *
     * 언제 호출?
     * - 관리자 employee 생성 후 회사에 관리자 ID를 연결할 때
     */
    void updateCompanyAdmin(@Param("adminId")String adminId, @Param("contactId")String contactId);

    /**
     * contactId로 회사 정보 조회
     *
     * 언제 호출?
     * - PaymentsController.paymentForm()에서 결제 화면 띄울 때
     * - PaymentServiceImpl.insertEmpAdminIfNeeded()에서 회사 정보 가져올 때
     *
     * 왜 @Param이 붙어있냐?
     * - XML에서 #{contactId}로 쓰기 위해 이름을 명확히 주는 것
     */
    CompanyVO selectCompany(@Param("contactId") String contactId);

    /**
     * COMPANY_DIVISION이 이미 있는지 확인하는 카운트 쿼리
     *
     * 언제 호출?
     * - insertEmpAdminIfNeeded()에서
     *   "이미 있으면 만들지 말자" 체크용
     */
    int countCompanyDivision(String companyNo);
    
    // 마이페이지 정보수정
	public int updateCompany(CompanyVO member);

	public List<CompanyVO> companyList();

}
