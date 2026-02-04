package kr.or.ddit.works.organization.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import kr.or.ddit.works.organization.service.OrganizationService;
import kr.or.ddit.works.organization.vo.DepartmentVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import kr.or.ddit.works.organization.vo.FancyTreeDto;
import kr.or.ddit.works.organization.vo.OrganizationVO;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/organization/popup")
@RequiredArgsConstructor
public class OrganizationPopupController {

    private static final String SESSION_KEY = "companyNo";
    private final OrganizationService service;

    private String requireCompanyNo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "session expired");

        Object v = session.getAttribute(SESSION_KEY);
        if (v == null || v.toString().isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "session expired");

        return v.toString();
    }

    /** ✅ 루트(최상위 부서) */
    @GetMapping(value="/root", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<FancyTreeDto<DepartmentVO>> root(HttpServletRequest request) {
        String companyNo = requireCompanyNo(request);

        return service.selectParentDep(companyNo).stream()
            .map(this::toDeptNode)
            .collect(Collectors.toList());
    }

    /**
     * ✅ 정석: children는 서버가 판단한다.
     * 1) 하위부서 조회
     * 2) 있으면 부서노드 반환 (lazy=true)
     * 3) 없으면 사원노드 반환 (lazy=false)
     */
    @GetMapping(value="/children", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<FancyTreeDto<?>> children(
        @RequestParam("parent") String parentDeptCd,
        HttpServletRequest request
    ) {
        String companyNo = requireCompanyNo(request);

        List<DepartmentVO> childDeps = service.selectChildDep(parentDeptCd, companyNo);
        if (childDeps != null && !childDeps.isEmpty()) {
            return childDeps.stream()
                .map(this::toDeptNode)
                .collect(Collectors.toList());
        }

        // 하위부서가 없으면 사원
        List<EmployeeVO> emps = service.selectEmployee(parentDeptCd, companyNo);
        return emps.stream()
            .map(this::toEmpNode)
            .collect(Collectors.toList());
    }

    /** ✅ 검색(트리 reload용 노드 배열로 반환) */
    @GetMapping(value="/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<FancyTreeDto<OrganizationVO>> search(
        @RequestParam String keyword,
        HttpServletRequest request
    ) {
        String companyNo = requireCompanyNo(request);

        return service.searchEmployees(keyword, companyNo).stream()
            .map(o -> FancyTreeDto.<OrganizationVO>builder()
                .key(o.getEmpId())
                .title(buildSearchTitle(o))
                .folder(false)
                .lazy(false)
                .data(o)
                .build())
            .collect(Collectors.toList());
    }

    private FancyTreeDto<DepartmentVO> toDeptNode(DepartmentVO dep) {
        return FancyTreeDto.<DepartmentVO>builder()
            .key(dep.getDeptCd())
            .title(dep.getDeptName())
            .folder(true)
            .lazy(true)
            .data(dep)
            .build();
    }

    private FancyTreeDto<EmployeeVO> toEmpNode(EmployeeVO emp) {
        return FancyTreeDto.<EmployeeVO>builder()
            .key(emp.getEmpId())
            .title(emp.getEmpNm())
            .folder(false)
            .lazy(false)
            .data(emp)
            .build();
    }

    private String buildSearchTitle(OrganizationVO o) {
        String empNm = (o.getEmpNm() == null) ? "" : o.getEmpNm();
        String pos = (o.getPositionName() == null) ? "" : o.getPositionName();
        return pos.isBlank() ? empNm : empNm + " (" + pos + ")";
    }
}
