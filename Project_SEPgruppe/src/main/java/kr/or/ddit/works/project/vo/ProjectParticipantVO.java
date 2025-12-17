package kr.or.ddit.works.project.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(of = "projectParticipantNo")
public class ProjectParticipantVO implements Serializable{
	
	private Long projectParticipantNo;      //참여자 번호
	@NotNull
	private Long projectNo;      			//프로젝트 번호
	private String empId;      				//사원 아이디

}
