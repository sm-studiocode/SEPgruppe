<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<link href="${pageContext.request.contextPath}/resources/sepgruppe/css/modal/sep-modal.css" rel="stylesheet">


<div class="explanation">
  <h2>${plan.planType} 플랜 안내</h2>
  <p>
    ○ 가용 인원 : ${plan.maximumPeople}명 제한<br>
    ○ 월 결제 : 1개월 ${plan.monthlyPrice}원<br>
    ○ 연 결제 : 1개월 ${plan.annualPrice}원<br>
    각 옵션을 선택하면 우측에 요금 정보가 갱신됩니다.
  </p>
</div>

<div class="subcontainer">
  <div class="form-left">
    <h2>구독 신청 유형</h2>
    <div class="input-group">
      <label>
        <input type="radio" name="productType" value="${plan.planType}" checked/>
        ${plan.planType}
      </label>
    </div>

    <h2>결제 방식</h2>
    <div class="input-group">
      <label>
        <input type="radio" name="paymentMethod" value="monthly"
               data-price="${plan.monthlyPrice}" checked/>
        월 결제
      </label>

      <label>
        <input type="radio" name="paymentMethod" value="yearly"
               data-price="${plan.annualPrice}"/>
        1년 결제 <span style="opacity:.8;">(15% 할인)</span>
      </label>
    </div>
  </div>

  <div class="form-right">
    <h2>결제 금액</h2>
    <div class="price" id="priceDisplay">
      ${plan.monthlyPrice}원 <span>(부가세 별도)</span>
    </div>

    <div class="notice">구독 플랜 공지</div>

    <div style="margin-top: 12px;">
      <c:url value="/payment/subPayment" var="paymentUrl">
        <c:param name="what" value="${plan.planType}" />
      </c:url>
      <a href="${paymentUrl}" class="btn btn-primary js-go-pay">구독하기</a>
    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/resources/sepgruppe/js/subscription/subscriptionInsertForm.js"></script>
