package kr.or.ddit.works.subscription.vo;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "subscriptionNo")
public class SubscriptionsVO implements Serializable {
	
	private Long subscriptionNo;      		//구독 번호
	@NotBlank
	private String contactId;      			//고객사 아이디
	private String paymentStatus;      		//결제 상태(성공, 실패 등)
	private String subscriptionStart;      	//서비스 시작일
	private String subscriptionEnd;      	//서비스 종료일
	private String subscriptionsActive;     //활성화 여부(Y/N)
	@NotBlank
	private String planType;      			//구독 플랜 번호
	private String billingDate;				//자동 결제일
	private Long billingKeyId;				//빌링키 아이디
	private char autoPayment;				//자동결제여부
	
	
}
