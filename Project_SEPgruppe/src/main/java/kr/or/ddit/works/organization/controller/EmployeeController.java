package kr.or.ddit.works.organization.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.or.ddit.paging.PaginationInfo;
import kr.or.ddit.paging.SimpleCondition;
import kr.or.ddit.works.organization.vo.OrganizationVO;
import kr.or.ddit.works.organization.vo.PositionVO;


@Controller
@RequestMapping("/employee")
public class EmployeeController {
	
	// 관리자페이지 - 멤버 통합관리
    @GetMapping("/admin/list")
    public String selectListAllEmployee(
    	@RequestParam(defaultValue = "1") int page
    	, @ModelAttribute SimpleCondition simpleCondition
    	, Model model
    ) {
    	PaginationInfo<OrganizationVO> paging = new PaginationInfo<>(10, 2);
        paging.setCurrentPage(page);
        paging.setSimpleCondition(simpleCondition);

        PaginationInfo<OrganizationVO> pageData = service.getAllEmployees(companyNo, paging);
        List<PositionVO> positionList = positionService.selectPositionList();
        
        model.addAttribute("pageData", pageData);
        model.addAttribute("simpleCondition", simpleCondition);
        model.addAttribute("positionList", positionList);

        return "gw:admin/employee/employeeList";
    }

}
