package kr.or.ddit.works.organization.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import kr.or.ddit.works.organization.service.PositionService;
import kr.or.ddit.works.organization.vo.PositionVO;


@Controller
@RequestMapping("/position/admin")
public class PositionController {
	
	@Autowired
	private PositionService service;
	
	@GetMapping("/positionList")
    public String positionList(
    	@SessionAttribute("companyNo") String companyNo
    	, Model model
    ) {
        List<PositionVO> positionList = service.selectPositionListCount();
        model.addAttribute("companyNo", companyNo);
        model.addAttribute("positionList", positionList);
        return "gw:admin/organization/positionList";
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public int deletePositions(@RequestBody List<String> positionCds) {
        return service.deletePositions(positionCds);
    }
    
    @PostMapping("/insert")
    @ResponseBody
    public int insertPosition(@RequestBody PositionVO positionVO) {
        return service.insertPosition(positionVO);
    }
    
    @PutMapping("/updateSort")
    @ResponseBody
    public int updateSort(@RequestBody List<PositionVO> sortedList) {
        int result = 0;
        for (int i = 0; i < sortedList.size(); i++) {
            PositionVO vo = sortedList.get(i);
            result += service.updateSortOrder(vo.getPositionCd(), i + 1);
        }
        return result;
    }
}
