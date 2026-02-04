<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<security:authentication property="principal.realUser" var="realUser"/> <!-- 로그인 사용자 (RealUserWrapper.realUser) -->

<%-- 권한/타입 분기 플래그 --%>
<security:authorize access="hasAuthority('EMPLOYEE')" var="isEmployee" />
<security:authorize access="hasAuthority('COMPANY')"  var="isCompany" />
<security:authorize access="hasAuthority('PROVIDER')" var="isProvider" />

<div class="main-header-logo">
    <!-- Logo Header -->
    <div class="logo-header" data-background-color="dark">

        <!-- ✅ 정적 index.html 제거, 그룹웨어 홈으로 -->
        <!-- ✅ companyNo를 URL에서 제거(세션 기반) -->
        <a href="<c:url value='/groupware'/>" class="logo">
            <!-- ✅ 정적 assets 경로 제거 -->
            <img
              src="${pageContext.request.contextPath}/resources/groupware/kaiadmin/assets/img/kaiadmin/logo_light.svg"
              alt="navbar brand"
              class="navbar-brand"
              height="20" />
        </a>

        <div class="nav-toggle">
            <button class="btn btn-toggle toggle-sidebar">
                <i class="gg-menu-right"></i>
            </button>
            <button class="btn btn-toggle sidenav-toggler">
                <i class="gg-menu-left"></i>
            </button>
        </div>
        <button class="topbar-toggler more">
            <i class="gg-more-vertical-alt"></i>
        </button>
    </div>
    <!-- End Logo Header -->
</div>

