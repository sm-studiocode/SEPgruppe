package kr.or.ddit.works.subscription.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "billingKeyId")
public class BillingKeyVO implements Serializable {
	
	private Long billingKeyId;	//빌링키 아이디
	@NotBlank
	private String contactId;	//고객사 아이디
	@NotBlank
	private String billingKey;	//빌링키
	@NotBlank
	private String createAt;	//빌링키 등록(발급) 일시
	private String updateAt;	//빌링키수정일시

}
