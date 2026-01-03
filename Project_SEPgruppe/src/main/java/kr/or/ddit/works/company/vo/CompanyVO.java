package kr.or.ddit.works.company.vo;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import kr.or.ddit.validate.InsertGroup;
import kr.or.ddit.works.login.vo.AllUserVO;
import kr.or.ddit.works.subscription.vo.PaymentsVO;
import kr.or.ddit.works.subscription.vo.SubscriptionRequestsVO;
import kr.or.ddit.works.subscription.vo.SubscriptionsVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "contactId")
public class CompanyVO extends AllUserVO implements Serializable{

	private String contactId;		// 고객사 아이디 (실제 사용되는 관리자 아이디)
    @NotBlank(groups = InsertGroup.class)
	private String companyName;		// 회사이름
	@NotBlank
	private String contactNm;		// 고객사 관리자 이름
	@NotBlank
	@Size(min = 13,max = 13)
	private String contactPhone;	// 고객사 관리자 연락처
	@NotBlank
	@Email
	private String contactEmail;	// 고객사 관리자 이메일
	@NotBlank
	@Size(min = 4,max = 20)
	private String contactPw;		// 고객사 비밀번호
    @Size(min = 10, max = 10)
	private String businessRegNo;	// 사업자등록번호
	private char isDeleted;			// 탈퇴 여부
	private String companyNo;		// 고객사 번호
	private String adminId;			// 그룹웨어 관리자 아이디 (contactId + _admin 체계)
	@NotBlank
	private String companyZip;		// 고객사 우편번호
	private String companyAdd1;		// 고객사 주소
	private String companyAdd2;		// 고객사 상세주소
	private String signUp;			// 고객사 가입일
	
	private List<SubscriptionsVO> subscriptions;
	private List<PaymentsVO> payment;
	private List<SubscriptionRequestsVO> subscriptionRequests;

}
