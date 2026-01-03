package kr.or.ddit.works.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
		
	) {
		if(errors.hasErrors()) {
			// 검증 실패
			redirectAttributes.addFlashAttribute("company", company);
			
			redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "company", errors);
			
	        return "redirect:/login";
	        
		}else {
			
			//검증 성공
			service.joinCompany(company);
			return "redirect:/";
		}
	}
		
}
	

