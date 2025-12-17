package kr.or.ddit.works.login.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * ALL_USER VIEW
 */

@Data
public class AllUserVO implements Serializable{

	private String userId;
	private String userPw;
	private boolean retire;
	private String target;
	
}
