package kr.or.ddit.works.project.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;


import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;


@Data
@EqualsAndHashCode(of = "taskNo")
public class TaskVO implements Serializable{
	
	private Long taskNo;      				//업무번호
	@NotNull
	private Long projectNo;      			//프로젝트 번호
	@NotNull
	private Long projectParticipantNo;      //참여자 번호
	private String empId;      				//일감 부여자
	private String taskTitle;      			//업무 제목
	private String taskContent;      		//업무 내용
	private String progress;      			//진행율
	private String priority;      			//우선순위
	private Date taskCreateDate;      		//업무 생성일자
	private Date taskStartDate;      		//업무 시작일자
	private Date taskEndDate;      			//업무 마감일자
	private Date taskUpdateDate;      		//업무 수정일자
	private Long fileGroupNo;      			//파일그룹번호
	private String projectEmpId; 			// 프로젝트 생성자 ID
	
}
