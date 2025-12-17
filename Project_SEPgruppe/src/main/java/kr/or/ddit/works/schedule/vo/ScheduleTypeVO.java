package kr.or.ddit.works.schedule.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "scheduleTypeNo")
public class ScheduleTypeVO implements Serializable{
	
	private Long scheduleTypeNo;      	//일정유형번호
	@NotBlank
	private String scheduleTypeNm;      //(개인/부서/사내/프로젝트)
}
