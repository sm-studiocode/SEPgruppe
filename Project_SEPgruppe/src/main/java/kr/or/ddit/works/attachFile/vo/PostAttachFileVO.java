package kr.or.ddit.works.attachFile.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * POST_ATTACH_FILE 테이블과 매핑되는 VO
 * 하나의 게시글 또는 공지사항에 여러 첨부파일을 관리
 */

@Data
@EqualsAndHashCode(of = "postAttachFileNo")
public class PostAttachFileVO implements Serializable{
	
	private String postAttachFileNo;	// 기본키
	private Long postNo;				// 게시판 글번호
	private Long noticeNo;				// 공지사항 글번호
	private String attachFileNo;		// 첨부파일 번호
	
}
