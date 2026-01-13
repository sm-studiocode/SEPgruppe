<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!--
  paymentForm.jsp 역할(초보용):
  - 사용자가 구독할 플랜 정보를 확인하는 화면
  - 월/연 가격을 화면에 표시(표시용 UI)
  - "구독하기(정기결제)" 버튼을 누르면 JS가 /payment/schedule 로 요청을 보냄
  - 서버는 amount를 믿지 않고 planType으로 DB에서 가격을 다시 조회해서 결제 처리(조작 방지)
-->
<section class="explore-section section-padding" id="section_2">
  <title>구독 서비스 결제</title>

  <style>
    .paymentContainer { max-width: 800px; margin: 40px auto; background: #fff; padding: 20px; border-radius: 5px; }
    h1, h2, h3 { margin-bottom: 15px; }
    .plan-info, .payment-form { margin-bottom: 30px; }
    .plan-info p { margin: 5px 0; }
    .form-group { margin-bottom: 15px; }
    label { display: block; margin-bottom: 5px; }
    input[type="text"], input[type="number"], input[type="password"] { width: 100%; padding: 8px; box-sizing: border-box; }
    button { padding: 10px 20px; background: #007bff; color: #fff; border: none; border-radius: 3px; cursor: pointer; }
    button:hover { background: #0056b3; }
  </style>

  <!--
    ✅ data-ctx 추가 이유:
    - JS에서 컨텍스트 경로(예: /sep)를 알아야 요청 URL을 정확히 만들 수 있음
    - JS에서: document.body.getAttribute("data-ctx") 로 읽어감
  -->
  <body data-ctx="${pageContext.request.contextPath}">
    <div class="paymentContainer">
      <h1>구독 서비스 결제</h1>

      <!-- [1] 구독 플랜 정보 표시 영역 -->
      <div class="plan-info">
        <h2>구독 플랜 정보</h2>
        <p>
          플랜명: <strong>${plan.planType}</strong>
        </p>
        <p>
          가용 인원: <strong>${plan.maximumPeople}명</strong>
        </p>
        <p>
          월 결제 가격: <strong>${plan.monthlyPrice}원</strong>
        </p>
        <p>
          연 결제 가격: <strong>${plan.annualPrice}원</strong> (15% 할인 적용)
        </p>
      </div>

      <!-- [2] 구독 요청 폼 영역 -->
      <div class="payment-form">
        <h2>구독 정보 입력</h2>

        <!--
          form action은 있어도 되고 없어도 됨(현재는 JS로 요청)
          - 실제 결제 요청은 JS(schedulePayment())가 /payment/schedule 로 보냄
        -->
        <form id="scheduleForm" action="<c:url value='/payment/schedule'/>" method="post">

          <!--
            planType: 서버가 플랜을 다시 조회할 때 사용하는 핵심 값
            - 금액은 프론트에서 보내지 않아도 됨(서버에서 DB 조회)
          -->
          <input type="hidden" name="planType" value="${plan.planType}" />

          <!-- 회사 정보 표시(표시용: 사용자가 입력하는 값 아님) -->
          <div class="row mb-3">
            <label for="companyName" class="col-md-4 col-lg-3 col-form-label">고객사명</label>
            <div class="col-mb-8 col-lg-9">
              <div>${company.companyName }</div>
            </div>
          </div>

          <div class="row mb-3">
            <label for="contactId" class="col-md-4 col-lg-3 col-form-label">고객사 아이디</label>
            <div class="col-md-8 col-lg-9">
              <div>${company.contactId}</div>
            </div>
          </div>

          <!--
            [결제 방식 선택 라디오]
            - 화면에 보여주는 가격을 바꿔주는 UI
            - ✅ name="amount" 라는 이름이 JS에서 이벤트를 걸 대상임
          -->
          <div class="form-group">
            <label>결제 방식 선택</label>
            <label>
              <input type="radio" id="monthly" name="amount" value="${plan.monthlyPrice}" checked>
              월 결제
            </label>
            <label>
              <input type="radio" id="yearly" name="amount" value="${plan.annualPrice}">
              연 결제 (15% 할인)
            </label>
          </div>

          <!-- [결제 금액 표시 영역] -->
          <div class="form-group">
            <label>결제 금액</label>
            <div id="priceDisplay">
              ${plan.monthlyPrice}원 <span>(부가세 별도)</span>
            </div>
          </div>

          <!--
            ✅ 너가 수정한 부분:
            - 버튼 클릭 시 schedulePayment() 호출
            - JS가 /payment/schedule 로 AJAX 요청을 보내서 구독/결제 로직이 실행됨
          -->
          <p class="btn btn-primary" onclick="requestPay()">정기결제</p>

        </form>
      </div>
    </div>

    <!-- 포트원 SDK: 현재 schedulePayment()는 포트원 SDK 없이 서버만 호출하지만, 남겨둬도 문제는 없음 -->
    <script src="https://cdn.iamport.kr/js/iamport.payment-1.2.0.js"></script>

    <!-- 결제 화면 동작 JS -->
    <script src="${pageContext.request.contextPath }/resources/sepgruppe/js/payment/paymentForm.js"></script>
  </body>
</section>
