package kr.or.ddit.works.subscription.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

//결재내역 테이블 VO (PAYMENTS)

@Data
@EqualsAndHashCode(of = "paymentNo")
public class PaymentsVO implements Serializable {
	
	private String paymentNo;      		//결제 번호
	private Long subscriptionNo;      	//구독 번호
	private String contactId;      		//고객사 아이디
	private String paymentDate;      	//결제 일자
	private Long paymentAmount;      	//결제 금액
	private String paymentMethod;      	//결제 수단
	private String paymentStatus;      	//결제 상태(예: 완료, 실패, 보류)
	private String autoPayment;      	//자동 결제 여부(Y/N)

}
