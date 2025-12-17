package kr.or.ddit.works.attachFile.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "attachFileNo")
public class AttachFileVO implements Serializable{
	
	private String attachFileNo;      		//첨부파일번호
	@NotNull
	private Long fileGroupNo;      			//파일그룹번호
	private String attachFileName;      	//파일명
	private String attachOrgFileName;      	//원본파일명
	private Long attachFileSize;      		//파일크기
	private String attachFilePath;      	//파일경로
	private String attachFileExt;      		//파일확장자
	private String attachFileDate;      	//업로드날짜
	private String attachFileStatus;      	//상태여부(활성화,삭제 등)
	private String empId;					//업로드한 계정

}
