package kr.or.ddit.works.reservation.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(of = "roomNo")
public class MeetingRoomVO implements Serializable {
	
	private Long roomNo;      			//회의실 번호
	private String roomNm;      		//회의실 이름
	private String roomLocation;		//회의실 위치
	private String roomAvailability;	//예약 가능 여부
	private Long roomCapacity;			//수용 인원
	
}
