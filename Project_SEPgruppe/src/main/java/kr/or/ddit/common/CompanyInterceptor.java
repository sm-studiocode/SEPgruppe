package kr.or.ddit.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class CompanyInterceptor implements HandlerInterceptor {

    private static final String SESSION_KEY = "companyNo";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        String companyNo = (session != null) ? (String) session.getAttribute(SESSION_KEY) : null;

        if (companyNo != null && !companyNo.isEmpty()) {
            request.setAttribute("companyNo", companyNo);
            return true;
        }

        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {

        if (modelAndView == null) return;

        Object companyNo = request.getAttribute("companyNo");
        if (companyNo != null) {
            modelAndView.addObject("companyNo", companyNo);
        }
    }
}
