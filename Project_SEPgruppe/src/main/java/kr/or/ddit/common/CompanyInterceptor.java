package kr.or.ddit.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

// GET /groupware -> Interceptor (companyNo 체크) -> Controller -> View
// -> 로그인 성공 후 사용자가 어떤 URL 요청 시 컨트롤러 실행 전에 먼저 실행
// -> 이 때 세션에서 companyNo를 꺼내서 있으면 request/model에 담아 컨트롤러와 JSP에서 쓸 수 있게 함
// -> 세션에 companyNo가 없으면 로그인으로 보내서 요청을 막음

@Component
public class CompanyInterceptor implements HandlerInterceptor {

    private static final String SESSION_KEY = "companyNo";

    // preHandle : 컨트롤러 실 행 전 무조건 호출
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    	// 1. session 가져오기 (false : 세션 없으면 새로 만들지 않음)
    	// * session은 CustomAuthenticationSuccessHandler에서 생성됨
        HttpSession session = request.getSession(false);
        
        // 2. session이 있으면 companyNo를 session에서 가져와 companyNo에 저장
        String companyNo = (session != null) ? (String) session.getAttribute(SESSION_KEY) : null;

        // 3. companyNo가 있고, 빈 문자열이 아닌 경우 컨트롤러와 JSP에서 사용할 수 있도록 request에 companyNo를 저장
        if (companyNo != null && !companyNo.isEmpty()) {
            request.setAttribute("companyNo", companyNo);
            return true;
        }

        // 4. 3번 조건에 충족되지 않으면 컨트롤러 실행 하지 않고 로그인 페이지로 이동
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }

    // postHandle : 컨트롤러 실행 후 JSP 가기 직전에 호출
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {

    	// 1. REST API, redirect, @ResponseBody일 경우 (즉, 뷰가 없으면 호출 종료)
        if (modelAndView == null) return;

        // 2. 뷰가 있으면 request에서 companyNo 꺼냄
        Object companyNo = request.getAttribute("companyNo");
        
        // 3. JSP에서 사용 가능하도록 Model에 저장
        if (companyNo != null) {
            modelAndView.addObject("companyNo", companyNo);
        }
    }
}
