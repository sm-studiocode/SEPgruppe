package kr.or.ddit;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kr.or.ddit.security.RealUserWrapper;
import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.login.vo.AllUserVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;

@Controller
public class IndexController {

    @GetMapping("/")
    public String sepIndex() {
        return "sep:indexSep";
    }
    
    @GetMapping("/groupware")
    public String groupwareIndex(HttpSession session, Model model, Authentication authentication) {
        Object companyNo = session.getAttribute("companyNo");

        if (companyNo == null && authentication != null && authentication.getPrincipal() instanceof RealUserWrapper) {
            RealUserWrapper wrapper = (RealUserWrapper) authentication.getPrincipal();
            AllUserVO realUser = wrapper.getRealUser();

            if (realUser instanceof EmployeeVO) {
                companyNo = ((EmployeeVO) realUser).getCompanyNo();
            } else if (realUser instanceof CompanyVO) {
                companyNo = ((CompanyVO) realUser).getCompanyNo();
            }
            if (companyNo != null) session.setAttribute("companyNo", companyNo);
        }

        model.addAttribute("companyNo", companyNo);
        return "gw:indexGW";
    }
}
