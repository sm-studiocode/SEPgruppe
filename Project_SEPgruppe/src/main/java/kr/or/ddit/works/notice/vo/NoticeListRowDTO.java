package kr.or.ddit.works.notice.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class NoticeListRowDTO implements Serializable {
	private int rnum;
	private int noticeNo;
	private String noticeCategory;
	private String noticeTitle;
	private String empNm;
	private String positionName;
	private String noticeCreatedAt;
	private Long noticeViewCount;
}