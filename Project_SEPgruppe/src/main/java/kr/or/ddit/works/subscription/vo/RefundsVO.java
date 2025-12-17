package kr.or.ddit.works.subscription.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "refundNo")
public class RefundsVO implements Serializable {
	
	private Long refundNo;      		//환불 요청 번호
	private Long paymentNo;      		//결제 번호
	private String refundDate;      	//환불 요청 날짜
	private Long refundAmount;      	//환불 금액
	private String refundStatus;      	//환불 상태
	private String reason;      		//환불 사유
	private String processedDate;      	//환불 처리 날짜
	@NotBlank
	private String planType;      		//구독 플랜 번호

}
