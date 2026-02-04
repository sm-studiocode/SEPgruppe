<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일                수정자           수정내용
 *  ============       ============== =======================
 *  2025. 3. 13.       KMJ            최초 생성
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

<security:authentication property="principal.realUser" var="realUser"/>

<link href="${pageContext.request.contextPath}/resources/groupware/css/admin/adminPage.css" rel="stylesheet">

<title>${company.companyName} - 관리자 페이지</title>

<div class="biggestContainer">
    <div class="contentContainer">
        <br><br><br><br>

        <div class="content">
            <div class="table-content">
                <div class="basicInfo">
                    <div class="card-header">
                        <div class="card-title">기본 정보</div>
                        <a class="btn btn-primary"
                           href="${pageContext.request.contextPath}/${companyNo}/employee/new"
                           role="button">근태관리</a>
                    </div>

                    <div class="card-body">
                        <table class="table mt-3">
                            <thead>
                                <tr>
                                    <th scope="row">사이트명</th>
                                    <td>${company.companyName}</td>
                                    <th scope="row">담당자</th>
                                    <td>관리자</td>
                                </tr>
                            </thead>

                            <tbody>
                                <tr>
                                    <th scope="row">사이트URL</th>
                                    <td>localhost${pageContext.request.contextPath}/${companyNo}</td>
                                    <th scope="row">사이트 아이디</th>
                                    <td>${realUser.userId}</td>
                                </tr>

                                <tr>
                                    <th scope="row">도메인명</th>
                                    <td>localhost${pageContext.request.contextPath}/${companyNo}</td>
                                    <th scope="row">도입일자</th>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty subStart}">
                                                ${subStart.subscriptionStart}
                                            </c:when>
                                            <c:otherwise>
                                                -
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>

                                <tr>
                                    <th scope="row">총 계정수</th>
                                    <td>${countEmp}</td>
                                    <th scope="row">활동 계정</th>
                                    <td>${countActiveEmp}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="table-content">
                <h3>통계</h3>

                <div class="col-md-6">
                    <div class="card">
                        <div class="card-header">
                            <div class="card-title">Line Chart</div>
                        </div>
                        <div class="card-body">
                            <div class="chart-container">
                                <canvas id="lineChart"></canvas>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/resources/groupware/js/admin/adminPage.js"></script>
