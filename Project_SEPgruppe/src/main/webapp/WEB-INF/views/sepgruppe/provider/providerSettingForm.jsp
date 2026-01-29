<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<section class="section-padding">
  <div id="layoutSidenav_content">
    <div class="container-fluid px-4">

      <div>
        <button id="btn1Week" class="btn btn-outline-info">1주일</button>
        <button id="btn1Month" class="btn btn-outline-info">1개월</button>
        <button id="btn3Months" class="btn btn-outline-info">3개월</button>
        <button id="btn6Months" class="btn btn-outline-info">6개월</button>
      </div>

      <br>

      <div class="row">

        <!-- 날짜별 활성 구독 수 -->
        <div class="col-xl-6">
          <div class="card mb-4">
            <div class="card-header">
              <i class="fas fa-chart-area me-1"></i> 날짜별 활성 구독 수
            </div>
            <div class="card-body">
              <canvas
                id="activeLineChart"
                data-subscription-list='<c:out value="${subscriptionListJson}" escapeXml="false" />'
                style="height: 100%;"></canvas>
            </div>
          </div>
        </div>

        <!-- 매출 현황 -->
        <div class="col-xl-6">
          <div class="card mb-4" style="height: 510px;">
            <div class="card-header">
              <i class="fas fa-chart-area me-1"></i> 매출 현황
            </div>
            <div class="card-body">
              <canvas
                id="paymentChart"
                width="100%"
                height="40"
                data-payment-list='<c:out value="${paymentListJson}" escapeXml="false" />'></canvas>
            </div>
          </div>
        </div>

        <!-- 플랜 유형별 활성 구독 (도넛) -->
        <div class="col-xl-6">
          <div class="card mb-4">
            <div class="card-header">
              <i class="fas fa-chart-pie me-1"></i> 플랜 유형별 활성 구독
            </div>
            <div class="card-body">
              <canvas
                id="activeDonutChart"
                data-subscription-list='<c:out value="${subscriptionListJson}" escapeXml="false" />'
                style="height: 100%;"></canvas>
            </div>
          </div>
        </div>

      </div><!-- /.row -->
    </div><!-- /.container-fluid -->
  </div><!-- /#layoutSidenav_content -->
</section>


<!-- ✅ 너가 만든 스크립트 -->
<script src="${pageContext.request.contextPath}/resources/sepgruppe/js/provider/providerSettingForm.js"></script>
