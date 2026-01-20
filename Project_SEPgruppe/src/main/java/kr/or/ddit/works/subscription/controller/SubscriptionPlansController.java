package kr.or.ddit.works.subscription.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.ddit.works.subscription.service.SubScriptionService;
import kr.or.ddit.works.subscription.vo.SubscriptionPlansVO;

@Controller
@RequestMapping("/subscriptionPlan")
public class SubscriptionPlansController {
	@Autowired
	private SubScriptionService service;
	
	// 구독 플랜 목록 조회
	@GetMapping("")
	public String selectListAllSubscriptionPlan(Model model) {
		List<SubscriptionPlansVO> planList = service.readPlanList();
		model.addAttribute("planList", planList);
		model.addAttribute("currentPage", "subscription"); // detailHeader 동적으로 변경
		return "sep:subscription/subscriptionPlanList";
	}

	// 구독 플랜 상세 조회
	@GetMapping("{planType}")
	public String selectSubscriptionPlanDetail(@PathVariable String planType, Model model) {
		SubscriptionPlansVO plan = service.planOne(planType);
		model.addAttribute("plan", plan);
		return "sepgruppe/subscription/subscriptionPlanDetail";
	}
	
	// 관리자페이지 구독 플랜 관리 
	@GetMapping("manage")
	public String manageSubscriptionPlans(Model model) {
		List<SubscriptionPlansVO> planList = service.readPlanList();
		model.addAttribute("planList", planList);
		return "sep:subscription/subscriptionPlanManage"; // JSP 페이지
	}

	// 관리자 페이지 구독 플랜 변경 저장 처리
	@PostMapping("/manage/save")
	public String saveSubscriptionPlanChanges(SubscriptionPlansVO wrapper) {
		for (SubscriptionPlansVO plan : wrapper.getPlans()) {
			service.updatePlanInfo(plan); // 가격 및 인원 업데이트
		}
		return "redirect:/subscriptionPlan/manage";
	}
}
