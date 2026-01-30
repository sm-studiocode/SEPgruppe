<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<security:csrfMetaTags/>

<link href="${pageContext.request.contextPath}/resources/sepgruppe/css/modal/sep-modal.css" rel="stylesheet">

<!-- schedulePayment가 읽어야 하는 planType -->
<input type="hidden" name="planType" value="${plan.planType}" />



<div class="explanation">
  <h2>구독 서비스 결제</h2>
  <p>
    ○ 플랜명 : ${plan.planType}<br>
    ○ 가용 인원 : ${plan.maximumPeople}명<br>
    ○ 월 결제 가격 : ${plan.monthlyPrice}원<br>
    ○ 연 결제 가격 : ${plan.annualPrice}원 <span style="opacity:.85;">(15% 할인)</span>
  </p>
</div>

<div class="subcontainer">
  <!-- 좌측 -->
  <div class="form-left">

    <div class="sec-title">고객 정보</div>

    <div class="kv">
      <div class="k">고객사명</div>
      <div class="v">${company.companyName}</div>
    </div>

    <div class="kv">
      <div class="k">고객사 아이디</div>
      <div class="v">${company.contactId}</div>
    </div>

    <div class="sec-title" style="margin-top:14px;">결제 방식</div>

    <label class="pay-option" for="monthly">
      <input type="radio" id="monthly" name="amount" value="${plan.monthlyPrice}" checked />
      월 결제
    </label>

    <label class="pay-option" for="yearly">
      <input type="radio" id="yearly" name="amount" value="${plan.annualPrice}" />
      1년 결제 <small>(15% 할인)</small>
    </label>

  </div>

  <!-- 우측 -->
  <div class="form-right">
    <div class="sec-title">결제 금액</div>

    <div class="price" id="priceDisplay">
      ${plan.monthlyPrice}원 <span>(부가세 별도)</span>
    </div>

    <div class="notice">구독 플랜 공지</div>

    <div style="margin-top:12px;">
      <button type="button" class="btn btn-primary" onclick="requestPay()">정기결제</button>
    </div>
  </div>
</div>
