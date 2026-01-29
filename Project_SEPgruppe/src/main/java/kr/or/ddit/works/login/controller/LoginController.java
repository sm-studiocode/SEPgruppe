package kr.or.ddit.works.login.controller;

import kr.or.ddit.works.login.exception.LoginException;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import kr.or.ddit.validate.InsertGroup;
import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.login.service.LoginService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/login")
public class LoginController {

	// 회원가입 시 이메일 인증 여부 확인을 위한 상수
	private static final String JOIN_MAIL_VERIFIED = "JOIN_MAIL_VERIFIED";

    @Autowired
    private LoginService service;

    // 회원가입 Form 이동
    @GetMapping("")
    public String loginFormUI(Model model) {
        model.addAttribute("company", new CompanyVO());
        return "sep:login/loginForm";
    }

    // 회원가입 실패 시 회원가입 탭 유지를 위한 메서드
    private String joinFail(Model model, HttpSession session) {
        model.addAttribute("activeTab", "join");
        model.addAttribute("mailVerified", session.getAttribute(JOIN_MAIL_VERIFIED));
        model.addAttribute("verifiedEmail", session.getAttribute("JOIN_MAIL_EMAIL")); 

        return "sep:login/loginForm";
    }
    
    // 회원가입 시 이메일 인증 (메일발송)
    @PostMapping("/join/mail/send")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendJoinMail(
            @RequestParam String email,
            HttpSession session
    ) {
        service.sendJoinMailAuthCode(email, session);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 회원가입 시 이메일 인증 (인증번호 검증)
    @PostMapping("/join/mail/verify")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifyJoinMail(
            @RequestParam String email,
            @RequestParam String code,
            HttpSession session
    ) {
        boolean ok = service.checkJoinMailAuthCode(email, code, session);
        return ResponseEntity.ok(Map.of("success", ok));
    }
    
    // 회원가입 처리
    @PostMapping
    public String joinCompany(
            @Validated(InsertGroup.class) @ModelAttribute("company") CompanyVO company,
            BindingResult errors,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpSession session

    ) {
    	// 1. session에서 이메일 인증여부 확인
        Object verified = session.getAttribute(JOIN_MAIL_VERIFIED);
        if (!(verified instanceof Boolean) || !((Boolean) verified)) {
            errors.reject("mail.notVerified", "이메일 인증을 완료해야 회원가입이 가능합니다.");
            return joinFail(model, session);
        }
        
        // 2. 회원가입 제출 시 서버가 인증 성공한 이메일 주소와 현재 입력된 이메일주소가 일치하는지 확인
        String verifiedEmail = (String) session.getAttribute("JOIN_MAIL_EMAIL");
        if (verifiedEmail == null || company.getContactEmail() == null ||
            !verifiedEmail.equals(company.getContactEmail().trim())) {
            session.setAttribute(JOIN_MAIL_VERIFIED, false);
            errors.reject("mail.notVerified", "인증한 이메일과 입력한 이메일이 다릅니다. 다시 인증해주세요.");
            return joinFail(model, session); 
        }
        
        // 3. Bean Validation 에러 시 회원가입 실패
        if (errors.hasErrors()) return joinFail(model, session);

        // 4. 입력한 비밀번호 일치하는지 검증
        if (!company.getContactPw().equals(company.getConfirmPw())) {
            errors.rejectValue("confirmPw", "password.mismatch", "비밀번호가 일치하지 않습니다.");
            return joinFail(model, session);
        }

        // 5. 회원가입 처리 + 예외 처리
        try {
            service.joinCompany(company);
        } catch (LoginException e) {
            errors.rejectValue("contactId", "join.fail", e.getMessage());
            return joinFail(model, session);
        }

        return "redirect:/";
    }

    // 아이디 찾기 Form
    @GetMapping("/findId")
    public String findIdForm() {
        return "sep:login/findId";
    }

    // 아이디 찾기 처리
    @PostMapping("/findId")
    public ResponseEntity<Map<String, Object>> findIdProcess(@RequestBody CompanyVO company) {
        String contactId = service.findContactId(company);
        return ResponseEntity.ok(Map.of("success", true, "contactId", contactId));
    }

    // 패스워드 찾기 Form
    @GetMapping("/findPw")
    public String findPwForm() {
        return "sep:login/findPw";
    }

    // 패스워드 찾기 처리
    @PostMapping("/findPw")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> findPwProcess(@RequestBody CompanyVO company) {
        service.issueTempPassword(company);
        return ResponseEntity.ok(Map.of("success", true));
    }

}
