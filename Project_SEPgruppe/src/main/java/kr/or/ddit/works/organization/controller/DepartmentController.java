package kr.or.ddit.works.organization.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.works.organization.service.DepartmentService;
import kr.or.ddit.works.organization.vo.DepartmentVO;

/**
 * 부서 컨트롤러 
 * 
 * @author JYS
 * @since 2025. 3. 13.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 3. 13.     	JYS	          최초 생성
 *
 * </pre>
 */
@Controller
@RequestMapping("/department")
public class DepartmentController {
	
	@Autowired
	private DepartmentService service;
	
    /** 부서 전체 조회 */
    @GetMapping("")
    public String selectListAllDepartment(
    	@SessionAttribute("companyNo") String companyNo
    	, Model model
    ) {
    	model.addAttribute("companyNo", companyNo);
        return "gw:department/departmentList";
    }

    // 관리자 - 조직 추가
    @PostMapping("new/dept")
    @ResponseBody
    public int addDepartment(
    	@RequestBody DepartmentVO department
    	, @SessionAttribute("companyNo") String companyNo
    ) {
        department.setCreateAt(LocalDate.now().toString());
        department.setCompanyNo(companyNo);
        return service.addDepartment(department);
    } 
    
    // 부서 개별 수정 
    @PatchMapping("updateField")
    @ResponseBody
    public int updateField(
    	@RequestBody DepartmentVO dept
    	, @SessionAttribute("companyNo") String companyNo
    ) {
    	dept.setCompanyNo(companyNo);
        return service.updateDepartmentField(dept); // 어떤 필드가 왔는지 판단 후 update
    }
    
    @DeleteMapping("delete/{deptCd}")
    @ResponseBody
    public int deleteDepartment(
    	@SessionAttribute("companyNo") String companyNo
    	, @PathVariable("deptCd") String deptCd
    ) {
    	return service.deleteDepartment(companyNo, deptCd);
    }
    
    @GetMapping("/bulkInsertForm")
    public String showBulkInsertForm(
    	@SessionAttribute("companyNo") String companyNo
    	, Model model
    ) {
    	model.addAttribute("companyNo", companyNo);
        return "gw:admin/organization/deptBulkRegist"; // 엑셀 업로드 폼 JSP 페이지
    }
    
    @PostMapping("/bulkInsertExcel")
    @ResponseBody
    public List<DepartmentVO> bulkInsertFromExcelAjax(
    	@RequestParam("file") MultipartFile file
    	, @SessionAttribute("companyNo") String companyNo
    ) {
        try {
            List<DepartmentVO> deptList = service.parseExcel(file, companyNo);
            service.bulkInsertDepartments(deptList); // status 필드 세팅 포함
            return deptList;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
