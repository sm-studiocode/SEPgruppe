package kr.or.ddit.works.organization.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AnnualLeaveVO implements Serializable{
	
	@NotBlank
	private Long annualLeaveNo;     //연차 번호
	@NotNull
	private String empId;      		//사원 아이디
	private double totalLeave;      //총 연차 수
	private double usedLeave;      	//사용한 연차 수
	private double remainingLeave;  //남은 연차 수
	private double createdLeave;    // 발생 연차 (기안 상태, 반려되면 -처리 가능)
	private String leaveYear;      	//연차 기준 연도 (사용 기간).
	private String createdDate;     //연차 생성일
	private String updatedDate;     //연차 수정일
}
