package kr.or.ddit.works.approval.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "lineStatusCd")
public class AprvlLineStatusCdVO implements Serializable {
	
	private String lineStatusCd;      	//결재선 상태 코드
	@NotBlank
	private String lineStatusName;      //결재선 상태 이름
	private String lineStatusDesc;      //결재선 상태 설명
}
