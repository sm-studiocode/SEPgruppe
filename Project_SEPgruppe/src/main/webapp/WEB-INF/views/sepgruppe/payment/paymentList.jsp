<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<section class="section-padding">
  <div class="card mb-4">
    <div class="card-header">
      <i class="fas fa-table me-1"></i>
      자동결제관리
    </div>
    <div class="card-body">
      <table id="datatablesSimple">
        <thead>
          <tr>
            <th>No</th>
            <th>고객사명</th>
            <th>결제일자</th>
            <th>결제금액</th>
            <th>결제수단</th>
            <th>결제상태</th>
            <th>자동결제여부</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="payment" items="${paymentList}" varStatus="status">
            <tr>
              <td>${status.index + 1}</td>
              <td>${payment.contactId}</td>
              <td>${payment.paymentDate}</td>
              <td>${payment.paymentAmount}</td>
              <td>${payment.paymentMethod}</td>
              <td>${payment.paymentStatus}</td>
              <td>${payment.autoPayment}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</section>
