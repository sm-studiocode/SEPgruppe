<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/groupware/css/sidebar/sidepopup.css" />
 <!-- Sidebar -->
<div class="sidebar" data-background-color="dark">
  <div class="sidebar-logo">
    <!-- Logo Header -->
    <div class="logo-header" data-background-color="dark">
      <a href="<c:url value='/${companyNo}/groupware'/>" class="logo">
        <img
          src="${pageContext.request.contextPath }/resources/groupware/images/gruppeware.png"
          alt="navbar brand"
          class="navbar-brand"
          height="50"
          style="outline: none;"
        />
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
  <div class="sidebar-wrapper scrollbar scrollbar-inner">
    <div class="sidebar-content">
      <ul class="nav nav-secondary">
        <li class="nav-section">
          <span class="sidebar-mini-icon">
            <i class="fa fa-ellipsis-h"></i>
          </span>
          <h4 class="text-section">Components</h4>
        </li>
        <li class="nav-item">
          <a data-bs-toggle="collapse" href="#sidebarLayout">
            <i class="fas fa-user-clock"></i>
            <p>근태관리</p>
            <span class="caret"></span>
          </a>
          <div class="collapse" id="sidebarLayout">
            <ul class="nav nav-collapse">
              <li>
                <a href="<c:url value='/${companyNo}/dclz/mydclz'/>">
                  <span class="sub-item">내 근태 현황</span>
                </a>
              </li>
              <li>
                <a href="<c:url value='/${companyNo}/annual/myannual'/>">
                  <span class="sub-item">내 연차 내역</span>
                </a>
              </li>
              <li>
                <a href="<c:url value='/${companyNo}/employee/myEmployee'/>">
                  <span class="sub-item">내 인사 정보</span>
                </a>
              </li>
            </ul>
          </div>
        </li>
        <li class="nav-item">
          <a href="<c:url value='/${companyNo}/project'/>">
            <i class="fas fa-project-diagram"></i>
            <p>공유 프로젝트</p>
          </a>
        </li>
        <li class="nav-item">
          <a href="<c:url value='/${companyNo}/apprHome' />">
            <i class="fas fa-file-signature"></i>
            <p>전자결재</p>
          </a>
        </li>
        <li class="nav-item">
		  <a href="<c:url value='/${companyNo}/notice'/>">
		    <i class="fas fa-bullhorn"></i>
		    <p>공지사항</p>
		  </a>
		</li>
        <li class="nav-item">
            <a href="<c:url value='/${companyNo}/mail'/>">
              <i class="fas fa-envelope"></i>
              <p>메일함</p>
            </a>
        </li>
        <li class="nav-item">
          <a href="<c:url value='/${companyNo}/schedule/'/>">
            <i class="fas fa-calendar-alt"></i>
            <p>일정관리</p>
          </a>
        </li>
        <li class="nav-item">
          <a data-bs-toggle="collapse" href="#tables">
            <i class="fas fa-calendar-check"></i>
            <p>예약관리</p>
            <span class="caret"></span>
          </a>
          <div class="collapse" id="tables">
            <ul class="nav nav-collapse">
              <li>
				    <a href="<c:url value='/${companyNo}/reservation'/>">
				        <span class="sub-item">회의실 예약</span>
				    </a>
			  </li>
              <li>
                <a href="<c:url value='/${companyNo}/meetingroom'/>">
                  <span class="sub-item">회의실 목록</span>
                </a>
              </li>
            </ul>
          </div>
        </li>
        <li class="nav-item">
          <a href="<c:url value='/${companyNo}/webhard'/>">
            <i class="fas fa-hdd"></i>
            <p>웹하드</p>
          </a>
        </li>
        <li class="nav-item">
          <a data-bs-toggle="collapse" href="#submenu">
            <i class="fas fa-address-book"></i>
            <p>주소록</p>
            <span class="caret"></span>
          </a>
          <div class="collapse" id="submenu">
            <ul class="nav nav-collapse">
              <li>
                <a href="<c:url value='/${companyNo}/address'/>">
                  <span class="sub-item">주소록 목록</span>
                </a>
              </li>
              <li>
		        <a href="<c:url value='/${companyNo}/address/new'/>">
		          <span class="sub-item">주소록 등록</span>
		        </a>
		      </li>
            </ul>
          </div>
        </li>
		<li class="nav-item">
		  <a href="<c:url value='/${companyNo}/community'/>">
		    <i class="fas fa-comments"></i>
		    <p>커뮤니티</p>
		  </a>
		</li>
        <li class="nav-item">
			<a data-bs-toggle="collapse" href="#survey">
				<i class="far fa-chart-bar"></i>
				<p>설문/투표</p>
				<span class="caret"></span>
			  </a>
			  <div class="collapse" id="survey">
				<ul class="nav nav-collapse">
				  <li>
					<a href="<c:url value='/${companyNo}/surveyApi/list'/>">
					  <span class="sub-item">설문/투표 조회</span>
					</a>
				  </li>
			    </ul>
			  </div>
		  </li>
		<li class="nav-item">
				  <a href="#" onclick="showOrganizationPopup()">
				    <i class="fas fa-users"></i>
				    <p>조직도 조회</p>
				  </a>
				</li>
		      </ul>
		    </div>
		  </div>
		</div>
<!-- 조직도 FancyTree 팝업 -->
<div id="organizationPopup" class="popup"
     data-company-no="${companyNo}"
     data-context-path="${pageContext.request.contextPath}">
  <div class="popup-content">
    <span class="close-btn" onclick="closeOrganizationPopup()">&times;</span>
    <h2>조직도</h2>
    <!-- 검색창 -->
    <div class="search-box">
      <input type="text" id="employee-search" placeholder="사원이름 검색" />
      <button id="search-btn" class="btn btn-primary btn-sm">검색</button>
    </div>

    <div class="popup-body">
      <!-- 트리 -->
      	<div id="depTree" class="tree" ></div>
  </div>
</div>
</div>
<!-- 상세 정보 팝업 (별도 팝업) -->
<div id="detailPopup" class="popup" style="display: none; float: right;">
  <div class="popup-content">
    <span class="close-btn" onclick="closeDetailPopup()">&times;</span>
    <div id="detailPopupContent"></div>
  </div>
</div>
<script src="${pageContext.request.contextPath }/resources/groupware/js/sidebar/sidebar.js"></script>
<script src="${pageContext.request.contextPath }/resources/groupware/js/sidebar/sidepopup.js"></script>