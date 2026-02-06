package kr.or.ddit.works.organization.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "positionCd")
public class PositionVO implements Serializable{
	
	private String positionCd;      	// 직위코드
	private String positionName;      	// 직위명
	private Integer sortOrder;			// 정렬 순서 (순서바꾸기용)
	
	private int memberCount;			// 사용 멤버 수


}
