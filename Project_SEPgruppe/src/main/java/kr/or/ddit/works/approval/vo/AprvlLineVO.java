package kr.or.ddit.works.approval.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "aprvlLineNo")
public class AprvlLineVO implements Serializable {
	
	private Long aprvlLineNo;	//결재선번호
	@NotBlank
	private String aprvlDocNo;	//문서번호
	private String empId;		//사원 아이디
	private Long aprvlTurn;		//결재순번
	private String aprvlDate;	//결재일
	private String aprvlStatus;	//결재상태
	private String rejectRsn;	//반려사유
	
}
