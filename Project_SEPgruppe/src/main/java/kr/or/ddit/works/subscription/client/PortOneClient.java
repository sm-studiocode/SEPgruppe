package kr.or.ddit.works.subscription.client;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Component
public class PortOneClient {


	// 포트원 API 기본 URL
    private static final String BASE_URL = "https://api.iamport.kr";


    // JSON 요청 바디의 Content-Type
    private static final MediaType JSON =
        MediaType.parse("application/json; charset=utf-8");

    // 서버가 서버에게 요청할 때 HTTP 통신을 담당하는 클라이언트
    private final OkHttpClient client = new OkHttpClient();

    // JSON <-> Java 객체 변환용 라이브러리 (Jackson)
    private final ObjectMapper om = new ObjectMapper();

    // application.properties 값 주입
    @Value("${iamport.api.key}")
    private String apiKey;

    @Value("${iamport.api.secret}")
    private String apiSecret;

    // 포트원 호출을 위한 토큰 발급
    public String getAccessToken() throws IOException {

        // 1) 요청 바디(JSON) 생성
        // { "imp_key": apiKey, "imp_secret": apiSecret }
        ObjectNode body = om.createObjectNode();
        body.put("imp_key", apiKey);
        body.put("imp_secret", apiSecret);

        // 2) HTTP 요청 객체 생성
        Request request = new Request.Builder()
            .url(BASE_URL + "/users/getToken")      // 호출할 URL
            .post(RequestBody.create(body.toString(), JSON)) // POST + JSON 바디
            .addHeader("Content-Type", "application/json")
            .build();

        // 3) HTTP 요청 실행
        try (Response response = client.newCall(request).execute()) {

            // HTTP 레벨 실패 (500, 401 등)
            if (!response.isSuccessful()) {
                throw new IOException("포트원 토큰 발급 HTTP 실패: " + response.code());
            }

            // 응답 바디 가져오기
            ResponseBody rb = response.body();
            if (rb == null) {
                throw new IOException("포트원 토큰 발급 응답 바디 없음");
            }

            // JSON 문자열 → JsonNode 파싱
            JsonNode json = om.readTree(rb.string());

            // 포트원 API 레벨 실패 (code != 0)
            if (json.get("code").asInt() != 0) {
                throw new IllegalStateException(
                    "포트원 토큰 발급 실패: " + json.get("message").asText()
                );
            }

            // 정상 응답이면 access_token만 꺼내서 반환
            return json.get("response")
                       .get("access_token")
                       .asText();
        }
    }
    
    // 정기결제 스케줄 등록 API
    public JsonNode schedulePayment(
        String token,		// getAccessToken()으로 발급받은 토큰
        String customerUid,	// billingKey : 카드 등록 시 발급됨
        String merchantUid,	// 시스템 주문번호
        long scheduleAt,	// 결제 실행 시각
        long amount,		// 결제 금액
        String name			// 결제 이름
    ) throws IOException {

    	// 1. 단일 스케줄 생성
        ObjectNode schedule = om.createObjectNode();
        schedule.put("merchant_uid", merchantUid);
        schedule.put("schedule_at", scheduleAt);
        schedule.put("amount", amount);
        schedule.put("name", name);

        // 2) schedules 배열 생성 (포트원은 배열 구조 요구)
        ArrayNode schedules = om.createArrayNode();
        schedules.add(schedule);

        // 3. 요청 바디 생성
        ObjectNode body = om.createObjectNode();
        body.put("customer_uid", customerUid);
        body.set("schedules", schedules);

        // 4) HTTP 요청 생성
        Request request = new Request.Builder()
            .url(BASE_URL + "/subscribe/payments/schedule")
            .addHeader("Authorization", token) // ★ 토큰 필수
            .post(RequestBody.create(body.toString(), JSON))
            .build();

        // 5) HTTP 요청 실행
        try (Response response = client.newCall(request).execute()) {

            // HTTP 레벨 실패
            if (!response.isSuccessful()) {
                throw new IOException(
                    "포트원 스케줄 결제 HTTP 실패: " + response.code()
                );
            }

            // 응답 바디 확인
            ResponseBody rb = response.body();
            if (rb == null) {
                throw new IOException("포트원 스케줄 결제 응답 바디 없음");
            }

            // JSON 응답 그대로 반환
            // (Service에서 code/message 판단함)
            return om.readTree(rb.string());
        }
    }
    
    // 예약결제(스케줄) 취소 API
    public JsonNode unscheduleAll(String token, String customerUid) throws IOException {

        ObjectNode body = om.createObjectNode();
        body.put("customer_uid", customerUid);

        Request request = new Request.Builder()
            .url(BASE_URL + "/subscribe/payments/unschedule")
            .addHeader("Authorization", token)
            .post(RequestBody.create(body.toString(), JSON))
            .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("포트원 예약취소 HTTP 실패: " + response.code());
            }

            ResponseBody rb = response.body();
            if (rb == null) throw new IOException("포트원 예약취소 응답 바디 없음");

            return om.readTree(rb.string());
        }
    }

}