<!-- Navbar Header -->
<nav class="navbar navbar-header navbar-header-transparent navbar-expand-lg border-bottom">
    <div class="container-fluid d-flex justify-content-end">
        <div class="profile">
            <ul class="navbar-nav topbar-nav ms-md-auto align-items-center">

                <li class="nav-item topbar-icon dropdown hidden-caret ms-3">
                    <a class="nav-link dropdown-toggle" onclick="openMessengerWindow(); return false;"
                       id="messageDropdown" role="button" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <i class="fas fa-comments"></i>
                    </a>
                </li>

                <li class="nav-item topbar-icon dropdown hidden-caret ms-3">
                    <a class="nav-link dropdown-toggle" href="#" id="notifDropdown" role="button"
                       data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <i class="fa fa-bell"></i>
                        <span class="notification">4</span>
                    </a>
                    <ul class="dropdown-menu notif-box animated fadeIn" aria-labelledby="notifDropdown">
                        <li>
                            <div class="dropdown-title">You have 4 new notification</div>
                        </li>
                        <li>
                            <div class="notif-scroll scrollbar-outer">
                                <div class="notif-center">
                                    <a href="#">
                                        <div class="notif-icon notif-primary"><i class="fa fa-user-plus"></i></div>
                                        <div class="notif-content">
                                            <span class="block"> New user registered </span>
                                            <span class="time">5 minutes ago</span>
                                        </div>
                                    </a>
                                    <a href="#">
                                        <div class="notif-icon notif-success"><i class="fa fa-comment"></i></div>
                                        <div class="notif-content">
                                            <span class="block"> Rahmad commented on Admin </span>
                                            <span class="time">12 minutes ago</span>
                                        </div>
                                    </a>
                                    <a href="#">
                                        <div class="notif-img">
                                            <img src="${pageContext.request.contextPath}/resources/groupware/kaiadmin/assets/img/profile2.jpg" alt="Img Profile" />
                                        </div>
                                        <div class="notif-content">
                                            <span class="block"> Reza send messages to you </span>
                                            <span class="time">12 minutes ago</span>
                                        </div>
                                    </a>
                                    <a href="#">
                                        <div class="notif-icon notif-danger"><i class="fa fa-heart"></i></div>
                                        <div class="notif-content">
                                            <span class="block"> Farrah liked Admin </span>
                                            <span class="time">17 minutes ago</span>
                                        </div>
                                    </a>
                                </div>
                            </div>
                        </li>

                        <%-- ✅ 세션 기반 경로로 변경 --%>
                        <li>
                            <a class="see-all" href="<c:url value='/alarm'/>">
                                See all notifications<i class="fa fa-angle-right"></i>
                            </a>
                        </li>
                    </ul>
                </li>

                <li class="nav-item topbar-icon dropdown hidden-caret ms-3">
                    <a class="nav-link" data-bs-toggle="dropdown" href="#" aria-expanded="false">
                        <i class="fas fa-star"></i>
                    </a>
                    <div class="dropdown-menu quick-actions animated fadeIn">
                        <div class="quick-actions-header">
                            <span class="title mb-1">바로 가기</span>
                        </div>
                        <div class="quick-actions-scroll scrollbar-outer">
                            <div class="quick-actions-items">
                                <div class="row m-0">

                                    <%-- ✅ 세션 기반 경로로 변경 --%>
                                    <a class="col-6 col-md-4 p-0" href="<c:url value='/schedule/'/>">
                                        <div class="quick-actions-item">
                                            <div class="avatar-item bg-danger rounded-circle">
                                                <i class="far fa-calendar-alt"></i>
                                            </div>
                                            <span class="text">나의 일정</span>
                                        </div>
                                    </a>

                                    <a class="col-6 col-md-4 p-0" href="<c:url value='/mail'/>">
                                        <div class="quick-actions-item">
                                            <div class="avatar-item bg-success rounded-circle">
                                                <i class="fas fa-envelope"></i>
                                            </div>
                                            <span class="text">메일함</span>
                                        </div>
                                    </a>

                                    <%-- ✅ 마이페이지는 직원/회사 분기 --%>
                                    <c:if test="${isEmployee}">
                                        <a class="col-6 col-md-4 p-0" href="<c:url value='/employee/mypage'/>">
                                            <div class="quick-actions-item">
                                                <div class="avatar-item bg-warning rounded-circle">
                                                    <i class="fas fa-smile"></i>
                                                </div>
                                                <span class="text">마이 페이지</span>
                                            </div>
                                        </a>
                                    </c:if>

                                    <c:if test="${isCompany}">
                                        <a class="col-6 col-md-4 p-0" href="<c:url value='/company/mypage'/>">
                                            <div class="quick-actions-item">
                                                <div class="avatar-item bg-warning rounded-circle">
                                                    <i class="fas fa-smile"></i>
                                                </div>
                                                <span class="text">마이 페이지</span>
                                            </div>
                                        </a>
                                    </c:if>

                                </div>

                                <img src="${pageContext.request.contextPath }/resources/groupware/images/dog.png"
                                     alt="개"
                                     class="dog">
                            </div>
                        </div>
                    </div>
                </li>

                <li class="nav-item topbar-user dropdown hidden-caret ms-3">
                    <a class="dropdown-toggle profile-pic" data-bs-toggle="dropdown" href="#" aria-expanded="false">
                        <div class="avatar-sm">
<%--                             <spring:eval expression="@fileInfo.attachFiles" var="attachFiles"/> --%>

                            <%-- =========================
                                 EMPLOYEE 프로필 이미지/이름
                                 ========================= --%>
                            <c:if test="${isEmployee}">
                                <c:if test="${empty realUser.empImg }">
                                    <img src="<c:url value='${attachFiles }default/defaultImage.jpg'/>" class="avatar-img rounded">
                                </c:if>
                                <c:if test="${not empty realUser.empImg }">
                                    <img src="<c:url value='${attachFiles }${realUser.empImg}'/>" class="avatar-img rounded">
                                </c:if>
                            </c:if>

                            <%-- =========================
                                 COMPANY 프로필(기본 이미지)
                                 - CompanyVO에 이미지가 없다고 가정
                                 ========================= --%>
                            <c:if test="${isCompany}">
                                <img src="<c:url value='${attachFiles }default/defaultImage.jpg'/>" class="avatar-img rounded">
                            </c:if>

                        </div>

                        <span class="profile-username">
                            <c:if test="${isEmployee}">
                                <span class="fw-bold">${realUser.empNm}</span>
                            </c:if>

                            <c:if test="${isCompany}">
                                <%-- CompanyVO에 필드명 맞춰서 바꿔: companyNm / contactNm 등 --%>
                                <span class="fw-bold">
                                    <c:out value="${realUser.companyName}" default="회사 계정"/>
                                </span>
                            </c:if>

                            <c:if test="${isProvider}">
                                <span class="fw-bold">
                                    <c:out value="${realUser.providerNm}" default="PROVIDER"/>
                                </span>
                            </c:if>
                        </span>
                    </a>

                    <ul class="dropdown-menu dropdown-user animated fadeIn">
                        <div class="dropdown-user-scroll scrollbar-outer">
                            <li>
                                <div class="user-box">
                                    <div class="avatar-lg">
