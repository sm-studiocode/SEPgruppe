<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>    
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/groupware/css/index/indexGW.css" />
<security:authentication property="principal.realUser" var="realUser"/>
<title>${realUser.companyNo } - 메인페이지</title>

<!-- ✅ JS에서 읽을 설정값을 data로 내려줌 -->
<div id="indexGWConfig"
     data-context-path="${pageContext.request.contextPath}"
     data-company-no="${companyNo}"
     data-emp-id="${realUser.empId}">
</div>

<!-- 위젯 전체 레이아웃 -->
<div id="widgetContainer">
  <div id="leftColumn" class="column">
    <c:if test="${empty leftWidgets}">
      <div class="placeholder">위젯을 여기에 드래그하세요</div>
    </c:if>

    <c:forEach var="widget" items="${leftWidgets}">
      <div class="widget-box" data-widget-id="${widget.widgetId}" id="${widget.widgetId}-widget">
        <c:choose>
          <c:when test="${widget.widgetId eq 'dclz'}">
            <c:import url="${pageContext.request.contextPath}/${companyNo}/dclz/main-panel" />
          </c:when>
          <c:when test="${widget.widgetId eq 'schedule'}">
            <c:import url="${pageContext.request.contextPath}/${companyNo}/widget/schedule" />
          </c:when>
          <c:when test="${widget.widgetId eq 'notice'}">
            <c:import url="${pageContext.request.contextPath}/${companyNo}/widget/notice" />
          </c:when>
          <c:when test="${widget.widgetId eq 'approval-waiting'}">
            <c:import url="${pageContext.request.contextPath}/${companyNo}/widget/approval-waiting" />
          </c:when>
          <c:when test="${widget.widgetId eq 'project-task'}">
            <c:import url="${pageContext.request.contextPath}/${companyNo}/widget/project-task" />
          </c:when>
        </c:choose>
      </div>
    </c:forEach>
  </div>

  <div id="rightColumn" class="column">
    <c:if test="${empty rightWidgets}">
      <div class="placeholder">위젯을 여기에 드래그하세요</div>
    </c:if>

    <c:forEach var="widget" items="${rightWidgets}">
      <div class="widget-box" data-widget-id="${widget.widgetId}" id="${widget.widgetId}-widget">
        <c:choose>
          <c:when test="${widget.widgetId eq 'dclz'}">
            <c:import url="${pageContext.request.contextPath}/${companyNo}/dclz/main-panel" />
          </c:when>
          <c:when test="${widget.widgetId eq 'schedule'}">
            <c:import url="${pageContext.request.contextPath}/${companyNo}/widget/schedule" />
          </c:when>
          <c:when test="${widget.widgetId eq 'notice'}">
            <c:import url="${pageContext.request.contextPath}/${companyNo}/widget/notice" />
          </c:when>
          <c:when test="${widget.widgetId eq 'approval-waiting'}">
            <c:import url="${pageContext.request.contextPath}/${companyNo}/widget/approval-waiting" />
          </c:when>
          <c:when test="${widget.widgetId eq 'project-task'}">
            <c:import url="${pageContext.request.contextPath}/${companyNo}/widget/project-task" />
          </c:when>
        </c:choose>
      </div>
    </c:forEach>
  </div>
</div>

<lottie-player
    id="background-lottie"
    src="${pageContext.request.contextPath}/resources/groupware/images/Animation - 1744694190711.json"
    background="transparent"
    speed="1"
    loop
    autoplay
    style="position: fixed; bottom: 0; left: 85%; transform: translateX(-50%); width: 300px; height: 300px; z-index: -1;">
</lottie-player>

<!-- ✅ 이제 JSP inline script 없음. 전부 indexGW.js로 -->
<script src="${pageContext.request.contextPath }/resources/groupware/js/index/indexGW.js"></script>
<script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/index.global.min.js'></script>
<script src="https://cdn.jsdelivr.net/npm/@fullcalendar/interaction@6.1.15/index.global.min.js"></script>
<script src="https://unpkg.com/lottie-web@latest/build/player/lottie.min.js"></script>
<script src="https://unpkg.com/@lottiefiles/lottie-player@latest/dist/lottie-player.js"></script>
