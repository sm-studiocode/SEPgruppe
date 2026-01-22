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

/**
 * 포트원(아임포트) API 전용 클라이언트 클래스
 *
 * 이 클래스의 역할:
 * - "외부 결제사 서버"와 통신하는 코드만 모아둔 곳
 * - DB 작업 ❌
 * - 비즈니스 판단 ❌
 * - HTTP 요청/응답 처리만 담당
 *
 * 왜 Service가 아니라 Client냐?
 * - Service는 "우리 시스템 로직"
 * - Client는 "외부 시스템 호출"
 */
@Component
public class PortOneClient {

    /**
     * 포트원 API 기본 URL
     * - 토큰 발급, 결제 스케줄 등록 등 모든 요청의 공통 prefix
     */
    private static final String BASE_URL = "https://api.iamport.kr";

    /**
     * JSON 요청 바디의 Content-Type
     * - OkHttp에서 요청 바디 만들 때 사용
     */
    private static final MediaType JSON =
        MediaType.parse("application/json; charset=utf-8");

    /**
     * HTTP 통신을 담당하는 클라이언트
     * - 브라우저가 아니라 서버가 서버에게 요청할 때 사용
     */
    private final OkHttpClient client = new OkHttpClient();

    /**
     * JSON <-> Java 객체 변환용 라이브러리(Jackson)
     * - Map 쓰는 것보다 구조가 명확해서 유지보수에 좋음
     */
    private final ObjectMapper om = new ObjectMapper();

    /**
     * application.properties / application.yml 에서 값을 읽어옴
     *
     * iamport.api.key=xxxxx
     * iamport.api.secret=yyyyy
     *
     * 👉 절대 코드에 하드코딩하면 안 됨 (보안!)
     */
    @Value("${iamport.api.key}")
    private String apiKey;

    @Value("${iamport.api.secret}")
    private String apiSecret;

    /**
     * ==========================
     * 1️⃣ 포트원 Access Token 발급
     * ==========================
     *
     * 포트원 API는 무조건 토큰을 먼저 발급받아야 호출 가능함
     *
     * 요청:
     * POST /users/getToken
     * {
     *   "imp_key": "...",
     *   "imp_secret": "..."
     * }
     *
     * 응답:
     * {
     *   "code": 0,
     *   "response": {
     *     "access_token": "..."
     *   }
     * }
     */
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

    /**
     * ===============================
     * 2️⃣ 정기결제 스케줄 등록 API
     * ===============================
     *
     * 이 메서드는 "정기결제 예약"만 담당함
     * - 실제 결제가 일어나는 건 포트원이 나중에 자동으로 처리
     *
     * token        : getAccessToken()으로 발급받은 토큰
     * customerUid : billingKey (카드 등록 시 발급됨)
     * merchantUid : 우리 시스템 주문번호 (고유해야 함)
     * scheduleAt  : 결제 실행 시각 (epoch second)
     * amount      : 결제 금액
     * name        : 결제 이름 (관리자 화면에 보임)
     */
    public JsonNode schedulePayment(
        String token,
        String customerUid,
        String merchantUid,
        long scheduleAt,
        long amount,
        String name
    ) throws IOException {

        // 1) schedules 배열 안에 들어갈 "단일 스케줄" 생성
        // {
        //   merchant_uid,
        //   schedule_at,
        //   amount,
        //   name
        // }
        ObjectNode schedule = om.createObjectNode();
        schedule.put("merchant_uid", merchantUid);
        schedule.put("schedule_at", scheduleAt);
        schedule.put("amount", amount);
        schedule.put("name", name);

        // 2) schedules 배열 생성 (포트원은 배열 구조 요구)
        ArrayNode schedules = om.createArrayNode();
        schedules.add(schedule);

        // 3) 최종 요청 바디 생성
        // {
        //   customer_uid,
        //   schedules: [...]
        // }
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
}
