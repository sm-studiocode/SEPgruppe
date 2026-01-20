package kr.or.ddit.works.subscription.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;

import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.mybatis.mappers.CompanyMapper;
import kr.or.ddit.works.subscription.service.PaymentService;
import kr.or.ddit.works.subscription.service.SubScriptionService;
import kr.or.ddit.works.subscription.vo.BillingKeyVO;
import kr.or.ddit.works.subscription.vo.PaymentsVO;
import kr.or.ddit.works.subscription.vo.SubscriptionPlansVO;

/**
 * 결제/구독 관련 화면과 API 요청을 받는 컨트롤러
 *
 * 컨트롤러 역할(아주 중요):
 * - "요청을 받는다"
 * - 필요한 값 꺼낸다(로그인 사용자, 파라미터 등)
 * - 서비스 로직(비즈니스 로직)은 Service에 맡긴다
 * - 결과를 화면(Model)이나 JSON(ResponseBody)로 돌려준다
 */
@Controller
@RequestMapping("/payment") // 이 컨트롤러의 모든 URL은 /payment 로 시작함
public class PaymentsController {

    /**
     * 구독 플랜 조회/구독 등록 같은 "구독 관련 서비스"
     */
    @Autowired
    private SubScriptionService subService;

    /**
     * 결제/빌링키/스케줄 등록 같은 "결제 관련 서비스"
     */
    @Autowired
    private PaymentService paymentService;

    /**
     * 회사 정보 조회용 Mapper
     * (로그인 principal을 CompanyVO로 캐스팅하는 방식은 위험해서 DB에서 조회하도록 바꾼 상태)
     */
    @Autowired
    private CompanyMapper companyMapper;

    
    /**
     * 구독 결제 화면(form) 보여주기
     *
     * URL: GET /payment/subPayment?what=PLAN_TYPE
     *
     * planType(what)으로 플랜 정보를 DB에서 조회해서 화면에 보여주는 용도
     *
     * 하는 일:
     * 1) 로그인한 회사(contactId)로 회사 정보를 DB에서 조회
     * 2) planType으로 구독 플랜 정보를 DB에서 조회
     * 3) JSP에서 쓰도록 model에 담고 결제 화면으로 이동
     */
    @GetMapping("/subPayment")
    public String paymentForm(
    		@RequestParam("what") String planType
    		, Model model
    		, Authentication authentication
            , @RequestParam(value="fragment", required=false, defaultValue="false") boolean fragment
    	) {
        // 로그인한 사용자(contactId)를 기준으로 회사 정보 조회
        CompanyVO company = getCompany(authentication);

        // planType으로 플랜 정보 조회 (가격/인원수 등)
        SubscriptionPlansVO plan = subService.planOne(planType);

        // JSP에서 출력할 수 있도록 model에 담음
        model.addAttribute("plan", plan);
        model.addAttribute("company", company);

        if (fragment) {
            // ✅ tiles 안 타는 “모달용 조각 JSP”
            return "sepgruppe/payment/paymentFormFragment";
        }
        // 결제 폼 화면으로 이동
        return "sep:payment/paymentForm";
    }

    /**
     * 로그인한 사용자의 회사 정보 가져오기
     *
     * Authentication(authentication.getName()) = 로그인 아이디(여기서는 contactId)
     *
     * 하는 일:
     * 1) 로그인 ID(contactId)를 얻는다
     * 2) DB에서 회사 정보(CompanyVO)를 조회한다
     * 3) 없으면 예외(정상 흐름이 아니므로)
     */
    private CompanyVO getCompany(Authentication authentication) {
        // 로그인한 사용자 id (보통 username)
        String contactId = authentication.getName();

        // DB에서 회사 정보 조회
        CompanyVO company = companyMapper.selectCompany(contactId);

        // 회사 정보가 없다면 로직 진행이 불가능 (화면에 뿌릴 것도 없고, 이후 구독 저장도 안 됨)
        if (company == null) {
            throw new IllegalStateException("회사 정보 없음: " + contactId);
        }
        return company;
    }

