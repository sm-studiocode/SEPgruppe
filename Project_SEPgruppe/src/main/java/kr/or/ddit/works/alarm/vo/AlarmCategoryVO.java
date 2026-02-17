package kr.or.ddit.works.alarm.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 알람 카테고리 ALARM_CATEGORY VO
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
public class AlarmCategoryVO implements Serializable {
	
	private Long alarmCategoryNo;      	 //알람 카테고리 번호
	private String empId;      		     //사원 아이디
	private String alarmCategoryNm;      //카테고리 명 (승인 요청, 일정 알림, 일반 공지)
	private String alarmCategoryCt;      //카테고리 설명

}
