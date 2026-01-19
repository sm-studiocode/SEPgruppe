package kr.or.ddit.works.company.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.ddit.works.company.service.CompanyService;
import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.subscription.service.SubScriptionService;
import kr.or.ddit.works.subscription.vo.SubscriptionsVO;


@Controller
@RequestMapping("/company")
public class CompanyController {

	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private SubScriptionService subService;
	
	// 고객사정보 수정폼으로 이동
	@GetMapping("/mypage")
	public String updateCompanyFormUI(
			Authentication authentication
			, Model model
		    , HttpSession session

		) {
	    	//String companyNo = (String) session.getAttribute("companyNo");
	        String contactId = authentication.getName();

			CompanyVO member = companyService.selectCompany(authentication.getName());
			SubscriptionsVO subscription = subService.selectSubscription(contactId);
			
			model.addAttribute("member", member);
			model.addAttribute("subscription", subscription);
		return "sep:user/company/companyEdit";
	}
	
	// 고객사 정보 수정
	@PostMapping("/edit")
	public String updateCompany(
	        Authentication authentication,
	        @ModelAttribute("member") CompanyVO member
	) {
	    String contactId = authentication.getName();
	    member.setContactId(contactId);

	    // ✅ 서비스에서 (비번 변경 시에만) 인증 갱신까지 처리하니까
	    // 컨트롤러에서 재인증 갱신 로직 제거
	    companyService.updateCompany(member);

	    return "redirect:/company/mypage";
	}

	// 정보수정 전 비밀번호 확인 (모달)
	@PostMapping("/mypage/verifyPassword")
	public ResponseEntity<Object> verifyPassword(
	        Authentication authentication,
	        @RequestBody CompanyVO company
	) {
	    String contactId = authentication.getName();
	    boolean checkPw = companyService.authenticateMember(contactId, company.getContactPw());

	    if (checkPw) return ResponseEntity.ok().build();
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
}
