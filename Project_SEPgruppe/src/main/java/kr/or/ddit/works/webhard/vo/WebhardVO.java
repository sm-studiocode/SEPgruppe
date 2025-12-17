package kr.or.ddit.works.webhard.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.google.api.client.util.DateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "webhardNo")
public class WebhardVO implements Serializable {
	
	private Long webhardNo;      			//폴더 번호
	private Long parentFolderNo;      		//상위 폴더 번호
	private String empId;      				//사원 아이디
	private String itemType;      			//항목 유형
	private String webhardNm;      			//폴더명(파일이름)
	private Long fileSz;      				//파일 크기
	private String fileExtnNm;      		//파일 확장자
	private String filePathNm;      		//폴더(파일) 경로
	private LocalDateTime uldYmd;      		//업로드 날짜
	private String fileDelYn;      			//삭제 여부(Y/N)
	private String fileFavorite;      		//즐겨찾기 여부
	private LocalDateTime fileUpdateDate;	//수정 날짜
	private String fileId;					// 구글파일 아이디

}
