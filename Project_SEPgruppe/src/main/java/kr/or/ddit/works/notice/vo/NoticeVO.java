package kr.or.ddit.works.notice.vo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of="noticeNo")
public class NoticeVO implements Serializable{

	private int noticeNo;      			//공지사항 번호
	private String empId;      			//사원 아이디
	private String noticeCategory;      //공지사항 카테고리
	private String noticeTitle;      	//제목
	private String noticeContent;      	//내용
	private String noticeCreatedAt;     //작성일
	private String noticeUpdatedAt;     //수정일
	private Long noticeViewCount;      	//조회수
	private char isDraft;      			//임시저장 여부
	private Long fileGroupNo;      		//파일그룹번호
	private String companyNo;			// 고객사 번호
	
}
