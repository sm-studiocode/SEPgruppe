package kr.or.ddit.works.alarm.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 실시간 알람 이벤트 전송용 VO.
 *
 * - WebSocket(STOMP) Topic을 통해 클라이언트로 전달되는 이벤트 payload
 * - 현재는 알람 이력 DB 저장 없이 실시간 전달 용도로만 사용됨
 * 
 */

@Data
@EqualsAndHashCode(of = "alarmNo")
public class AlarmHistoryVO implements Serializable{
	
	private Long alarmNo;      			//알람 번호
	private String empId;      			//사원 아이디
	private Long alarmCategoryNo;       //알람 카테고리 번호
	private String alarmNm;      		//알람 제목
	private String alarmContent;      	//알람 내용
	private String isAlarmRead;    		//읽음 상태
	private String alarmDate;      		//알람 발송 시간
	private String alarmReadTime;       //읽은 시간
	
}
