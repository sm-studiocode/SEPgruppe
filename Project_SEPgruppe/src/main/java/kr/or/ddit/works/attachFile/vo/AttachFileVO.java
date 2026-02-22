package kr.or.ddit.works.attachFile.vo;

import java.io.Serializable;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

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
	
	private MultipartFile attachFile; 		// 클라이언트의 업로드 파일을 받기 위한 프로퍼티

	public void setAttachFile(MultipartFile attachFile) {

		// 파일이 비어있거나 null인지 검증
		if(attachFile == null || attachFile.isEmpty()) {
			return;
		}else {

			// 원본파일명 가져오기
			String originalFileName = attachFile.getOriginalFilename();

			// 파일명과 확장자 분리
			int dotIndex = originalFileName.lastIndexOf(".");

			this.attachFile = attachFile;															// 업로드된 파일
			this.attachFileName = attachFile.getOriginalFilename().substring(0,dotIndex);			// 파일명
			this.attachOrgFileName = originalFileName;												// 원본 파일명
			this.attachFileSize = attachFile.getSize();												// 파일 크기
			this.attachFilePath = UUID.randomUUID().toString();										// 파일 경로
			this.attachFileExt = originalFileName.substring(originalFileName.lastIndexOf(".") + 1); // 파일 확장자
		}
	}

	private AttachFileGroupVO attachFileGroupVo;	//첨부파일그룹 has a 관계


}
