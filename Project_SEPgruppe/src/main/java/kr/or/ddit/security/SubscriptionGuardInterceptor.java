package kr.or.ddit.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import kr.or.ddit.works.subscription.service.SubScriptionService;

@Component
public class SubscriptionGuardInterceptor implements HandlerInterceptor {

    private final SubScriptionService subscriptionService;

    public SubscriptionGuardInterceptor(SubScriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 비로그인(anonymous) 이거나 인증정보 없으면 통과
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return true;
        }

        boolean isCompany = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("COMPANY") || a.getAuthority().equals("ROLE_COMPANY"));


        // 회사 계정만 구독 체크 
        if (!isCompany) return true;

        String contactId = auth.getName();

        // 활성 구독이면 통과
        if (subscriptionService.hasActiveSubscription(contactId)) {
            return true;
        }

        // 미구독이면 플랜 페이지로
        response.sendRedirect(request.getContextPath() + "/subscriptionPlan");
        return false;
    }
}
