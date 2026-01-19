package kr.or.ddit.works.subscription.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "planNo")
public class SubscriptionPlansVO implements Serializable {
	
	private String planNo;      				//구독 플랜 번호
	private String planType;     				//구독 플랜 유형
	private Long monthlyPrice;      			//월간 가격
	private Long annualPrice;      				//연간 가격
	private String maintainOldPrice;    		//기존 가격 유지 여부 NULL-가격 변화 없음 'Y'-기존 가격 유지 'N'-기존 가격 미유지
	private Long maximumPeople;      			//가용 인원
	private List<SubscriptionPlansVO> plans;	// 구독 플랜 리스트

	
}
