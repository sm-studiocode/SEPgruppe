package kr.or.ddit.works.subscription.vo;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "requestNo")
public class SubscriptionRequestsVO implements Serializable {
	
	private Long requestNo;      		//신청 번호
	@NotBlank
	private String planNo;      		//구독 플랜 번호
	@NotBlank
	private String contactId;      		//고객사 아이디
	private String requestDate;     	//신청 날짜
	private String requestPeriod;      	//신청 구독 기간
	private Long requestPersonCount;    //신청 인원 수
	private String paymentMethod;      	//결제 방식
	private String requestsStatus;      //신청 상태(승인/거절 등)

}