    /**
     * 정기결제 "스케줄 등록" 요청 (AJAX로 호출)
     *
     * URL: POST /payment/schedule?planType=xxx
     * 응답: JSON (success: true/false)
     *
     * 이 메서드가 하는 일(요약):
     * 1) 로그인한 사용자(contactId) 찾기
     * 2) 서비스(paymentService.scheduleAndPersist) 호출해서
     *    - 플랜 조회해서 금액 확정
     *    - billingKey 있는지 확인
     *    - 포트원 스케줄 등록
     *    - DB에 SUBSCRIPTIONS / PAYMENTS 저장
     * 3) 결과를 JSON으로 프론트에 돌려준다
     */
    @PostMapping(value = "/schedule", produces = "application/json; charset=UTF-8")
    @ResponseBody // return 값을 "뷰 이름"이 아니라 "그대로 응답 바디(JSON)"로 보낸다
    public ResponseEntity<Map<String, Object>> schedulePayment(@RequestParam String planType, Authentication authentication) {
        try {
            // 로그인한 사용자의 id(contactId)
            String contactId = authentication.getName();

            // 핵심 로직은 서비스에 위임
            JsonNode result = paymentService.scheduleAndPersist(planType, contactId);

            // 프론트가 보기 편하게 JSON 형태로 응답
            return ResponseEntity.ok(Map.of("success", true, "result", result));

        } catch (IllegalArgumentException | IllegalStateException e) {
            // 사용자 입력/상태 문제(예: 없는 planType, billingKey 없음 등)
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));

        } catch (IOException e) {
            // 외부 API 통신(포트원) 실패 같은 서버 통신 문제
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "서버 통신 오류: " + e.getMessage()));
        }
    }

    /**
     * 카드 등록 성공 후 billingKey(customerUid)를 서버(DB)에 저장하는 API
     *
     * URL: POST /payment/saveBillingKey
     * 요청 바디(JSON): { "customerUid": "xxx" }
     * 응답(JSON): success true/false
     *
     * 이 메서드가 하는 일(요약):
     * 1) 프론트가 보내준 customerUid(= billingKey)를 꺼낸다
     * 2) 로그인한 사용자(contactId)를 확인한다
     * 3) customerUid가 비어있으면 400 오류
     * 4) 이미 billingKey가 저장돼 있으면 "이미 있음" 반환
     * 5) 없으면 BILLING_KEY 테이블에 저장
     */
    @PostMapping(value = "/saveBillingKey", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveBillingKey(
            @RequestBody Map<String, Object> requestData,
            Authentication authentication
    ) {
        // 프론트에서 넘어온 JSON에서 billingKey(customerUid) 꺼냄
        String customerUid = (String) requestData.get("customerUid");

        // 로그인한 사용자 ID(contactId)
        String userId = authentication.getName();

        // customerUid가 없으면 저장 불가능
        if (customerUid == null || customerUid.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "customerUid가 없습니다."));
        }

        // 이미 billingKey가 저장돼 있으면 중복 저장하지 않음
        BillingKeyVO exists = paymentService.selectBilling(userId);
        if (exists != null && exists.getBillingKey() != null) {
            return ResponseEntity.ok(Map.of("success", true, "message", "BillingKey 이미 있음"));
        }

        // DB에 저장할 VO 생성
        BillingKeyVO billing = new BillingKeyVO();
        billing.setBillingKey(customerUid); // 실제 카드 등록 키
        billing.setContactId(userId);       // 어느 회사의 키인지 구분

        // DB 저장
        paymentService.saveBilling(billing);

        // 성공 응답
        return ResponseEntity.ok(Map.of("success", true, "message", "BillingKey 저장 완료"));
    }
    
    // 관리자페이지 자동결제관리
	@GetMapping("")
	public String selectListAllPayment(Model model) {
		List<PaymentsVO> paymentList = paymentService.paymentList();
		model.addAttribute("paymentList", paymentList);
		return "sep:admin/payment/paymentList";
	}
}
