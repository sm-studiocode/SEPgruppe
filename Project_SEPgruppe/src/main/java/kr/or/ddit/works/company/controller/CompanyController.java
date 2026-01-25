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
import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/company")
@Slf4j
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
			// 1. 로그인한 사용자 정보 가져오기
	        String contactId = authentication.getName();

	        // 2. 고객사 정보 조회
			CompanyVO member = companyService.selectCompany(authentication.getName());
			
			// 3. 구독 정보 조회
			SubscriptionsVO subscription = subService.selectSubscription(contactId);
			
			// 4. 모델에 정보 담아 companyEdit.jsp로 보내기
			model.addAttribute("member", member);
			model.addAttribute("subscription", subscription);
			
		return "sep:user/company/companyEdit";
	}
	
	// 정보수정 전 비밀번호 확인 (모달)
	@PostMapping("/mypage/verifyPassword")
	public ResponseEntity<Object> verifyPassword(
	        Authentication authentication
	        , @RequestBody CompanyVO company
	) {
		// 1. 로그인한 ID 가져오기
	    String contactId = authentication.getName();
	    
	    // 2. Service로직에서 사용자가 입력한 비밀번호 검증
	    boolean checkPw = companyService.authenticateMember(contactId, company.getContactPw());

	    // 3. 결과를 HTTP 상태코드로 반환
	    if (checkPw) return ResponseEntity.ok().build(); // 성공 : 200
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 실패 : 401
	}
	
	// 고객사 정보 수정 처리
	@PostMapping("/edit")
	public String updateCompany(
	        Authentication authentication
	        , @ModelAttribute("member") CompanyVO member
	) {
		// 1. 로그인한 사용자 정보 가져오기
	    String contactId = authentication.getName();
	    member.setContactId(contactId);
	    
	    // 2. 정보 수정 실행
	    companyService.updateCompany(member);

	    // 3. 정보 수정 완료 후 마이페이지 이동
	    return "redirect:/company/mypage";
	}



}
