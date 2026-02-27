package kr.or.ddit.works.organization.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import kr.or.ddit.paging.PaginationInfo;
import kr.or.ddit.paging.SimpleCondition;
import kr.or.ddit.works.mybatis.mappers.EmpRoleMapper;
import kr.or.ddit.works.mybatis.mappers.OrganizationMapper;
import kr.or.ddit.works.organization.service.EmployeeService;
import kr.or.ddit.works.organization.service.PositionService;
import kr.or.ddit.works.organization.vo.DepartmentVO;
import kr.or.ddit.works.organization.vo.EmpRoleGrantRequest;
import kr.or.ddit.works.organization.vo.EmpRoleVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import kr.or.ddit.works.organization.vo.OrganizationVO;
import kr.or.ddit.works.organization.vo.PositionVO;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PositionService positionService;

    // ✅ 권한 관리용 추가
    @Autowired
    private EmpRoleMapper empRoleMapper;

    // ✅ "같은 회사 소속 직원인지" 검증용(너 프로젝트에 이미 존재한다고 가정)
    @Autowired
    private OrganizationMapper organizationMapper;

    // =========================
    // ✅ 권한 체크 헬퍼
    // =========================
    private boolean hasAuthority(Authentication auth, String role) {
        if (auth == null) return false;
        for (GrantedAuthority a : auth.getAuthorities()) {
            if (role.equals(a.getAuthority())) return true;
        }
        return false;
    }

    private boolean isTenantAdmin(Authentication auth) {
        // transitional: ROLE_ADMIN도 테넌트관리자로 인정(나중에 DB 정리되면 ROLE_ADMIN 제거 가능)
        return hasAuthority(auth, "ROLE_TENANT_ADMIN") || hasAuthority(auth, "ROLE_ADMIN");
    }

    /** 관리자 - 전사 인사정보 조회 */
    @GetMapping("/admin/list")
    public String selectListAllEmployee(
        @SessionAttribute("companyNo") String companyNo,
        @RequestParam(defaultValue = "1") int page,
        @ModelAttribute SimpleCondition simpleCondition,
        Model model
    ) {
        PaginationInfo<OrganizationVO> paging = new PaginationInfo<>(10, 2);
        paging.setCurrentPage(page);
        paging.setSimpleCondition(simpleCondition);

        PaginationInfo<OrganizationVO> pageData = employeeService.getAllEmployees(companyNo, paging);
        List<PositionVO> positionList = positionService.selectPositionList();

        model.addAttribute("pageData", pageData);
        model.addAttribute("simpleCondition", simpleCondition);
        model.addAttribute("positionList", positionList);

        return "gw:admin/employee/employeeList";
    }

    /** 관리자 - 인사 정보 등록 */
    @PostMapping("/admin/new")
    @ResponseBody
    public ResponseEntity<?> insertEmployee(
        @SessionAttribute("companyNo") String companyNo,
        @ModelAttribute EmployeeVO member
    ) {
        member.setCompanyNo(companyNo);
        int result = employeeService.insertEmployee(member);

        return result > 0 ? ResponseEntity.ok().build()
                          : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /** 관리자 - 일괄 수정 */
    @PutMapping("/admin/bulkUpdate")
    @ResponseBody
    public ResponseEntity<?> bulkUpdate(
        @SessionAttribute("companyNo") String companyNo,
        @RequestBody Map<String, Object> payload
    ) {
        List<String> empIds = (List<String>) payload.get("empIds");
        String fieldType = (String) payload.get("fieldType");
        String value = (String) payload.get("value");

        int updatedCount = employeeService.bulkUpdateEmployees(empIds, fieldType, value);
        return updatedCount > 0 ? ResponseEntity.ok().build()
                                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /** 관리자 - 삭제 */
    @DeleteMapping("/admin/delete")
    @ResponseBody
    public ResponseEntity<?> deleteEmployees(
        @SessionAttribute("companyNo") String companyNo,
        @RequestBody List<String> empIds
    ) {
        int deletedCount = employeeService.deleteEmployees(empIds, companyNo);
        return deletedCount > 0 ? ResponseEntity.ok().build()
                                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /** 관리자 - ajax 리스트 */
    @GetMapping("/admin/ajaxList")
    @ResponseBody
    public Map<String, Object> getAjaxEmployeeList(
        @SessionAttribute("companyNo") String companyNo,
        @RequestParam Map<String, String> param
    ) {
        int start = Integer.parseInt(param.get("start"));
        int length = Integer.parseInt(param.get("length"));
        int page = (start / length) + 1;

        SimpleCondition condition = new SimpleCondition();
        condition.setSearchType(param.get("searchType"));
        condition.setSearchWord(param.get("searchWord"));

        PaginationInfo<OrganizationVO> paging = new PaginationInfo<>(length, 2);
        paging.setCurrentPage(page);
        paging.setSimpleCondition(condition);

        paging = employeeService.getAllEmployees(companyNo, paging);

        Map<String, Object> result = new HashMap<>();
        result.put("draw", Integer.parseInt(param.get("draw")));
        result.put("recordsTotal", paging.getTotalRecord());
        result.put("recordsFiltered", paging.getTotalRecord());
        result.put("data", paging.getDataList());
        return result;
    }

    /** 관리자 - 엑셀 다운로드 */
    @GetMapping("/admin/excelDownload")
    public void downloadExcel(
        @SessionAttribute("companyNo") String companyNo,
        @RequestParam(required = false) String searchType,
        @RequestParam(required = false) String searchWord,
        HttpServletResponse response
    ) throws IOException {
        // 1. 전체사원 및 검색 결과 가져오기
        PaginationInfo<OrganizationVO> paging = new PaginationInfo<>(Integer.MAX_VALUE, 1); // 모든 데이터
        paging.setCurrentPage(1);

        SimpleCondition condition = new SimpleCondition();
        condition.setSearchType(searchType);
        condition.setSearchWord(searchWord);
        paging.setSimpleCondition(condition);

        paging = employeeService.getAllEmployees(companyNo, paging);
        List<OrganizationVO> data = paging.getDataList();

        // 엑셀 파일 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("사원 리스트");

        // 헤더
        Row headerRow = sheet.createRow(0);
        String[] headers = {"사번", "이름", "부서", "직책", "이메일"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 데이터
        for (int i = 0; i < data.size(); i++) {
            OrganizationVO emp = data.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(emp.getEmpNo());
            row.createCell(1).setCellValue(emp.getEmpNm());
            row.createCell(2).setCellValue(emp.getDeptName());
            row.createCell(3).setCellValue(emp.getPositionName());
            row.createCell(4).setCellValue(emp.getEmpEmail());
        }

        // 동적 파일명 생성
        String fileName = companyNo;
        if (StringUtils.isNotBlank(searchType) && StringUtils.isNotBlank(searchWord)) {
            switch (searchType) {
                case "empNm":
                    fileName += "_이름검색_" + searchWord;
                    break;
                case "deptName":
                    fileName += "_부서검색_" + searchWord;
                    break;
                case "positionName":
                    fileName += "_직책검색_" + searchWord;
                    break;
            }
        }

        // 날짜 추가
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        fileName += "_" + today + ".xlsx";

        // 파일명 인코딩 (한글 깨짐 방지)
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

        // 응답 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", contentDisposition);

        // 파일 출력
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // 부서 목록 조회
    @GetMapping("/departments")
    @ResponseBody
    public List<DepartmentVO> getDepartments(@SessionAttribute("companyNo") String companyNo) {
        return employeeService.selectDepartments(companyNo);
    }

    // =========================
    // ✅ 권한 부여/회수 API 추가
    // =========================

    /**
     * 특정 직원 권한 목록 조회
     * GET /employee/admin/roles?empId=xxx
     */
    @GetMapping("/admin/roles")
    @ResponseBody
    public ResponseEntity<?> getRoles(
        @SessionAttribute("companyNo") String companyNo,
        @RequestParam("empId") String empId,
        Authentication authentication
    ) {
        if (!isTenantAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("NOT_TENANT_ADMIN");
        }

        // ✅ 같은 회사 소속인지 검증 (OrganizationMapper가 empId로 조회된다고 가정)
        OrganizationVO target = organizationMapper.selectOrganization(empId);
        if (target == null || target.getCompanyNo() == null || !companyNo.equals(target.getCompanyNo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID_TARGET_EMP");
        }

        List<EmpRoleVO> roles = empRoleMapper.selectRolesByEmpId(empId);
        return ResponseEntity.ok(roles);
    }

    /**
     * 권한 부여
     * POST /employee/admin/roles/grant
     * body: { empId, roleName, deptCd }
     */
    @PostMapping("/admin/roles/grant")
    @ResponseBody
    public ResponseEntity<?> grantRole(
        @SessionAttribute("companyNo") String companyNo,
        @RequestBody EmpRoleGrantRequest req,
        Authentication authentication
    ) {
        if (!isTenantAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("NOT_TENANT_ADMIN");
        }

        if (req == null || StringUtils.isBlank(req.getEmpId()) || StringUtils.isBlank(req.getRoleName())) {
            return ResponseEntity.badRequest().body("INVALID_PAYLOAD");
        }

        // 같은 회사 소속인지 검증
        OrganizationVO target = organizationMapper.selectOrganization(req.getEmpId());
        if (target == null || target.getCompanyNo() == null || !companyNo.equals(target.getCompanyNo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID_TARGET_EMP");
        }

        String roleName = req.getRoleName().trim();
        String deptCd = StringUtils.trimToNull(req.getDeptCd());

        // ✅ 전사권한이면 deptCd는 무조건 null
        if ("ROLE_NOTICE_ADMIN".equals(roleName) || "ROLE_TENANT_ADMIN".equals(roleName)) {
            deptCd = null;
        }

        // ✅ 부서 스코프 권한이면: "대상 직원의 실제 부서"와 일치해야만 부여
        if ("ROLE_NOTICE_DEPT_ADMIN".equals(roleName)) {
            if (deptCd == null) {
                return ResponseEntity.badRequest().body("DEPT_CD_REQUIRED");
            }

            String targetDeptCd = StringUtils.trimToNull(target.getDeptCd()); // OrganizationVO에 deptCd 있어야 함
            if (targetDeptCd == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("TARGET_HAS_NO_DEPT");
            }

            // 🔥 핵심: 불일치면 부여 자체를 막는다
            if (!StringUtils.equals(targetDeptCd, deptCd)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "DEPT_SCOPE_MISMATCH: targetDeptCd=" + targetDeptCd + ", requestedDeptCd=" + deptCd
                );
            }
        }

        empRoleMapper.insertRole(req.getEmpId(), roleName, deptCd);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/admin/summary")
    @ResponseBody
    public ResponseEntity<?> getEmployeeSummary(
        @SessionAttribute("companyNo") String companyNo,
        @RequestParam("empId") String empId,
        Authentication authentication
    ) {
        if (!isTenantAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("NOT_TENANT_ADMIN");
        }

        OrganizationVO target = organizationMapper.selectOrganization(empId);
        if (target == null || target.getCompanyNo() == null || !companyNo.equals(target.getCompanyNo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID_TARGET_EMP");
        }

        Map<String, Object> res = new HashMap<>();
        res.put("empId", target.getEmpId());
        res.put("deptCd", target.getDeptCd());
        res.put("deptName", target.getDeptName()); // 있으면
        return ResponseEntity.ok(res);
    }
    /**
     * 권한 회수
     * POST /employee/admin/roles/revoke
     * body: { empId, roleName, deptCd }
     */
    @PostMapping("/admin/roles/revoke")
    @ResponseBody
    public ResponseEntity<?> revokeRole(
        @SessionAttribute("companyNo") String companyNo,
        @RequestBody EmpRoleGrantRequest req,
        Authentication authentication
    ) {
        if (!isTenantAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("NOT_TENANT_ADMIN");
        }

        if (req == null || StringUtils.isBlank(req.getEmpId()) || StringUtils.isBlank(req.getRoleName())) {
            return ResponseEntity.badRequest().body("INVALID_PAYLOAD");
        }

        OrganizationVO target = organizationMapper.selectOrganization(req.getEmpId());
        if (target == null || target.getCompanyNo() == null || !companyNo.equals(target.getCompanyNo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID_TARGET_EMP");
        }

        String roleName = req.getRoleName().trim();
        String deptCd = StringUtils.trimToNull(req.getDeptCd());

        // ✅ 전사 권한은 deptCd null로 강제
        if ("ROLE_NOTICE_ADMIN".equals(roleName) || "ROLE_TENANT_ADMIN".equals(roleName)) {
            deptCd = null;
        }

        empRoleMapper.deleteRole(req.getEmpId(), roleName, deptCd);
        return ResponseEntity.ok().build();
    }
}