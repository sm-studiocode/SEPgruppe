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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService service;

    private String joinFail(Model model) {
        model.addAttribute("activeTab", "join");
        return "sep:login/loginForm";
    }

    @GetMapping("")
    public String loginFormUI(Model model) {
        model.addAttribute("company", new CompanyVO());
        return "sep:login/loginForm";
    }

    @PostMapping
    public String joinCompany(
            @Validated(InsertGroup.class) @ModelAttribute("company") CompanyVO company,
            BindingResult errors,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (errors.hasErrors()) return joinFail(model);

        if (!company.getContactPw().equals(company.getConfirmPw())) {
            errors.rejectValue("confirmPw", "password.mismatch", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("activeTab", "join");
            return joinFail(model);
        }

        try {
            service.joinCompany(company);
        } catch (LoginException e) {
            errors.rejectValue("contactId", "duplicate", e.getMessage());
            return joinFail(model);
        }

        return "redirect:/";
    }

    @GetMapping("/findId")
    public String findIdForm() {
        return "sep:login/findId";
    }

    @PostMapping("/findId")
    public ResponseEntity<Map<String, Object>> findIdProcess(@RequestBody CompanyVO company) {
        String contactId = service.findContactId(company);
        return ResponseEntity.ok(Map.of("success", true, "contactId", contactId));
    }

    @GetMapping("/findPw")
    public String findPwForm() {
        return "sep:login/findPw";
    }

    // ✅ 비밀번호 찾기: 입력값 검증 후 임시비번 발급/메일발송
    @PostMapping("/findPw")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> findPwProcess(@RequestBody CompanyVO company) {
        service.issueTempPassword(company);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 회원가입 이메일 인증은 유지
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
        boolean ok = service.checkJoinMailAuthCode(code, session);
        return ResponseEntity.ok(Map.of("success", ok));
    }
}
