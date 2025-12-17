package kr.or.ddit.works.approval.vo;

import java.io.Serializable;
import java.util.List;

import javax.swing.text.Position;
import javax.validation.constraints.NotBlank;

import kr.or.ddit.works.organization.vo.DepartmentVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import kr.or.ddit.works.organization.vo.PositionVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "aprvlLineNo")
public class AprvlLineAutoVO implements Serializable {
	
	private String aprvlLineNo;		// 결재선 번호
	@NotBlank
    private String aprvlTurn;		// 결재 순번
    private String aprvlLineName;	// 결재선명
    private String positionCd;		// 직위 코드
    @NotBlank
    private String docFrmNo;		// 양식번호
    private String commCodeNo;		// 결재선 타입 (결재/합의)
    private String deptCd;			// 부서 코드
}
