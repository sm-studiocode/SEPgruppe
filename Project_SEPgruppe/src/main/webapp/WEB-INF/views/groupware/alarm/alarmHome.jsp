<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

<security:authentication property="principal.realUser" var="realUser" />
<security:authentication property="name" var="alarmUserId" />

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
<link href="${pageContext.request.contextPath }/resources/groupware/css/mail/mailList.css" rel="stylesheet">

<!-- ✅ 설정값: JS에서만 사용 (회사/직원 공용) -->
<c:if test="${not empty alarmUserId}">
  <div id="alarmConfig"
       data-context-path="${pageContext.request.contextPath}"
       data-user-id="${alarmUserId}">
  </div>
</c:if>

<div class="mail-container">
    <!-- 왼쪽 사이드 메뉴 -->
    <div class="mailSidebar">
        <h3>🔔 알림함</h3>

        <a href="#" class="menu-item" data-filter="all">전체 알림</a>
        <a href="#" class="menu-item" data-filter="unread">
            읽지 않은 알림 <span id="unreadBadge" style="margin-left:6px;"></span>
        </a>

        <hr>
        <button type="button" class="btn btn-sm btn-outline-secondary" id="btnReadAll">전체 읽음</button>
    </div>

    <!-- 오른쪽 알림 리스트 -->
    <div class="content-area">
        <div id="noAlarm" class="no-mail" style="display:none;">📭 받은 알림이 없습니다.</div>

        <table class="table table-head-bg-primary mt-4" id="alarmTable">
            <thead>
                <tr>
                    <th scope="col">선택</th>
                    <th scope="col">종류</th>
                    <th scope="col">내용</th>
                    <th scope="col">날짜</th>
                </tr>
            </thead>
            <tbody id="alarmTbody">
                <!-- ✅ JS가 렌더링 -->
            </tbody>
        </table>
    </div>
</div>

<script src="${pageContext.request.contextPath}/resources/groupware/js/alarm/alarmHome.js"></script>
