package kr.or.ddit.works.project.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "projectNo")
public class ProjectVO implements Serializable {
	
    private Long projectNo;               // 프로젝트 번호
    @NotBlank
    private String empId;                 // 사원 아이디
    private String projectTitle;          // 프로젝트 제목
    private Date projectStartTime;        // 프로젝트 시작일
    private Date projectEndTime;          // 프로젝트 종료일
    private String projectStatus;         // 프로젝트 상태
    private String projectDesc;           // 프로젝트 설명
  
}





