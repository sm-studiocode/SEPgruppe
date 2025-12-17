package kr.or.ddit.works.approval.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "statusCd")
public class AprvlDocStatusVO implements Serializable {
	
	private String statusCd;      	//문서 상태 코드
	@NotBlank
	private String statusName;      //문서 상태 이름
	private String statusDesc;      //문서 상태 설명

}
