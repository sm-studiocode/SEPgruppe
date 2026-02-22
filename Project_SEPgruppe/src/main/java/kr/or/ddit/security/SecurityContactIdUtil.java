package kr.or.ddit.security;

import org.springframework.security.core.Authentication;

import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.login.vo.AllUserVO;

public final class SecurityContactIdUtil {

    private SecurityContactIdUtil() {}

    /**
     * COMPANY 흐름에서 "진짜 contactId" 추출
     * - auth.getName()은 로그인쿼리 때문에 COMPANY일 때 adminId(empId)로 바뀔 수 있음
     * - 구독/결제/회사조회는 CONTACT_ID로 해야 함
     */
    public static String resolveContactId(Authentication authentication) {
        if (authentication == null) return null;

        Object principal = authentication.getPrincipal();
        if (principal instanceof RealUserWrapper) {
            RealUserWrapper wrapper = (RealUserWrapper) principal;
            AllUserVO allUser = wrapper.getRealUser();

            if (allUser instanceof CompanyVO) {
                return ((CompanyVO) allUser).getContactId();
            }
        }

        // fallback
        return authentication.getName();
    }
}