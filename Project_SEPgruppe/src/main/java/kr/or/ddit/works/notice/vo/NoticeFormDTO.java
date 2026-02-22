package kr.or.ddit.works.notice.vo;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class NoticeFormDTO implements Serializable {

	private Integer noticeNo;

	private String empId;
	private String companyNo;

	@NotBlank(message = "제목은 공백일 수 없습니다.")
	private String noticeTitle;

	@NotBlank(message = "내용은 공백일 수 없습니다.")
	private String noticeContent;

	private String noticeCategory;
	private char isDraft;

	private List<MultipartFile> uploadFiles;

	private String attachFileNo;
}