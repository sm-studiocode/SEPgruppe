<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 4. 9.     	SJH            최초 생성
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <title>구독 플랜 관리</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/sepgruppe/css/subscription/subscriptionPlanManage.css">
<section class="section-padding" id="subscription-manage-wrapper">    
<div class="container mt-5">
    <h2>구독 플랜 일괄 관리</h2>
    <form method="post" action="${pageContext.request.contextPath}/subscriptionPlan/manage/save">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    
        <table class="table table-bordered">
            <thead class="table-light">
                <tr>
                    <th></th>
                    <th>플랜 유형</th>
                    <th>월간 가격</th>
                    <th>연간 가격</th>
                    <th>기존 가격 유지</th>
                    <th>최대 인원</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="plan" items="${planList}" varStatus="status">
                    <tr>
                        <td>${status.index + 1}</td>
                        <td>${plan.planType}</td>
                        <td>
                            <input type="number" name="plans[${status.index}].monthlyPrice"
                                   value="${plan.monthlyPrice}" class="form-control" step="1000" />
                        </td>
                        <td>
                            <input type="number" name="plans[${status.index}].annualPrice"
                                   value="${plan.annualPrice}" class="form-control" step="1000" />
                        </td>
                        <td>
                            <select name="plans[${status.index}].maintainOldPrice" class="form-select">
                                <option value="" ${empty plan.maintainOldPrice ? 'selected' : ''}>없음</option>
                                <option value="Y" ${plan.maintainOldPrice == 'Y' ? 'selected' : ''}>유지</option>
                                <option value="N" ${plan.maintainOldPrice == 'N' ? 'selected' : ''}>미유지</option>
                            </select>
                        </td>
                        <td>
                            <input type="number" name="plans[${status.index}].maximumPeople"
                                   value="${plan.maximumPeople}" class="form-control" step="10" />
                        </td>
                        <input type="hidden" name="plans[${status.index}].planNo" value="${plan.planNo}" />
                        <input type="hidden" name="plans[${status.index}].planType" value="${plan.planType}" />
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <div class="text-end">
            <button type="submit" class="btn btn-primary">변경사항 저장</button>
        </div>
    </form>
</div>
</section>

<script src="${pageContext.request.contextPath}/resources/sepgruppe/js/subscription/subscriptionPlanManage.js"></script>
