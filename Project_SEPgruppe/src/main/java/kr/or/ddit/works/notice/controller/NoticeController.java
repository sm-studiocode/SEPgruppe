package kr.or.ddit.works.notice.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.paging.DefaultPaginationRenderer;
import kr.or.ddit.paging.PaginationInfo;
import kr.or.ddit.paging.PaginationRenderer;
import kr.or.ddit.paging.SimpleCondition;
import kr.or.ddit.security.RealUserWrapper;
import kr.or.ddit.validate.InsertGroup;
import kr.or.ddit.validate.UpdateGroup;
import kr.or.ddit.works.attachFile.service.AttachFileService;
import kr.or.ddit.works.attachFile.vo.AttachFileVO;
import kr.or.ddit.works.mybatis.mappers.OrganizationMapper;
import kr.or.ddit.works.notice.service.NoticeService;
import kr.or.ddit.works.notice.vo.NoticeDetailDTO;
import kr.or.ddit.works.notice.vo.NoticeFormDTO;
import kr.or.ddit.works.notice.vo.NoticeListRowDTO;
import kr.or.ddit.works.notice.vo.NoticeSearchCondition;
import kr.or.ddit.works.notice.vo.NoticeVO;
import kr.or.ddit.works.organization.vo.DepartmentVO;
import kr.or.ddit.works.organization.vo.OrganizationVO;
import lombok.extern.slf4j.Slf4j;

