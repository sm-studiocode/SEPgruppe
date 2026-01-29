<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!-- ✅ AJAX용 CSRF -->
<meta name="_csrf" content="${_csrf.token}">
<meta name="_csrf_header" content="${_csrf.headerName}">

<section class="section-padding">
  <div class="card mb-4">
    <div class="card-header">
      <i class="fas fa-table me-1"></i> 고객사 목록
    </div>

    <!-- 필터 UI -->
    <div class="d-flex justify-content-end align-items-center px-3 pt-3">
      <div class="d-flex align-items-center">
        <label for="planFilter" class="me-2 fw-bold">구독상품 필터:</label>
        <select id="planFilter" class="form-select form-select-sm" style="width: auto;">
          <option value="">전체 보기</option>
          <option value="Basic">Basic</option>
          <option value="Standard">Standard</option>
          <option value="Professional">Professional</option>
        </select>
      </div>
    </div>

    <div class="card-body">
      <table id="datatablesSimple" class="table table-striped">
		<thead class="table-light">
		  <tr>
		    <th>No</th>
		    <th>고객사명</th>
		    <th>구독상품</th>
		    <th>구독시작일</th>
		    <th>결제상태</th>
		    <th>최근결제일자</th>
		    <th>결제금액</th>
		    <th>관리</th> <!-- ✅ 추가 -->
		  </tr>
		</thead>
		
		<tbody>
		  <c:forEach var="sub" items="${subscriptions}" varStatus="status">
		    <!-- ✅ row에 contactId를 data로 박아둠 -->
		    <tr data-contact-id="${sub.contactId}">
		      <td>${status.index + 1}</td>
		      <td>${sub.contactId}</td>
		      <td>${sub.planType}</td>
		      <td>
		        <c:if test="${not empty sub.subscriptionStart}">
		          ${sub.subscriptionStart}
		        </c:if>
		      </td>
		      <td>
		        <c:choose>
		          <c:when test="${not empty sub.paymentStatus}">
		            ${sub.paymentStatus}
		          </c:when>
		          <c:otherwise>⏳ 확인중</c:otherwise>
		        </c:choose>
		      </td>
		      <td>
		        <c:if test="${not empty sub.billingDate}">
		          ${sub.billingDate}
		        </c:if>
		      </td>
		      <td>
		        <c:choose>
		          <c:when test="${not empty sub.subscriptionPlan and not empty sub.subscriptionPlan.monthlyPrice}">
		            ₩<fmt:formatNumber value="${sub.subscriptionPlan.monthlyPrice}" type="number" groupingUsed="true"/>
		          </c:when>
		          <c:otherwise>₩0</c:otherwise>
		        </c:choose>
		      </td>
		      <!-- ✅ 해지 버튼 -->
<td>

  <c:choose>
    <c:when test="${sub.subscriptionsActive eq 'Y'}">
      <button type="button"
              class="btn btn-sm btn-danger btn-cancel-sub"
              data-contact-id="${sub.contactId}">
        해지
      </button>
    </c:when>
    <c:otherwise>
      <span class="badge bg-secondary">해지됨</span>
    </c:otherwise>
  </c:choose>
</td>
		    </tr>
		  </c:forEach>
		</tbody>
      </table>
    </div>
  </div>
</section>

<!-- ✅ 결제이력 모달 -->
<div class="modal fade" id="paymentHistoryModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-xl modal-dialog-scrollable">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          결제 이력 - <span id="phContactId"></span>
        </h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>

      <div class="modal-body">
        <div class="table-responsive">
          <table class="table table-striped align-middle">
            <thead class="table-light">
              <tr>
                <th>결제번호</th>
                <th>구독번호</th>
                <th>결제일</th>
                <th>금액</th>
                <th>수단</th>
                <th>상태</th>
                <th>자동결제</th>
              </tr>
            </thead>
            <tbody id="phTbody">
              <tr><td colspan="7" class="text-muted">고객사를 클릭하면 결제 이력이 표시됩니다.</td></tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
      </div>
    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/resources/sepgruppe/js/company/companyList.js"></script>
