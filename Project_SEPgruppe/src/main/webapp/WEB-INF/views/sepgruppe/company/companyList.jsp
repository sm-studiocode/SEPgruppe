<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일       			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 3. 15.      	???            최초 생성
 *  2025. 4. 9.      	SJH            수정 중
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<section class="section-padding">
  <div class="card mb-4">
    <div class="card-header">
      <i class="fas fa-table me-1"></i> 고객사 목록
    </div>

    <!-- ✅ 필터 UI 영역 -->
	<div class="d-flex justify-content-end align-items-center px-3 pt-3">
	  <!-- 구독상품 필터 -->
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
          </tr>
        </thead>
        <tbody>
          <c:forEach var="sub" items="${subscriptions}" varStatus="status">
            <tr>
              <td>${status.index + 1}</td>
              <td>${sub.contactId}</td>
              <td>${sub.planType}</td>
              <td>
                <c:if test="${not empty sub.subscriptionStartDate}">
                  <fmt:formatDate value="${sub.subscriptionStartDate}" pattern="yyyy-MM-dd" />
                </c:if>
              </td>
              <td>
				<c:choose>
				  <c:when test="${not empty sub.payment and not empty sub.payment[0].paymentStatus}">
				    ${sub.payment[0].paymentStatus}
				  </c:when>
				  <c:otherwise>
				    ⏳ 확인중
				  </c:otherwise>
				</c:choose>
			  </td>
              <td>
                <c:if test="${not empty sub.billingDate}">
                  ${sub.billingDate}
                </c:if>
              </td>
              <td>
				<c:if test="${not empty sub.payment and not empty sub.payment[0].paymentAmount}">
				  ₩<fmt:formatNumber value="${sub.payment[0].paymentAmount}" type="number" groupingUsed="true"/>
				</c:if>
				<c:if test="${empty sub.payment or empty sub.payment[0].paymentAmount}">
				  ₩0
				</c:if>
			  </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</section>

<script src="${pageContext.request.contextPath}/resources/sepgruppe/js/company/companyList.js"></script>
