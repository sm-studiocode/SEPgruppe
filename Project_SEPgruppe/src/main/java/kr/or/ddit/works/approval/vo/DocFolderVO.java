package kr.or.ddit.works.approval.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "docFolderNo")
public class DocFolderVO implements Serializable {
	
	private String docFolderNo;      		//양식폴더번호
	private String parentDocFolder;      	//상위양식폴더번호
	private String docFolderName;      		//폴더명
}
