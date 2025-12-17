package kr.or.ddit.works.organization.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "dclzNo")
public class DclzStatusVO implements Serializable{
	
	private Long dclzNo;      				//근태번호
	private String empId;      				//사원 아이디
	private String attendDate;      		//출근시간
	private String lvffcDate;      			//퇴근시간
	private String workingDay;      		//근무일자
	private String extndWorkingHours;      	//근무연장시간
	private String workStatus;      		//근무상태(출근/퇴근)
	private String workingTime;				//근무시간
	
}
