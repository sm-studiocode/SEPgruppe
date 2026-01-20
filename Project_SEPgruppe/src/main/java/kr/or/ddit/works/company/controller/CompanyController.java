package kr.or.ddit.works.company.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

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
import kr.or.ddit.works.subscription.service.PaymentService;
import kr.or.ddit.works.subscription.service.SubScriptionService;
import kr.or.ddit.works.subscription.vo.PaymentsVO;
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
	
	@Autowired
	private PaymentService paymentService;

	
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
	
	// provicer의 고객사 관리 - 고객사 전체 목록 조회
	@GetMapping("")
	public String selectListAllCompany(Model model) {
		model.addAttribute("currentPage", "customer"); // detailHeader 동적으로 변경
		
		// 구독 리스트 함께 조회
		List<SubscriptionsVO> subscriptions = subService.subscriptionList();
		
		// 결제 내역을 각 구독 객체에 주입 
	    for (SubscriptionsVO sub : subscriptions) {
	        List<PaymentsVO> payments = paymentService.selectPaymentsBySubscriptionNo(sub.getSubscriptionNo());
	        sub.setPayment(payments);
	    }
		
		// 날짜 파싱 처리
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (SubscriptionsVO vo : subscriptions) {
		    try {
		        if (vo.getSubscriptionStart() != null) {
		            vo.setSubscriptionStartDate(sdf.parse(vo.getSubscriptionStart()));
		        }
		        if (vo.getSubscriptionEnd() != null) {
		            vo.setSubscriptionEndDate(sdf.parse(vo.getSubscriptionEnd()));
		        }
		    } catch (ParseException e) {
		        log.error("날짜 파싱 오류: {}", e.getMessage());
		    }
		}
		
		model.addAttribute("subscriptions", subscriptions);
		
		return "sep:admin/company/companyList";
	}
}
