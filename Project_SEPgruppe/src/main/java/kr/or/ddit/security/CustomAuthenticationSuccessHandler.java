package kr.or.ddit.security;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import kr.or.ddit.works.login.vo.AllUserVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;

// POST /login -> Security Filter -> SuccessHandler (session 생성) -> redirect
// -> 로그인 성공 직후 실행 -> 사용자 권한에 따라 어느 URL로 보낼지 결정
// -> 필요한 경우 세션을 생성하고 로그인한 사용자에게 companyNo를 세션에 저장

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String SESSION_KEY = "companyNo";

    // onAuthenticationSuccess : 로그인 성공 직후 1번만 호출
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

    	// 1. 로그인한 사용자 객체 꺼내기
        RealUserWrapper user = (RealUserWrapper) authentication.getPrincipal();
        AllUserVO allUser = user.getRealUser();

        // 2. 로그인 성공 시점에 사용자가 Employee일 경우 해당 계정의 companyNo를 가져와 'companyNo' 변수에 저장
        String companyNo = null;
        if (allUser instanceof EmployeeVO) {
            companyNo = ((EmployeeVO) allUser).getCompanyNo();
        }

        // 3. companyNo가 Null이 아니고, 빈 문자열이 아니면 companyNo를 session에 저장
        if (companyNo != null && !companyNo.isEmpty()) {
            HttpSession session = request.getSession(true);
            session.setAttribute(SESSION_KEY, companyNo);
        }

        // 부모 (SimpleUrlAuthenticationSuccessHandler)의 로그인 성공 처리 로직 실행
        // -> 내부에서 handle() 호출 -> determineTargetUrl()로 이동할 URL 결정 -> redirect 수행
        super.onAuthenticationSuccess(request, response, authentication);
    }

    // SimpleUrlAuthenticationSuccessHandler의 부모(AbstractAuthenticationTargetUrlRequestHandler)에 정의된
    // determineTargetUrl() 메서드를 오버라이드
    // URL 결정
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

    	// 권한 목록을 읽고 EMPLOYEE, COMPANY, PROVIDER 중 무엇인지 체크
    	// 해당하는 값 true로 변경
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isEmployee = false;
        boolean isCompany = false;
        boolean isProvider = false;

        for (GrantedAuthority authority : authorities) {
            String a = authority.getAuthority();
            if ("EMPLOYEE".equals(a)) isEmployee = true;
            if ("COMPANY".equals(a)) isCompany = true;
            if ("PROVIDER".equals(a)) isProvider = true;
        }

        // 사용자 권한에 따라 로그인 성공 후 이동할 URL 반환
        if (isEmployee) return "/groupware";
        if (isCompany) return "/";
        if (isProvider) return "/";

        return "/";
    }
}
