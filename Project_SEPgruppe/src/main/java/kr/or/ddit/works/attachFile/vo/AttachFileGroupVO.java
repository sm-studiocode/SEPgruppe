package kr.or.ddit.works.attachFile.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "fileGroupNo")
public class AttachFileGroupVO implements Serializable{
	
	private Long fileGroupNo;   				//코드번호
	private String fileGroupName;     			//파일그룹번호
	private String fileGroupCreatedDate;      	//생성일
	private String fileGroupUpdatedDate;      	//수정일
	private String fileGroupStatus;      		//상태여부(활성화,삭제 등)
	@NotBlank
	private String companyNo;					//고객사번호
}
