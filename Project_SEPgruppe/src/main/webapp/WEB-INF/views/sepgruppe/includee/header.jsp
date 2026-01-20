<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<header>
    <nav class="navbar navbar-expand-lg">
        <div class="container">

            <!-- 로고 -->
            <a class="navbar-brand" href="/sep">
                <img id="seplogo" alt="tree" src="${pageContext.request.contextPath}/resources/sepgruppe/images/favicon.png">
            </a>

            <div class="collapse navbar-collapse" id="navbarNav">
<ul class="navbar-nav ms-lg-5 me-lg-auto">

    <!-- 로그인 된 경우: PROVIDER면 관리자메뉴, 아니면 일반메뉴 -->
    <security:authorize access="isAuthenticated()">
        <security:authentication property="principal" var="principal"/>

        <c:choose>
            <c:when test="${not empty principal.realUser and principal.realUser.target eq 'PROVIDER'}">
                <!-- 관리자 메뉴 -->
                <li class="nav-item">
                    <a class="nav-link active" id="btnNonDrop" href="/sep/provider">대시보드</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" id="btnNonDrop" href="/sep/company">고객사 목록</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" id="navbarLightDropdownMenuLink" href="/sep/subscriptionPlan/manage">구독 관리</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" id="navbarLightDropdownMenuLink" href="/sep/payment">자동결제내역</a>
                </li>
            </c:when>

            <c:otherwise>
                <!-- 일반 사용자 메뉴 -->
                <li class="nav-item">
                    <a class="nav-link active" href="/sep/subscriptionPlan">PRODUCT</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" href="#">PROCESS</a>
                </li>
            </c:otherwise>
        </c:choose>
    </security:authorize>

    <!-- 로그인 안 된 경우 -->
    <security:authorize access="isAnonymous()">
        <li class="nav-item">
            <a class="nav-link active" href="/sep/subscriptionPlan">PRODUCT</a>
        </li>
        <li class="nav-item">
            <a class="nav-link active" href="${pageContext.request.contextPath}/login">LOGIN</a>
        </li>
    </security:authorize>
</ul>

                <!-- 로그인 된 경우 -->
                <security:authorize access="isAuthenticated()">
                    <security:authentication property="principal" var="principal"/>

                    <div class="d-none d-lg-block dropdown">
                        <a class="navbar-icon bi-person" data-bs-toggle="dropdown" aria-expanded="false"></a>

                        <ul class="dropdown-menu dropdown-menu-light">

                            <!-- COMPANY 사용자 -->
                            <c:if test="${principal.realUser.target eq 'COMPANY'}">
                                <li>
                                    <a class="dropdown-item" href="/sep/company/mypage">마이페이지</a>
                                </li>
                            </c:if>

                            <!-- PROVIDER (관리자) -->
                            <c:if test="${principal.realUser.target eq 'PROVIDER'}">
                                <li>
                                    <a class="dropdown-item" href="#">관리자모드</a>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#exampleModal1">알림작성</a>
                                </li>
                            </c:if>

                            <li>
                                <a class="dropdown-item" href="<c:url value='/login/logout'/>">Logout</a>
                            </li>
                        </ul>
                    </div>
                </security:authorize>
            </div>
        </div>
    </nav>

    <!-- Modal -->
    <div class="modal fade" id="exampleModal1" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-sm">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5">알림작성</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    ...
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        Close
                    </button>
                </div>
            </div>
        </div>
    </div>
</header>
