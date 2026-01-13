<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link href="${pageContext.request.contextPath}/resources/sepgruppe/css/payment/paymentFormFragment.css" rel="stylesheet">

<!-- 첫번째 모달과 같은 틀 유지 -->
<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/sepgruppe/css/subscriptionPlan.css" />

<!-- schedulePayment가 읽어야 하는 planType -->
<input type="hidden" name="planType" value="${plan.planType}" />

<div class="explanation">
  <h2>구독 서비스 결제</h2>
  <p>
    ○ 플랜명 : ${plan.planType}<br>
    ○ 가용 인원 : ${plan.maximumPeople}명<br>
    ○ 월 결제 가격 : ${plan.monthlyPrice}원<br>
    ○ 연 결제 가격 : ${plan.annualPrice}원 (15% 할인)
  </p>
</div>

<div class="subcontainer">
  <!-- 좌측 -->
  <div class="form-left">

<h2>고객 정보</h2>
<div class="input-group">

  <div class="customer-row">
    <span class="customer-label">고객사명</span>
    <span class="customer-value">${company.companyName}</span>
  </div>

  <div class="customer-row">
    <span class="customer-label">고객사 아이디</span>
    <span class="customer-value">${company.contactId}</span>
  </div>

</div>

    <h2>결제 방식</h2>
    <div class="input-group">
      <div>
        <label>
          <input type="radio" id="monthly" name="amount" value="${plan.monthlyPrice}" checked />
          월 결제
        </label>
        <label>
          <input type="radio" id="yearly" name="amount" value="${plan.annualPrice}" />
          1년 결제 (15% 할인)
        </label>
      </div>
    </div>

  </div>

  <!-- 우측 -->
  <div class="form-right">
    <h3>결제 금액</h3>
    <div class="price" id="priceDisplay">
      ${plan.monthlyPrice}원 <span>(부가세 별도)</span>
    </div>

    <div class="notice">구독 플랜 공지</div>

    <div style="margin-top:12px;">
      <button type="button" class="btn btn-primary" onclick="requestPay()">정기결제</button>
    </div>
  </div>
</div>
