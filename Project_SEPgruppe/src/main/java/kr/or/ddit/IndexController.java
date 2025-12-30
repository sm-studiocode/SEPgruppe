package kr.or.ddit;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
	
	@GetMapping("/")
	public String sepIndex() {
		return "sep:indexSep";
	}
	
	@GetMapping("/groupware")
	public String groupwareIndex() {
		return "gw:indexGW";
	}

}
