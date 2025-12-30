package kr.or.ddit.security;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import kr.or.ddit.works.login.vo.AllUserVO;
import kr.or.ddit.works.mail.exception.NeedOAuthRedirectException;
import kr.or.ddit.works.mail.service.MailService;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import lombok.extern.slf4j.Slf4j;

/**
 * 로그인 성공 후 실행되는 클래스
 * 로그인 후 어디로 이동할지, 메일 OAuth 연동 필요한지 여부 결정
 */
@Slf4j
@Component 
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    /**
     * 메일 관련 서비스
     * - Gmail 토큰 있는지 확인
     * - 없으면 OAuth 인증 필요
     */
    private final MailService mailService;

    public CustomAuthenticationSuccessHandler(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void onAuthenticationSuccess(
    		HttpServletRequest request
    		, HttpServletResponse response
    		, Authentication authentication
    		) throws IOException, ServletException {

        // 현재 로그인한 사용자 정보 꺼내기
        RealUserWrapper user = (RealUserWrapper) authentication.getPrincipal();
        AllUserVO allUser = user.getRealUser();

        // 직원인 경우에만 메일 OAuth 확인
        if (allUser instanceof EmployeeVO) {
            String empId = ((EmployeeVO) allUser).getEmpId();

            try {
                // Gmail 자동 로그인(토큰) 시도
                //mailService.tryAutoAuth(empId); -> 나중에 주석 제거

            } catch (NeedOAuthRedirectException e) {
                // Gmail 최초 사용자 → OAuth 인증 필요
                // Gmail 인증 화면으로 강제 이동
                getRedirectStrategy().sendRedirect(
                        request,
                        response,
                        "/mail/oauth/start?empId=" + empId
                );
                return;
            }
        }

        // 메일 인증 문제가 없으면 정상적인 로그인 성공 처리로 넘어감
        super.onAuthenticationSuccess(request, response, authentication);
    }

    /**
     * 로그인 성공 후 "어디 페이지로 보낼지" 결정하는 메서드
     */
    @Override
    protected String determineTargetUrl(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {

        Object principal = authentication.getPrincipal();

        // 사용자 객체인지 확인
        if (principal instanceof RealUserWrapper) {

            RealUserWrapper user = (RealUserWrapper) principal;
            AllUserVO allUser = user.getRealUser();

            String companyNo = null;

            if (allUser instanceof EmployeeVO) {
                EmployeeVO employee = (EmployeeVO) allUser;
                companyNo = employee.getCompanyNo();
            } else {
                log.warn("직원이 아닌 사용자 로그인");
            }

            // 로그인한 사용자의 권한 목록 가져오기
            Collection<? extends GrantedAuthority> authorities =
                    authentication.getAuthorities();

            boolean isEmployee = false;
            boolean isCompany = false;
            boolean isProvider = false;

            log.info(
                "현재 로그인된 사용자 권한: {}",
                SecurityContextHolder.getContext()
                                     .getAuthentication()
                                     .getAuthorities()
            );

            // 권한 하나씩 확인
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("EMPLOYEE")) {
                    isEmployee = true;
                }
                if (authority.getAuthority().equals("COMPANY")) {
                    isCompany = true;
                }
                if (authority.getAuthority().equals("PROVIDER")) {
                    isProvider = true;
                }
            }

            // 권한에 따라 이동할 페이지 결정
            if (isEmployee) {
                return "/" + companyNo + "/groupware";

            } else if (isCompany) {
                return "/";

            } else if (isProvider) {
                return "/";
            }
        }

        // 조건에 안 걸리면 기본 페이지
        return "/";
    }
}
