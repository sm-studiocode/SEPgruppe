package kr.or.ddit.works.notice.vo;

import java.io.Serializable;
import java.util.List;

import kr.or.ddit.works.attachFile.vo.AttachFileVO;
import lombok.Data;

@Data
public class NoticeDetailDTO implements Serializable {

	private int noticeNo;
	private String empId;
	private String noticeCategory;
	private String noticeTitle;
	private String noticeContent;
	private String noticeCreatedAt;
	private String noticeUpdatedAt;
	private Long noticeViewCount;
	private char isDraft;
	private Long fileGroupNo;
	private String companyNo;

	private String empNm;
	private String positionName;
	private String deptName;
	private String deptCd;

	private List<AttachFileVO> file;
}