package kr.or.ddit.works.provider.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
		session.setAttribute("isAdmin", true); // 상단 메뉴바 동적으로 변경
		model.addAttribute("currentPage", "provider"); // detailHeader 동적으로 변경
		
		List<PaymentsVO> paymentList = payService.paymentList();
		String paymentListJson = objectMapper.writeValueAsString(paymentList);
	    model.addAttribute("paymentListJson", paymentListJson);
	    
	    List<SubscriptionsVO> subscriptionList = subService.subscriptionList();
	    String subscriptionListJson = objectMapper.writeValueAsString(subscriptionList);
	    model.addAttribute("subscriptionListJson", subscriptionListJson);
	    
	    List<CompanyVO> companyList = companyService.companyList();
	    String companyListJson = objectMapper.writeValueAsString(companyList);
	    model.addAttribute("companyListJson", companyListJson);
	    
		return "sep:provider/providerSettingForm";
	}
	
	// provicer의 고객사 관리 - 고객사 전체 목록 조회
	@GetMapping("/company")
	public String selectListAllCompany(Model model) {
		
		// 구독 리스트 전체 조회
		List<SubscriptionsVO> subscriptions = subService.subscriptionList();
		
		model.addAttribute("subscriptions", subscriptions);
		
		return "sep:admin/company/companyList";
	}
	
	// 관리자페이지 구독 플랜 관리 
	@GetMapping("/subscriptionPlan/manage")
	public String manageSubscriptionPlans(Model model) {
		List<SubscriptionPlansVO> planList = subService.readPlanList();
		model.addAttribute("planList", planList);
		return "sep:subscription/subscriptionPlanManage"; // JSP 페이지
	}

	// 관리자 페이지 구독 플랜 변경 저장 처리
	@PostMapping("/subscriptionPlan/manage/save")
	public String saveSubscriptionPlanChanges(SubscriptionPlansVO wrapper) {
		for (SubscriptionPlansVO plan : wrapper.getPlans()) {
			subService.updatePlanInfo(plan); // 가격 및 인원 업데이트
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
