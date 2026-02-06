package kr.or.ddit.works.organization.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import kr.or.ddit.works.organization.fancytree.DepartmentNode;
import kr.or.ddit.works.organization.fancytree.EmployeeNode;
import kr.or.ddit.works.organization.service.OrganizationService;
import kr.or.ddit.works.organization.vo.DepartmentVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;

/**
 * 조직도 관리 (organization) 컨트롤러
 * 
 * @author JYS
 * @since 2025. 3. 12.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 3. 12.     	JYS	          최초 생성
 *
 * </pre>
 */
@Controller
@RequestMapping("/organization")
public class OrganizationController {
	
	@Autowired
	private OrganizationService service;

    /** 조직도 전체 목록 조회 */
	@GetMapping("/admin/organizationList")
    public String selectListAllOrganization(
    	@SessionAttribute("companyNo") String companyNo
    	, Model model
    ) {
        model.addAttribute("companyNo", companyNo);
    	return "gw:organization/organizationList";
    }
    

    /** 조직도 내 사원 상세 조회 */
    @GetMapping("/{empId}/detail")
    public String selectOrganizationEmp(
    	@SessionAttribute("companyNo") String companyNo
    	, @PathVariable("empId") int empId
    ) {
        return "gw:organization/organizationDetail";
    }
    
    /**
     * 최상위 부서 조회
     * @return
     */
    @RequestMapping("admin/parentDep")
    @ResponseBody
    public List<DepartmentNode> parentDep(
    	@SessionAttribute("companyNo") String companyNo
    ){
    	List<DepartmentVO> parents = service.selectParentDep(companyNo);
    	return parents.stream().map(DepartmentNode::new)
				.toList();
    }
    
    /**
     * 하위 부서 조회 or 소속 사원 조회
     * @param mode
     * @param deptCd
     * @return
     */
    @RequestMapping("admin/childeDep")
    @ResponseBody
    public List<?> childDep(
    	@RequestParam("mode") String mode
    	, @RequestParam("parent") String deptCd
    	, @SessionAttribute("companyNo") String companyNo
    ){
    	if(mode.equals("employee")) {
			List<EmployeeVO> employee = service.selectEmployee(deptCd, companyNo);
			return employee.stream().map(EmployeeNode::new).toList();
		} else {
			List<DepartmentVO> childrens = service.selectChildDep(deptCd, companyNo);
			return childrens.stream().map(DepartmentNode::new).toList();
		}
    }
    
    @GetMapping("admin/childDepartments")
    @ResponseBody
    public List<DepartmentVO> getChildDepartments(
    	@RequestParam("parentDeptCd") String parentDeptCd
    	, @SessionAttribute("companyNo") String companyNo
    ) {
        return service.selectChildDepartments(companyNo, parentDeptCd);
    }
}
