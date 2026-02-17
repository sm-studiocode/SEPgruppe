package kr.or.ddit.works.alarm.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 알람 정보 ALARM_NOTIFICATIONS VO
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
public class AlarmNotificationVO implements Serializable {
	
	private String empId;      				//사원 아이디
	private String playerId;				//ONESIGNAL에서 제공하는(PLAYER ID)
	private String pushNotificationTime;    //푸시 알람 등록 시간

}