/**
 * 공지사항 컨트롤러
 *
 * @author JYS
 * @since 2025. 3. 14.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 3. 14.     	JYS	          최초 생성
 *
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/notice")
public class NoticeController {

	private static final String MODELNAME = "notice";

    @Autowired
    private NoticeService service;

    @Autowired
    private AttachFileService attachFileService;

    @Autowired
    private OrganizationMapper organiMapper;

    long fileGroupNo = 3;

    private String resolveCompanyNo(HttpSession session, Authentication authentication) {
        Object companyNoObj = session.getAttribute("companyNo");
        if (companyNoObj != null) {
            return String.valueOf(companyNoObj);
        }
        String empId = authentication.getName();
        OrganizationVO org = organiMapper.selectOrganization(empId);
        if (org != null && org.getCompanyNo() != null && !org.getCompanyNo().isBlank()) {
            session.setAttribute("companyNo", org.getCompanyNo());
            return org.getCompanyNo();
        }
        throw new IllegalStateException("companyNo not found in session");
    }

    /** 공지사항 목록 조회 */
    @GetMapping("")
    public String selectListAllNotice(
        @ModelAttribute("condition") SimpleCondition condition
        , @RequestParam(name = "category", required = false, defaultValue = "all") String category
        , @RequestParam(name = "page", required = false, defaultValue = "1") int currentPage
        , Model model
        , Authentication authentication
        , HttpSession session
    ) {
    	String companyNo = resolveCompanyNo(session, authentication);

    	RealUserWrapper userWrapper = (RealUserWrapper) authentication.getPrincipal();
        session.setAttribute("realUser", userWrapper.getRealUser());

        OrganizationVO member = organiMapper.selectOrganization(authentication.getName());
        String deptCd = member.getDeptCd();

        String empId = authentication.getName();
        DepartmentVO advice = service.selectLogin(empId);

        NoticeSearchCondition detail = new NoticeSearchCondition();
        detail.setCompanyNo(companyNo);
        detail.setDeptCd(deptCd);
        detail.setCategory(category);

        PaginationInfo<NoticeSearchCondition> paging = new PaginationInfo<>();
        paging.setCurrentPage(currentPage);
        paging.setSimpleCondition(condition);
        paging.setDetailCondition(detail);

        List<NoticeListRowDTO> noticeList = service.selectAllNotice(paging, companyNo);
        model.addAttribute("noticeList", noticeList);

        int total_cnt = service.selectAllNoticeTotalRecord(paging);
        paging.setTotalRecord(total_cnt);

        PaginationRenderer renderer = new DefaultPaginationRenderer();
        String pagingHTML = renderer.renderPagination(paging);

        model.addAttribute("pagingHTML", pagingHTML);
        model.addAttribute("companyNo", companyNo);
        model.addAttribute("category", category);
        model.addAttribute("member", member);
        model.addAttribute("advice", advice);

        return "gw:notice/noticeList";
    }

    /** 공지사항 상세 조회 */
    @GetMapping("/{noticeNo}")
    public String selectNoticeDetail(
        @PathVariable("noticeNo") int noticeNo
        , @ModelAttribute("member") NoticeVO member
        , Authentication authentication
        , HttpSession session
        , Model model
    ) {
    	String companyNo = resolveCompanyNo(session, authentication);

    	String empId = authentication.getName();
    	member.setEmpId(empId);

    	NoticeDetailDTO detailNotice = service.getNoticeDetailWithCompany(noticeNo, companyNo, true);

    	if (detailNotice != null) {
	    	model.addAttribute("detailNotice", detailNotice);
	        model.addAttribute("companyNo", companyNo);
	        model.addAttribute("member", member);
	        model.addAttribute("noticeNo", noticeNo);
	        return "gw:notice/noticeDetail";
	    } else {
	        return "redirect:/notice?error=notfound";
	    }
    }

    /** 관리자 - 새 공지글 등록 폼 이동 */
    @GetMapping("new")
    public String insertNoticeFormUI(
        @ModelAttribute("member") NoticeVO member
        , Model model
        , Authentication authentication
        , HttpSession session
    ) {
    	String companyNo = resolveCompanyNo(session, authentication);

    	String empId = authentication.getName();
    	member.setEmpId(empId);

        int draftCnt = service.isDraftCnt(empId);
        model.addAttribute("draftList", service.isDraftList(empId));
        model.addAttribute("draftCnt", draftCnt);

        if (draftCnt > 0) {
        	model.addAttribute("selectDraft", service.isDraftList(empId).get(0));
        }

        DepartmentVO advice = service.selectLogin(empId);

        model.addAttribute("advice", advice);
        model.addAttribute("companyNo", companyNo);

        return "gw:notice/noticeForm";
    }

    /** 관리자 - 새 공지글 등록 */
    @PostMapping("new")
    public String insertNotice(
        @Validated(InsertGroup.class) @ModelAttribute(MODELNAME) NoticeFormDTO notice,
        BindingResult errors,
        RedirectAttributes redirectAttributes,
        Model model,
        Authentication authentication,
        HttpSession session
    ) {
        String companyNo = resolveCompanyNo(session, authentication);

        if (errors.hasErrors()) {
            String errorName = BindingResult.MODEL_KEY_PREFIX + MODELNAME;
            redirectAttributes.addFlashAttribute(MODELNAME, notice);
            redirectAttributes.addFlashAttribute(errorName, errors);
            return "redirect:/notice/new";
        }

        notice.setCompanyNo(companyNo);
        notice.setEmpId(authentication.getName());

        service.createNoticeWithFilesAndAlarm(notice, fileGroupNo);

        model.addAttribute("companyNo", companyNo);
        return "redirect:/notice";
    }

    /** 관리자 - 공지사항 수정 폼으로 이동 */
    @GetMapping("{noticeNo}/editForm")
    public String updateFormUI(
    	@PathVariable("noticeNo") int noticeNo
    	, Model model
        , @ModelAttribute("member") NoticeVO member
        , Authentication authentication
        , HttpSession session
    ) {
    	String companyNo = resolveCompanyNo(session, authentication);

    	String empId = authentication.getName();
    	member.setEmpId(empId);

        int draftCnt = service.isDraftCnt(empId);
        List<NoticeVO> draftList = service.isDraftList(empId);

        NoticeDetailDTO selectNotice = service.getNoticeDetailWithCompany(noticeNo, companyNo, false);

    	model.addAttribute("companyNo", companyNo);
    	model.addAttribute("selectNotice", selectNotice);
    	model.addAttribute("draftCnt", draftCnt);
    	model.addAttribute("draftList", draftList);
    	model.addAttribute("noticeNo", noticeNo);

    	return "gw:notice/noticeEdit";
    }

    /** 관리자 - 공지글 수정 */
    @PostMapping("{noticeNo}/edit")
    public String updateNotice(
    	@PathVariable("noticeNo") int noticeNo
    	, @Validated(UpdateGroup.class) @ModelAttribute(MODELNAME) NoticeFormDTO notice
    	, BindingResult errors
    	, RedirectAttributes redirectAttributes
		, Authentication authentication
        , Model model
        , HttpSession session
    ) {
    	String companyNo = resolveCompanyNo(session, authentication);

    	notice.setEmpId(authentication.getName());
    	notice.setNoticeNo(noticeNo);
    	notice.setCompanyNo(companyNo);

    	if (errors.hasErrors()) {
    		redirectAttributes.addFlashAttribute(MODELNAME, notice);
    		String errorName = BindingResult.MODEL_KEY_PREFIX + MODELNAME;
    		redirectAttributes.addFlashAttribute(errorName, errors);
    		return "redirect:/notice/" + noticeNo + "/editForm";
    	}

    	service.updateNoticeWithFiles(noticeNo, notice, fileGroupNo);

        model.addAttribute("companyNo", companyNo);
        return "redirect:/notice/" + noticeNo;
    }

    /** 공지사항 수정, 삭제 검증을 위한 메서드 */
    @GetMapping("/{noticeNo}/select")
    public ResponseEntity<NoticeDetailDTO> getNotice(
    		@PathVariable("noticeNo") int noticeNo
    		, Authentication authentication
    		, HttpSession session
    ) {
    	 String companyNo = resolveCompanyNo(session, authentication);
    	 NoticeDetailDTO notice = service.getNoticeDetailWithCompany(noticeNo, companyNo, false);
    	 return ResponseEntity.ok(notice);
    }

    /** 관리자 - 공지글 삭제 */
    @PostMapping("/delete")
    public String deleteNotice(
    		@RequestParam("noticeNo") String noticeNoStr
    		, Authentication authentication
    		, HttpSession session
    		, Model model
    	) {
    	String companyNo = resolveCompanyNo(session, authentication);
    	String empId = authentication.getName();

    	service.deleteNoticesWithAuth(noticeNoStr, empId, companyNo);

    	model.addAttribute("companyNo", companyNo);
    	return "redirect:/notice";
    }

    // 공지사항 리스트 엑셀 다운로드
    @GetMapping("/excelDownload")
    public void noticeExcel(
    	@ModelAttribute("condition") SimpleCondition condition
    	, @RequestParam(name = "category", required = false, defaultValue = "all") String category
    	, HttpServletResponse response
    	, Authentication authentication
    	, HttpSession session
    ) throws IOException {
    	String companyNo = resolveCompanyNo(session, authentication);

        OrganizationVO member = organiMapper.selectOrganization(authentication.getName());
        String deptCd = member.getDeptCd();

        byte[] excelBytes = service.buildNoticeExcelBytes(companyNo, deptCd, category, condition);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=notice_list.xlsx");

        OutputStream out = response.getOutputStream();
        out.write(excelBytes);
        out.flush();
        out.close();
    }

    // 파일 다운로드 처리
    @GetMapping("/{noticeNo}/download")
    public ResponseEntity<Resource> downloadFile(
    	@RequestParam("attachFileNo") String attachFileNo
		, @PathVariable("noticeNo") int noticeNo
    ) throws IOException{

    	AttachFileVO file = service.selectByFileNo(attachFileNo);

    	Resource resource = attachFileService.getAttachFileDownload(file);

    	if(resource == null) {
    		return ResponseEntity.notFound().build();
    	}

    	String encodedFileName = URLEncoder.encode(file.getAttachOrgFileName(),"UTF-8").replaceAll("\\+", "%20");

    	return ResponseEntity.ok()
    			.contentType(MediaType.APPLICATION_OCTET_STREAM)
    			.contentLength(file.getAttachFileSize())
    			.header("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"")
    			.body(resource);
    }
}