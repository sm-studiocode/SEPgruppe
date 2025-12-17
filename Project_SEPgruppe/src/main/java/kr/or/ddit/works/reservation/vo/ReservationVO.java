package kr.or.ddit.works.reservation.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(of = "reservationNo")
public class ReservationVO implements Serializable {
	
    private String reservationNo;				//예약번호
    private String empId;						//사원아이디
    private LocalDate reservationDate;			//예약일
    private LocalDateTime reservationStartTime;	//시작시간
    private LocalDateTime reservationEndTime;	//종료시간
    private String reservationContent;			//예약내용
    private Long roomNo; 						//방 번호

    
    
}
