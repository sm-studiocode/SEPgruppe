package kr.or.ddit.works.company.vo;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import kr.or.ddit.validate.InsertGroup;
import kr.or.ddit.validate.UpdateGroup;
import kr.or.ddit.works.login.vo.AllUserVO;
import kr.or.ddit.works.subscription.vo.PaymentsVO;
import kr.or.ddit.works.subscription.vo.SubscriptionRequestsVO;
import kr.or.ddit.works.subscription.vo.SubscriptionsVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "contactId")
public class CompanyVO extends AllUserVO implements Serializable{
	
	@NotBlank(groups = InsertGroup.class, message = "아이디는 필수 입력 사항입니다.")
	private String contactId;		// 고객사 아이디 (실제 사용되는 관리자 아이디), update 시 값 고정
	
    @NotBlank(groups = InsertGroup.class,message = "회사명은 필수 입력 사항입니다.")
	private String companyName;		// 회사이름, update 시 값 고정
    
	@NotBlank(groups = InsertGroup.class, message = "이름은 필수 입력 사항입니다.")
	private String contactNm;		// 고객사 관리자 이름, update 시 값 고정
	
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class}, message = "전화번호는 필수 입력 사항입니다")
	private String contactPhone;	// 고객사 관리자 연락처
	
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class}, message = "이메일은 필수 입력 사항입니다")
	@Email
	private String contactEmail;	// 고객사 관리자 이메일
	
	@NotBlank(groups = InsertGroup.class, message = "비밀번호는 필수 입력 사항입니다")
	@Size(min = 4,max = 12, message = "비밀번호를 4자리 이상 12자리 이하로 입력해주세요")
	private String contactPw;		// 고객사 비밀번호(DB 저장용), update 시 입력 안 할 경우 기존 값 유지
	
	private String confirmPw;		// 사용자가 입력한 PW(비밀번호 확인용)
	
	@NotBlank(groups = InsertGroup.class, message = "사업자등록번호는 필수 입력 사항입니다.")
	private String businessRegNo;	// 사업자등록번호, update 시 값 고정
	
	private char isDeleted;			// 탈퇴 여부
	private String companyNo;		// 고객사 번호
	private String adminId;			// 그룹웨어 관리자 아이디 (contactId + _admin 체계)
	
	private String companyZip;		// 고객사 우편번호
	
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class}, message = "주소는 필수 입력 사항입니다")
	private String companyAdd1;		// 고객사 주소
	
	private String companyAdd2;		// 고객사 상세주소
	private String signUp;			// 고객사 가입일
	
	private List<SubscriptionsVO> subscriptions;
	private List<PaymentsVO> payment;
	private List<SubscriptionRequestsVO> subscriptionRequests;

}
