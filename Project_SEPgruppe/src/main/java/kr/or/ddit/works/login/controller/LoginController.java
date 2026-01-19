package kr.or.ddit.works.login.controller;

import kr.or.ddit.works.login.exception.LoginException;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.validate.InsertGroup;
import kr.or.ddit.works.approval.controller.ApprDocAdminController;
import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.login.service.LoginService;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	@Autowired
	private LoginService service;

	// 회원가입 공통 실패 처리 (front)
	private String joinFail(Model model) {
		model.addAttribute("activeTab", "join");
		return "sep:login/loginForm";
	}
	
	// 로그인 및 회원가입 폼 이동
	@GetMapping("")
	public String loginFormUI(Model model) {
		CompanyVO company = new CompanyVO();

		model.addAttribute("company", company); 

		return "sep:login/loginForm";
	}
	
	// 회원가입 처리
	// CompanyVO, CompanyDivisionVO, LoginMapper.java, LoginMapper.xml
	// LoginService, LoginServiceImpl, loginForm.jsp, loginForm.js
	@PostMapping
	public String joinCompany(
		@Validated(InsertGroup.class) @ModelAttribute("company") CompanyVO company
		, BindingResult errors
		, RedirectAttributes redirectAttributes
		, Model model
		
	) {
		// 1. Bean Validation 실패 (VO @NotBlank)
		if(errors.hasErrors()) {
			return joinFail(model);
		}
		
		// 2. 비밀번호 일치 여부
		if(!company.getContactPw().equals(company.getConfirmPw())) {
			errors.rejectValue("confirmPw", "password.mismatch", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("activeTab", "join");
			return joinFail(model);
		}
		try {
			// 2. service 호출
			service.joinCompany(company);
			
		} catch (LoginException e) {
			// 3. DB 검증 (ID 중복확인)
			errors.rejectValue("contactId", "duplicate", e.getMessage());
			return joinFail(model);

		}
		//3. 모든 검증 통과
		return "redirect:/";
	}
	
	// 아이디 찾기 폼 이동
	@GetMapping("/findId")
	public String findIdForm() {
		return "sep:login/findId";
	}
	
	// 아이디 찾기 
	@PostMapping("/findId")
	public ResponseEntity<Map<String, Object>> findIdProcess(
		@RequestBody CompanyVO company
	){
		String contactId = service.findContactId(company);

	    return ResponseEntity.ok(Map.of("success", true, "contactId", contactId));
	}
	
	// 비밀번호 찾기 폼 이동
	@GetMapping("/findPw")
	public String findPwForm() {
	    return "sep:login/findPw";
	}

	// 비밀번호 찾기 - 계정 검증 단계
	@PostMapping("/findPw")
	public ResponseEntity<Map<String, Object>> findPwProcess(
		@RequestBody CompanyVO company
		, HttpSession session
	){
		String contactId = service.findContactId(company);

		session.setAttribute("pwResetContactId", contactId);
		
		return ResponseEntity.ok(Map.of("success", true, "contactId", contactId));
	}
	
	// 비밀번호 변경
	@PostMapping("/updatePw")
	public String updatePw(
		@RequestParam String contactPw
		, HttpSession session
		, RedirectAttributes redirectAttributes
	) {
		String contactId = (String) session.getAttribute("pwResetContactId");
		
		if(contactId == null) {
			// 사용자가 URL을 직접 입력
			// 새로고침 / 뒤로가기 / 세션 만료
			// 위 CASE를 방지하기 위한 ERROR 코드
			redirectAttributes.addFlashAttribute("error", "비정상적인 접근입니다.");
		    return "redirect:/login/findPw";

		}else {
			CompanyVO company = new CompanyVO();
			company.setContactId(contactId);
			company.setContactPw(contactPw);
			
			service.updateContactPw(company);
			
			// 1회용 비밀번호 변경 토큰 제거
			// 사용자가 새로고침 혹은 뒤로가기 해도 비밀번호 다시 변경 불가
			session.removeAttribute("pwResetContactId");
			
			redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
		}
		return "redirect:/login";
	}
	
	// 회원가입 시 이메일 인증
	@PostMapping("/join/mail/send")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> sendJoinMail(
	        @RequestParam String email,
	        HttpSession session
	) {
	    service.sendJoinMailAuthCode(email, session);
	    return ResponseEntity.ok(Map.of("success", true));
	}

	@PostMapping("/join/mail/verify")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> verifyJoinMail(
	        @RequestParam String email,
	        @RequestParam String code,
	        HttpSession session
	) {
	    // 이메일도 같이 받아서 "인증한 이메일 != 지금 입력 이메일" 방지
	    // (서비스가 session에 email 저장해두는 구조라서 같이 맞춰야 함)
	    boolean ok = service.checkJoinMailAuthCode(code, session);
	    return ResponseEntity.ok(Map.of("success", ok));
	}
}
	

