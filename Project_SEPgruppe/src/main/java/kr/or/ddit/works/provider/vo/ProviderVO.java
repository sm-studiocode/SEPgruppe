package kr.or.ddit.works.provider.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import kr.or.ddit.works.login.vo.AllUserVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "providerId")
public class ProviderVO extends AllUserVO implements Serializable{
	
	private String providerId;      	//서비스 제공자 아이디
	@NotBlank
	private String providerPw;      	//서비스 제공자 비밀번호
	@NotBlank
	private String providerNm;      	//서비스 제공자 이름
	private String providerTel;      	//서비스 제공자 연락처
	private String providerRetire;      //사용 여부
}
