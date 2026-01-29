package kr.or.ddit.works.provider.controller;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.or.ddit.works.company.service.CompanyService;
import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.subscription.service.PaymentService;
import kr.or.ddit.works.subscription.service.SubScriptionService;
import kr.or.ddit.works.subscription.vo.PaymentsVO;
import kr.or.ddit.works.subscription.vo.SubscriptionPlansVO;
import kr.or.ddit.works.subscription.vo.SubscriptionsVO;

@Controller
@RequestMapping("/provider")
public class ProviderController {

	// JSP 차트/테이블을 JS로 그리기 위한 JSON 문자열 변환
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private PaymentService payService;
	
	@Autowired
	private SubScriptionService subService;
	
	@Autowired
	private CompanyService companyService;
	
	// 관리자모드 - 대시보드
	@GetMapping("")
	public String providerFormUI(HttpSession session, Model model) throws JsonProcessingException {
		
		// 1. 결제 목록 조회 -> JSON으로 변환하여 MODEL에 담기
		List<PaymentsVO> paymentList = payService.paymentList();
		String paymentListJson = objectMapper.writeValueAsString(paymentList);
	    model.addAttribute("paymentListJson", paymentListJson);
	    
	    // 2. 구독 목록 조회 -> JSON으로 변환하여 MODEL에 담기
	    List<SubscriptionsVO> subscriptionList = subService.subscriptionList();
	    String subscriptionListJson = objectMapper.writeValueAsString(subscriptionList);
	    model.addAttribute("subscriptionListJson", subscriptionListJson);
	    
	    // 3. 회사 목록 조회 -> JSON으로 변환하여 MODEL에 담기
	    List<CompanyVO> companyList = companyService.companyList();
	    String companyListJson = objectMapper.writeValueAsString(companyList);
	    model.addAttribute("companyListJson", companyListJson);
	    
		return "sep:provider/providerSettingForm";
	}
	
	// provider의 고객사 관리 - 고객사 전체 목록 조회
	@GetMapping("/company")
	public String selectListAllCompany(Model model) {
		
		// 구독 리스트 전체 조회
		List<SubscriptionsVO> subscriptions = subService.subscriptionList();
		
		model.addAttribute("subscriptions", subscriptions);
		
		return "sep:admin/company/companyList";
	}
	
	// provider의 고객사 관리 - 고객사 결제 이력 조회(JSON)
	@GetMapping("/company/{contactId}/payments")
	@ResponseBody
	public List<PaymentsVO> paymentsByCompany(@PathVariable String contactId) {
	    return payService.paymentListByContactId(contactId);
	}
	
	// provider의 고객사 관리 - 고객사 구독 해지(UPDATE)
	@PostMapping("/company/{contactId}/cancel")
	@ResponseBody
	public ResponseEntity<?> cancelCompanySubscription(@PathVariable String contactId) {
	    try {
	        subService.cancelSubscription(contactId); // 여기서 포트원까지 끊게 만들거임
	        return ResponseEntity.ok(java.util.Map.of("success", true));
	    } catch (IllegalStateException e) {
	        return ResponseEntity.badRequest()
	                .body(java.util.Map.of("success", false, "message", e.getMessage()));
	    } catch (IOException e) {
	        return ResponseEntity.internalServerError()
	                .body(java.util.Map.of("success", false, "message", "포트원 통신 실패: " + e.getMessage()));
	    }
	}
	
	// 관리자페이지 구독 플랜 관리 
	@GetMapping("/subscriptionPlan/manage")
	public String manageSubscriptionPlans(Model model) {
		List<SubscriptionPlansVO> planList = subService.readPlanList();
		model.addAttribute("planList", planList);
		return "sep:subscription/subscriptionPlanManage"; 
	}

	// 관리자 페이지 구독 플랜 변경 저장 처리
	@PostMapping("/subscriptionPlan/manage/save")
	public String saveSubscriptionPlanChanges(SubscriptionPlansVO wrapper) {
		for (SubscriptionPlansVO plan : wrapper.getPlans()) {
			subService.updatePlanInfo(plan); 
		}
		return "redirect:/provider/subscriptionPlan/manage";
	}
	
    // 관리자페이지 자동결제관리
	@GetMapping("/payment")
	public String selectListAllPayment(Model model) {
		List<PaymentsVO> paymentList = payService.paymentList();
		model.addAttribute("paymentList", paymentList);
		return "sep:admin/payment/paymentList";
	}
}