<%--                                         <spring:eval expression="@fileInfo.attachFiles" var="attachFiles"/> --%>

                                        <c:if test="${isEmployee}">
                                            <c:if test="${empty realUser.empImg }">
                                                <img src="<c:url value='${attachFiles }default/defaultImage.jpg'/>" class="avatar-img rounded">
                                            </c:if>
                                            <c:if test="${not empty realUser.empImg }">
                                                <img src="<c:url value='${attachFiles }${realUser.empImg}'/>" class="avatar-img rounded">
                                            </c:if>
                                        </c:if>

                                        <c:if test="${isCompany}">
                                            <img src="<c:url value='${attachFiles }default/defaultImage.jpg'/>" class="avatar-img rounded">
                                        </c:if>

                                        <c:if test="${isProvider}">
                                            <img src="<c:url value='${attachFiles }default/defaultImage.jpg'/>" class="avatar-img rounded">
                                        </c:if>
                                    </div>

                                    <div class="u-text">

                                        <%-- ===== 이름/이메일 분기 ===== --%>
                                        <c:if test="${isEmployee}">
                                            <h4>${realUser.empNm}</h4>
                                            <p class="text-muted">${realUser.empEmail}</p>
                                            <a href="<c:url value='/employee/mypage'/>"
                                               class="btn btn-xs btn-secondary btn-sm">마이페이지</a>
                                        </c:if>

                                        <c:if test="${isCompany}">
                                            <%-- CompanyVO 필드명 맞춰서 수정 필요 --%>
                                            <h4><c:out value="${realUser.companyName}" default="회사 계정"/></h4>
                                            <p class="text-muted"><c:out value="${realUser.contactEmail}" default=""/></p>
                                            <a href="<c:url value='/company/mypage'/>"
                                               class="btn btn-xs btn-secondary btn-sm">마이페이지</a>
                                        </c:if>

                                        <c:if test="${isProvider}">
                                            <h4><c:out value="${realUser.providerNm}" default="PROVIDER"/></h4>
                                            <p class="text-muted"><c:out value="${realUser.providerEmail}" default=""/></p>
                                            <a href="<c:url value='/'/>"
                                               class="btn btn-xs btn-secondary btn-sm">홈</a>
                                        </c:if>

                                    </div>
                                </div>
                            </li>

                            <li>
                                <div class="dropdown-divider"></div>

                                <%-- ✅ 관리자 링크: 네가 쓰는 권한명이 ADMIN이면 hasAuthority가 더 정확함 --%>
								<security:authorize access="hasRole('ADMIN')">
								    <a class="dropdown-item" href="<c:url value='/adminpage'/>" target="_blank">관리자 페이지</a>
								    <a class="dropdown-item" href="<c:url value='/'/>">셉 홈페이지 이동</a>
								</security:authorize>

                                <a class="dropdown-item" href="#">Inbox</a>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item" href="#">Account Setting</a>
                                <div class="dropdown-divider"></div>

                                <a class="dropdown-item" onclick="logoutTidio()" style="cursor: pointer;">로그아웃</a>
                            </li>
                        </div>
                    </ul>
                </li>

            </ul>
        </div>
    </div>
</nav>

<div id="widget-select-wrapper" style="display: none; position: absolute; top: 80px; left: 0; width: 100%; z-index: 9999;">
    <div id="widget-select-container"></div>
</div>

<script src="${pageContext.request.contextPath }/resources/groupware/js/header/header.js"></script>
<script src="//code.tidio.co/tvnrajzvhhr5y1zqhstkbf50noqw7rkf.js" async></script>
