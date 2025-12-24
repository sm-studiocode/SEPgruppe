package kr.or.ddit.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


public class CompanyInterceptor implements HandlerInterceptor {
	
	// preHandle() : 요청이 컨트롤러로 가기 전에 실행되는 메서드
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 1. session이 있다면 그 session을, session이 없다면 null을 반환
		// * HttpSession session = request.getSession();과 차이점
		// session이 없다면 새로운 session이 생성됨
		// 즉, 로그인이 되어있지 않은 회원에게도 임시 회원증을 발급해주는것과 같음
		HttpSession session = request.getSession(false);
        Object companyNo = (session != null) ? session.getAttribute("companyNo") : null; 
        		
        // companyNo가 존재하면, 요청 객체에 companyNo를 저장
        if (companyNo != null) {
            request.setAttribute("companyNo", companyNo);  // 이후 다른 곳에서 사용될 수 있도록 request에 저장
            
            // true를 반환하면, 요청이 계속해서 컨트롤러로 넘어가게 됨
            return true;
        }
        // companyNo가 없으면 로그인 페이지로 리다이렉트
        response.sendRedirect("/login");
        return false;
    }

	// postHandle() : 컨트롤러의 메서드 실행 후, 뷰가 렌더링 되기 전에 실행되는 메서드
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    	// ModelAndView 객체가 null이 아니고, companyNo가 null이 아닌 경우
        if (modelAndView != null && request.getAttribute("companyNo") != null) {
            // 컨트롤러에서 처리된 뷰로 돌아가기 전에, 모델에 companyNo를 추가
            modelAndView.addObject("companyNo", request.getAttribute("companyNo"));
        }
    }
}
