<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<security:authentication property="principal.realUser" var="realUser"/> <!-- 로그인 사용자 -->
<security:authorize access="hasAuthority('EMPLOYEE')" var="isEmployee" />
<security:authorize access="hasAuthority('COMPANY')"  var="isCompany" />
<security:authorize access="hasAuthority('PROVIDER')" var="isProvider" />

<!-- ✅ 알람용 설정값(EMPLOYEE/COMPANY 공통) -->
<c:set var="alarmUserId" value="" />
<c:choose>
  <c:when test="${isEmployee}">
    <c:set var="alarmUserId" value="${realUser.empId}" />
  </c:when>
  <c:when test="${isCompany}">
    <c:set var="alarmUserId" value="${realUser.contactId}" />
  </c:when>
</c:choose>

<c:if test="${not empty alarmUserId}">
  <div id="alarmConfig"
       data-context-path="${pageContext.request.contextPath}"
       data-user-id="${alarmUserId}">
  </div>
</c:if>

<div class="main-header-logo">
  <div class="logo-header" data-background-color="dark">
    <a href="<c:url value='/groupware'/>" class="logo">
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
</div>

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

        <!-- ✅ 알림(종) : Offcanvas 오픈 트리거 -->
        <li class="nav-item topbar-icon hidden-caret ms-3">
          <a class="nav-link" href="#" id="notifDropdown">
            <i class="fa fa-bell"></i>
            <span class="notification" id="notifBadge" style="display:none;"></span>
          </a>
        </li>

        <!-- (나머지 원본 그대로) -->
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

              <!-- EMPLOYEE -->
              <c:if test="${isEmployee}">
                <c:if test="${empty realUser.empImg }">
                  <img src="<c:url value='${attachFiles }default/defaultImage.jpg'/>" class="avatar-img rounded">
                </c:if>
                <c:if test="${not empty realUser.empImg }">
                  <img src="<c:url value='${attachFiles }${realUser.empImg}'/>" class="avatar-img rounded">
                </c:if>
              </c:if>

              <!-- COMPANY -->
              <c:if test="${isCompany}">
                <img src="<c:url value='${attachFiles }default/defaultImage.jpg'/>" class="avatar-img rounded">
              </c:if>

            </div>

            <span class="profile-username">
              <c:if test="${isEmployee}">
                <span class="fw-bold">${realUser.empNm}</span>
              </c:if>

              <c:if test="${isCompany}">
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
                    <c:if test="${isEmployee}">
                      <h4>${realUser.empNm}</h4>
                      <p class="text-muted">${realUser.empEmail}</p>
                      <a href="<c:url value='/employee/mypage'/>"
                         class="btn btn-xs btn-secondary btn-sm">마이페이지</a>
                    </c:if>

                    <c:if test="${isCompany}">
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

<!-- ✅ Alarm Offcanvas (우측 패널) -->
<div class="offcanvas offcanvas-end" tabindex="-1" id="alarmOffcanvas" aria-labelledby="alarmOffcanvasLabel">
  <div class="offcanvas-header">
    <div class="d-flex align-items-center gap-2">
      <h5 class="offcanvas-title mb-0" id="alarmOffcanvasLabel">알림</h5>
      <span class="badge bg-danger" id="alarmOffBadge" style="display:none;"></span>
    </div>

    <div class="d-flex gap-2">
      <button type="button" class="btn btn-sm btn-outline-secondary" id="btnAlarmReadAll">전체 읽음</button>
      <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
    </div>
  </div>

  <div class="offcanvas-body pt-2">
    <!-- 탭 -->
    <ul class="nav nav-pills nav-sm mb-3" id="alarmTabs">
      <li class="nav-item">
        <button class="nav-link active" data-filter="all" type="button">전체</button>
      </li>
      <li class="nav-item">
        <button class="nav-link" data-filter="unread" type="button">미읽음</button>
      </li>
    </ul>

    <!-- 리스트 -->
    <div id="alarmPanelList" class="d-flex flex-column gap-2"></div>

    <!-- 빈 상태 -->
    <div id="alarmPanelEmpty" class="text-center text-muted py-5" style="display:none;">
      <div style="font-size:40px;">🔔</div>
      <div class="mt-2">최근 알림이 없습니다.</div>
    </div>

    <!-- 더 보기 -->
    <div class="d-grid mt-3">
      <button type="button" class="btn btn-light" id="btnAlarmMore">더 보기</button>
    </div>

    <!-- 고급 보기 -->
    <div class="text-center mt-3">
      <a href="<c:url value='/alarm'/>" class="text-muted" style="font-size:12px;">알림함으로 이동</a>
    </div>
  </div>
</div>

<div id="widget-select-wrapper" style="display: none; position: absolute; top: 80px; left: 0; width: 100%; z-index: 9999;">
  <div id="widget-select-container"></div>
</div>

<script src="${pageContext.request.contextPath }/resources/groupware/js/header/header.js"></script>
<script src="//code.tidio.co/tvnrajzvhhr5y1zqhstkbf50noqw7rkf.js" async></script>
