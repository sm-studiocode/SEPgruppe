package kr.or.ddit.works.subscription.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;

import kr.or.ddit.works.subscription.service.PaymentService;
import kr.or.ddit.works.subscription.service.SubScriptionService;
import kr.or.ddit.works.subscription.vo.BillingKeyVO;


// 결제, 구독 관련 화면과 API 요청을 받는 컨트롤러
@Controller
@RequestMapping("/payment") 
public class PaymentsController {

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private SubScriptionService subScriptionService;

    // 구독 결제화면 Form 이동
    @GetMapping("/subPayment")
    public String paymentForm(
    		@RequestParam("what") String planType
    		, Model model
    		, Authentication authentication
            , @RequestParam(value="fragment", required=false, defaultValue="false") boolean fragment
    	) {
        // 1. 로그인한 사용자 정보 가져오기
        String contactId = authentication.getName();

        // 2. 로그인한 사용자와 planType으로 플랜 정보 조회
        Map<String, Object> data = subScriptionService.getPaymentFormData(planType, contactId);

        // 3. JSP에서 출력할 수 있도록 model에 담음
        model.addAttribute("plan", data.get("plan"));		// 선택한 planType
        model.addAttribute("company", data.get("company"));	// 로그인한 사용자 정보

        // tiles 안 타는 “모달용 조각 JSP”
        return "sepgruppe/payment/paymentFormFragment";
    }
    
    // 정기결제 스케줄 등록 요청 (AJAX로 호출)
    @PostMapping("/schedule")
    @ResponseBody 
    public ResponseEntity<Map<String, Object>> schedulePayment(
    		@RequestParam String planType
    		, Authentication authentication) {
        try {
            // 1. 로그인한 사용자의 정보 가져오기
            String contactId = authentication.getName();

            // 2. 스케줄 등록 시 전반적인 핵심 로직은 비즈니스 로직에 위임
            JsonNode result = paymentService.scheduleAndPersist(planType, contactId);

            // 3. 비즈니스 로직에서 결과를 받아 JSON 형태로 응답
            return ResponseEntity.ok(Map.of("success", true, "result", result));

        } catch (IllegalArgumentException | IllegalStateException e) {
            // 4-1. 이미 활성 구독이 있는데 또 가입 시도할 경우 
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));

        } catch (IOException e) {
            // 4-2. 외부 API 통신(포트원) 실패 같은 서버 통신 문제
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "서버 통신 오류: " + e.getMessage()));
        }
    }


    // 카드 등록 성공 후 billingKey를 서버에 저장
    @PostMapping("/saveBillingKey")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveBillingKey(
            @RequestBody Map<String, Object> requestData,
            Authentication authentication
    ) {
        // 1. 프론트에서 넘어온 JSON에서 billingKey(customerUid) 꺼냄
        String customerUid = (String) requestData.get("customerUid");

        // 2. 로그인한 사용자의 정보 가져오기
        String userId = authentication.getName();

        // 3. 필수값(카드 토큰) 포함 검증
        if (customerUid == null || customerUid.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "customerUid가 없습니다."));
        }

        // 4. 이미 billingKey가 저장돼 있으면 중복 저장하지 않음
        BillingKeyVO exists = paymentService.selectBilling(userId);
        if (exists != null && exists.getBillingKey() != null) {
            return ResponseEntity.ok(Map.of("success", true, "message", "BillingKey 이미 있음"));
        }

        // 5. DB에 저장할 VO 생성
        BillingKeyVO billing = new BillingKeyVO();
        billing.setBillingKey(customerUid); // 실제 카드 등록 키
        billing.setContactId(userId);       // 어느 회사의 키인지 구분

        paymentService.saveBilling(billing);

        return ResponseEntity.ok(Map.of("success", true, "message", "BillingKey 저장 완료"));
    }
    
    

}
