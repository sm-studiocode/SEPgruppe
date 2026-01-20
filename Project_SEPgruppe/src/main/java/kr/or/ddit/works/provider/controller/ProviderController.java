package kr.or.ddit.works.provider.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.or.ddit.works.company.service.CompanyService;
import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.subscription.service.PaymentService;
import kr.or.ddit.works.subscription.service.SubScriptionService;
import kr.or.ddit.works.subscription.vo.PaymentsVO;
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
}
