<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일             수정자           수정내용
 *  ============    ============== =======================
 *  2025. 3. 24.      손현진            최초 생성
 *
 * 이 JSP의 역할:
 * - 카드 정보를 포트원 결제창으로 받아서
 * - billingKey(customer_uid)를 발급받는 전용 페이지
 * - 실제 결제는 하지 않고 "카드 등록만" 수행
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<!-- JSTL core 태그 (URL 생성용) -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- 포트원(아임포트) 결제 SDK -->
<script src="https://cdn.iamport.kr/js/iamport.payment-1.2.0.js"></script>

<section class="explore-section section-padding" id="section_2">
  <title>포트원(아임포트) 정기결제 테스트</title>

<body>

<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>

  <!-- 화면 제목 -->
  <h1>카드 등록(빌링키 발급) 데모</h1>

  <!-- 설명 문구 -->
  <p>
    아래 버튼 클릭 시 포트원 결제창이 뜨고,
    0원 결제를 통해 카드 등록만 진행합니다.
  </p>

  <!-- 카드 등록 버튼 -->
  <!-- 클릭 시 requestPay() 자바스크립트 함수 실행 -->
  <button onclick="requestPay()">카드 등록하기</button>

  <script>
    /**
     * 1) 포트원 가맹점 식별코드 초기화
     *
     * - 포트원 관리자 콘솔에서 발급받은 imp 코드
     * - 이 값이 없으면 결제창 자체가 열리지 않음
     */
    IMP.init('imp17236348'); // 테스트용 가맹점 코드

    /**
     * 카드 등록 요청 함수
     *
     * 동작 흐름:
     * 1) 포트원 결제창 호출
     * 2) 사용자가 카드 정보 입력
     * 3) 성공 시 billingKey(customer_uid) 발급
     * 4) 발급된 billingKey를 서버로 전송
     */
    function requestPay() {

      /**
       * 2) 포트원 결제창 호출
       *
       * 핵심 포인트:
       * - amount: 0 → 실제 결제 ❌, 카드 등록만 ⭕
       * - customer_uid → 이 값이 billingKey로 사용됨
       */
      IMP.request_pay({
        pg: 'tosspayments',              // 테스트 PG사
        pay_method: 'card',              // 결제 수단: 카드
        merchant_uid: 'order_' + new Date().getTime(),
        // merchant_uid:
        // - 우리 시스템의 주문 번호
        // - 매번 달라야 함 (중복 불가)

        name: '정기결제용 카드 등록',

        amount: 0,                       // 0원 결제 → 카드 정보만 저장

        customer_uid: 'my_customer_' + new Date().getTime()
        // customer_uid:
        // - 포트원에서 billingKey로 사용되는 값
        // - 이후 정기결제 시 이 값으로 카드 결제 수행
      }, function(rsp) {

        /**
         * 3) 결제창 콜백 함수
         *
         * rsp.success === true → 카드 등록 성공
         * rsp.success === false → 실패
         */
        if (rsp.success) {

          // 카드 등록 성공 안내
          alert('카드 등록 성공!\n고객 UID: ' + rsp.customer_uid);

          /**
           * 4) 서버로 billingKey 전달
           *
           * - rsp.customer_uid = billingKey
           * - /payment/saveBillingKey 컨트롤러로 전달
           * - DB(BILLING_KEY 테이블)에 저장됨
           */
          var xhr = new XMLHttpRequest();
          xhr.open('POST', '<c:url value="/payment/saveBillingKey"/>', true);
          xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');

          // ✅ CSRF 헤더 추가 (여기부터)
          var csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
          var csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
          if (csrfToken && csrfHeader) xhr.setRequestHeader(csrfHeader, csrfToken);
          // ✅ CSRF 헤더 추가 (여기까지)
          
          xhr.onload = function() {
            if (xhr.status === 200) {
              // 서버에서 정상 저장 완료
              alert('서버에 빌링키 저장 완료!\n' + xhr.responseText);
            } else {
              // 서버 통신 실패
              alert('서버 통신 에러: ' + xhr.status);
            }
          };

          /**
           * 서버로 전달되는 데이터
           *
           * customerUid : billingKey (필수)
           * impUid      : 포트원 결제 고유 ID (지금은 참고용)
           * payMethod   : 결제 수단 정보
           */
          xhr.send(JSON.stringify({
            customerUid: rsp.customer_uid,
            impUid: rsp.imp_uid,
            payMethod: rsp.pay_method
          }));

        } else {
          // 카드 등록 실패
          alert('카드 등록 실패: ' + rsp.error_msg);
        }
      });
    }
  </script>

</body>
</section>
