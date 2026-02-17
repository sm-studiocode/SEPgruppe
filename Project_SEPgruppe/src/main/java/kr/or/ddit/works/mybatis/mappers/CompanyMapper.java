package kr.or.ddit.works.mybatis.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.works.company.vo.CompanyDivisionVO;
import kr.or.ddit.works.company.vo.CompanyVO;

@Mapper
public interface CompanyMapper {

    // 회원가입 MAPPER - LoginServiceImpl에서 사용
    public int insertCompanyDivision(CompanyDivisionVO companyDivision);

    // 상품 구독 완료 후 adminID 연결용 Company UPDATE MAPPER - PaymentServiceImpl에서 사용
    // 구독 성공 후 최초 관리자 생성 시 사용
    public void updateCompanyAdmin(@Param("adminId")String adminId, @Param("contactId")String contactId);

    // 회원정보 조회 MAPPER
    public CompanyVO selectCompany(@Param("contactId") String contactId);

    // 마이페이지 정보수정 MAPPER
    public int updateCompany(CompanyVO member);

    // 관리자페이지 대시보드 전제 목록 가져오는 MAPPER - ProviderServiceImpl에서 사용
    public List<CompanyVO> companyList();

    // 구독 신청 페이지에서 회원정보 가져오는 MAPPER - SubScriptionServiceImpl에서 사용
    public CompanyVO selectCompanyByContactId(@Param("contactId") String contactId);

    // 구독 해지 시 회사의 관리자 제거
    public int clearAdminId(String contactId);

    // 구독 해지 시 회사 소속 직원 전체 삭제
    public int deleteEmployeesByContactId(String contactId);

    // 부서에 속한 회원정보 조회 - 관리자페이지
    public CompanyVO selectCompanyNo(String companyNo);

    // ✅ (추가) 구독 성공 후 ADMIN_ID 조회 (ROLE 부여 대상 empId)
    public String selectAdminIdByContactId(@Param("contactId") String contactId);
}
