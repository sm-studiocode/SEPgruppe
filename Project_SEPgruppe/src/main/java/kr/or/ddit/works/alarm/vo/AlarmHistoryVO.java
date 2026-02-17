package kr.or.ddit.works.alarm.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 알람 히스토리 ALARM_HISTORY VO
 * @author JYS
 * @since 2025. 3. 16.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 3. 16.     	JYS	          최초 생성
 *
 * </pre>
 */
@Data
public class AlarmHistoryVO implements Serializable {
	
	private Long alarmNo;      			//알람 번호
	private String empId;      			//사원 아이디
	private Long alarmCategoryNo;       //알람 카테고리 번호
	private String alarmNm;      		//알람 제목
	private String alarmContent;      	//알람 내용
	private String isAlarmRead;    		//읽음 상태
	private String alarmDate;      		//알람 발송 시간
	private String alarmReadTime;       //읽은 시간

}
