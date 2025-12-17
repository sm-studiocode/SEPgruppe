package kr.or.ddit.works.company.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "companyNo")
public class CompanyDivisionVO {
	
	private String companyNo;      //고객사번호
	private String contactId;      //고객사 아이디

}
