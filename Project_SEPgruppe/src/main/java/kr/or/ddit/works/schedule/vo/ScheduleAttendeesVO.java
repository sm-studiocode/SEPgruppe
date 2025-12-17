package kr.or.ddit.works.schedule.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ScheduleAttendeesVO {
	
	@NotNull
	private Long schdlNo;      				//일정순번
	@NotBlank
	private String empId;      				//사원 아이디
	private String attendanceStatus;      	//참석 여부 (참석/불참/미정)

}
