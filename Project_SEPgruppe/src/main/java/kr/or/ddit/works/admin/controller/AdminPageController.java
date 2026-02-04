package kr.or.ddit.works.admin.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/adminpage")
public class AdminPageController {
		
	@Autowired
	private AdminPageService service;
	
	@GetMapping
	public String adminPage(HttpSession session, Model model) {
	    String companyNo = (String) session.getAttribute("companyNo");
	    if (companyNo == null || companyNo.isBlank()) {
	        return "redirect:/login";
	    }

	    Map<String, Object> data = service.getAdminpageDate(companyNo);
	    model.addAttribute("companyNo", companyNo);
	    model.addAllAttributes(data);
	    return "gw:admin/admin/adminPage";
	}

}
