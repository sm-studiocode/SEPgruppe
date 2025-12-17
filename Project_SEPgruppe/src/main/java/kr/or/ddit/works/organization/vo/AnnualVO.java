package kr.or.ddit.works.organization.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "leaveTypeCd")
public class AnnualVO implements Serializable{

	private String leaveTypeCd;	//휴가종류코드
	@NotBlank
	private String leaveType;	//휴가종류
}
