package kr.or.ddit.works.notice.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class NoticeSearchCondition implements Serializable {
	private String companyNo;
	private String deptCd;
	private String category;
	private boolean admin;
}