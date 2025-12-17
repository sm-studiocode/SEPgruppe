package kr.or.ddit.works.approval.vo;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "aprvlDocNo")
public class AprvlDocVO implements Serializable{
	
	private String aprvlDocNo;			//기안번호
	@NotBlank
	private String docFrmNo;			//양식번호
	private String aprvlDocTitle;		//제목
	private String aprvlDocContents;	//내용
	private String aprvlDocStatus;		//진행상태
	private String isEmergency;			//긴급여부
	private String submitDate;			//상신날짜
	private String closingDate;			//마감날짜
	private Long fileGroupNo;			//파일그룹번호
	private String aprvdDocNo;			//결재완료문서번호
	private String aprvdDate;			//결재완료날짜
	

}
