//package kr.or.ddit.works.organization.controller;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.server.ResponseStatusException;
//
//@Controller
//@RequestMapping("/organization/admin")
//public class OrganizationAdminController {
//
//    private static final String SESSION_KEY = "companyNo";
//
//    private String companyNo(HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//        if (session == null) return null;
//        Object v = session.getAttribute(SESSION_KEY);
//        return (v == null) ? null : v.toString();
//    }
//
//    @GetMapping("/organizationList")
//    public String organizationList(HttpServletRequest request, Model model) {
//        String companyNo = companyNo(request);
//        if (companyNo == null || companyNo.isBlank()) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "session expired");
//        }
//        model.addAttribute("companyNo", companyNo);
//        return "gw:admin/organization/organizationList";
//    }
//}
