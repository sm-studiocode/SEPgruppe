package kr.or.ddit.works.approval.vo;

import java.io.Serializable;
import java.util.List;

import kr.or.ddit.works.attachFile.vo.AttachFileGroupVO;
import kr.or.ddit.works.attachFile.vo.AttachFileVO;
import kr.or.ddit.works.common.vo.CommCodeVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(of = "docFrmNo")
public class DocFormVO implements Serializable {

	private String docFrmNo;		//양식번호
	private String docFolderNo;		//양식폴더번호
	private String docFrmName;      //양식명
	private String docFrmContent;	//양식내용
	private String isEnabled;		//활성화여부
	private Long fileGroupNo;		//첨부파일 그룹 코드번호
	
}
