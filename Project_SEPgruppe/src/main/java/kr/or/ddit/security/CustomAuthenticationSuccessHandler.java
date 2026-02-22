package kr.or.ddit.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.login.vo.AllUserVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import kr.or.ddit.works.subscription.service.SubScriptionService;

// POST /login -> Security Filter -> SuccessHandler (session 생성) -> redirect
// -> 로그인 성공 직후 실행 -> 사용자 권한에 따라 어느 URL로 보낼지 결정
// -> 필요한 경우 세션을 생성하고 로그인한 사용자에게 companyNo를 세션에 저장

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String SESSION_KEY = "companyNo";

    @Autowired
    private SubScriptionService subscriptionService;

    // onAuthenticationSuccess : 로그인 성공 직후 1번만 호출
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. 로그인한 사용자 객체 꺼내기
        RealUserWrapper user = (RealUserWrapper) authentication.getPrincipal();
        AllUserVO allUser = user.getRealUser();

        // 2. 로그인 성공 시점에 사용자가 Employee일 경우 해당 계정의 companyNo를 가져와 'companyNo' 변수에 저장
        String companyNo = null;
        // EMPLOYEE는 EmployeeVO에서 companyNo
        if (allUser instanceof EmployeeVO) {
            companyNo = ((EmployeeVO) allUser).getCompanyNo();
        }
        // COMPANY는 CompanyVO에서 companyNo
        else if (allUser instanceof CompanyVO) {
            companyNo = ((CompanyVO) allUser).getCompanyNo();
        }

        // 3. companyNo가 Null이 아니고, 빈 문자열이 아니면 companyNo를 session에 저장
        if (companyNo != null && !companyNo.isEmpty()) {
            HttpSession session = request.getSession(true);
            session.setAttribute(SESSION_KEY, companyNo);
        }

        // 회사 계정이고, 활성 구독이면 ROLE_ADMIN을 “동적으로” 부여
        boolean isCompany = authentication.getAuthorities().stream()
                .anyMatch(a -> "COMPANY".equals(a.getAuthority()));

        if (isCompany) {
            // ✅ 핵심 변경: auth.getName()은 COMPANY에서 adminId가 될 수 있음
            // 구독 체크는 반드시 CONTACT_ID로 해야 함
            String contactId = SecurityContactIdUtil.resolveContactId(authentication);

            boolean active = subscriptionService.hasActiveSubscription(contactId);

            // 디버그 로그 (너가 원하던 거 제대로 찍히게)
            System.out.println("AUTH name=" + authentication.getName());
            System.out.println("AUTH roles=" + authentication.getAuthorities());
            System.out.println("SUB active=" + active);
            System.out.println("SUB contactId(used)=" + contactId);

            if (active) {
                // 기존 권한 복사 + ROLE_ADMIN 추가
                List<GrantedAuthority> newAuths = new ArrayList<>(authentication.getAuthorities());
                if (newAuths.stream().noneMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
                    newAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }

                // SecurityContext에 새 Authentication 주입
                UsernamePasswordAuthenticationToken newAuthentication =
                        new UsernamePasswordAuthenticationToken(
                                authentication.getPrincipal(),
                                authentication.getCredentials(),
                                newAuths
                        );

                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
                authentication = newAuthentication; // 아래 determineTargetUrl에서도 새 권한 기준으로 돌게
            }
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

        // 직원은 바로 그룹웨어
        if (isEmployee) return "/groupware";

        // 회사 계정이면 구독 상태 체크
        if (isCompany) {
            // ✅ 핵심 변경(여기도 동일): CONTACT_ID로 체크
            String contactId = SecurityContactIdUtil.resolveContactId(authentication);

            if (subscriptionService.hasActiveSubscription(contactId)) {
                return "/groupware";
            }
            return "/subscriptionPlan";
        }

        if (isProvider) return "/";

        return "/";
    }
}