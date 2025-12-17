package kr.or.ddit.works.schedule.vo;

import java.io.Serializable;
import java.sql.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "schdlNo")
public class ScheduleVO implements Serializable {

	private Long schdlNo; 					// 일전순번
	private Long scheduleTypeNo; 			// 일정유형번호
	private String empId; 					// 사원 아이디
	private String schdlNm; 				// 일정명
	private String schdlCn; 				// 일정설명
	private Date schdlBgngYmd; 				// 일정 시작일
	private Date schdlEndYmd; 				// 일정 종료일
	private String schdlLocation; 			// 일정 장소
	private Date schdlCreateDate; 			// 최초 등록 일시
	private Date notifyTime;				// 알림시간
	private String schdlStatus; 			// 일정상태 (참석/불참/확정/변경/취소)
	private Date schdlUpdateDate;			// 일정 수정일시
	private int projectNo;					// 프로젝트 번호
}
