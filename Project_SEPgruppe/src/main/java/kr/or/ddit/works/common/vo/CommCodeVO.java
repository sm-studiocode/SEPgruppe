package kr.or.ddit.works.common.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "commCodeNo")
public class CommCodeVO implements Serializable{

	private String commCodeNo;      		//공통코드
	private String parentCommCodeNo;      	//상위공통코드
	private String commCodeName;      		//공통코드명
	private String commCodeDesc;      		//코드설명
	private String commCodeCreatedDate;     //최초등록일시
	private String commCodeUpdateDate;      //수정일자
	private String commCodeYn;      		//사용여부
}
