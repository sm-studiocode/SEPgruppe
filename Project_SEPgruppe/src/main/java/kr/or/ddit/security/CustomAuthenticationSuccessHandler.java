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
import kr.or.ddit.works.mail.exception.NeedOAuthRedirectException;
import kr.or.ddit.works.mail.service.MailService;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String SESSION_KEY = "companyNo";

    private final MailService mailService;

    public CustomAuthenticationSuccessHandler(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        RealUserWrapper user = (RealUserWrapper) authentication.getPrincipal();
        AllUserVO allUser = user.getRealUser();

        // ✅ (중요) 로그인 성공 시점에 "검증된 companyNo"를 세션에 저장
        String companyNo = null;
        if (allUser instanceof EmployeeVO) {
            companyNo = ((EmployeeVO) allUser).getCompanyNo();
        }

        if (companyNo != null && !companyNo.isEmpty()) {
            HttpSession session = request.getSession(true);
            session.setAttribute(SESSION_KEY, companyNo);
        }

        // 직원인 경우 메일 OAuth 체크 (원래 로직 유지)
        if (allUser instanceof EmployeeVO) {
            String empId = ((EmployeeVO) allUser).getEmpId();
            try {
                // mailService.tryAutoAuth(empId); // 필요 시 사용
            } catch (NeedOAuthRedirectException e) {
                getRedirectStrategy().sendRedirect(request, response, "/mail/oauth/start?empId=" + empId);
                return;
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

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

        if (isEmployee) {
            // ✅ companyNo를 URL에 붙이지 않음 (세션 기반)
            return "/groupware";
        }
        if (isCompany) return "/";
        if (isProvider) return "/";

        return "/";
    }
}
