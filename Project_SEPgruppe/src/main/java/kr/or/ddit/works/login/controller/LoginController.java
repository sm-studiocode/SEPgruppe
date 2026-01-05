package kr.or.ddit.works.login.controller;

import kr.or.ddit.works.login.exception.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.validate.InsertGroup;
import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.login.service.LoginService;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	@Autowired
	private LoginService service;
	
	
	// 공통 실패 처리 (front)
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
}
	

