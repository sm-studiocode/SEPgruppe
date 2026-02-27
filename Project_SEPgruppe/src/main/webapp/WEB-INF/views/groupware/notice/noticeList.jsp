<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<%-- ✅ 타입(CompanyVO/EmployeeVO) 상관없이 안전한 로그인 아이디(= username) --%>
<security:authentication property="name" var="loginId"/>

<%-- (옵션) realUser가 필요하면 유지 가능. 단, empId 같은 필드는 직접 찍지 말 것 --%>
<security:authentication property="principal.realUser" var="realUser"/>

<!-- noticeList css 코드 -->
<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/groupware/css/notice/noticeList.css" />

<div class="container">
  <div class="page-inner">
    <!-- 상단 분류 -->
    <div class="page-header">
      <h3 class="fw-bold mb-3">공지사항</h3>
      <ul class="breadcrumbs mb-3">
        <!-- HOME -->
        <li class="nav-home">
          <a href="<c:url value='/groupware'/>">
            <i class="icon-home"></i>
          </a>
        </li>
        <!-- '>' 표시 아이콘 -->
        <li class="separator">
          <i class="icon-arrow-right"></i>
        </li>
        <!-- 분류 -->
        <li class="nav-item">
          <a href="<c:url value='/notice'/>">공지사항</a>
        </li>
      </ul>
    </div>

    <!-- 분류 선택 -->
    <div class="d-flex category">
      <div class="form-check">
        <input class="form-check-input" type="radio" name="category" id="flexRadioDefault1" value="all"
               onchange="fn_change(this)"
               <c:if test="${category eq 'all'}">checked</c:if> />
        <label class="form-check-label" for="flexRadioDefault1">전체</label>
      </div>

      <div class="form-check">
        <input class="form-check-input" type="radio" name="category" id="flexRadioDefault2" value="company"
               onchange="fn_change(this)"
               <c:if test="${category eq 'company'}">checked</c:if> />
        <label class="form-check-label" for="flexRadioDefault2">전사공지</label>
      </div>

      <div class="form-check">
        <input class="form-check-input" type="radio" name="category" id="flexRadioDefault3" value="depart"
               onchange="fn_change(this)"
               <c:if test="${category eq 'depart'}">checked</c:if> />
        <label class="form-check-label" for="flexRadioDefault3">부서공지</label>
      </div>
    </div>

    <%-- ✅ 등록/삭제 버튼: 권한으로 제어 --%>
    <c:if test="${empty subActive or subActive}">
      <%-- ✅ 여기만 변경: ROLE_ADMIN만 → 기능 권한들 전부 허용 --%>
      <security:authorize access="hasAnyAuthority('ROLE_TENANT_ADMIN','ROLE_NOTICE_ADMIN','ROLE_NOTICE_DEPT_ADMIN','ROLE_ADMIN')">

        <!-- ✅ 버튼툴바: 배경이 버튼 영역만큼만 보이게 -->
        <div class="notice-toolbar-wrap">
          <div class="notice-toolbar">
            <a href="${pageContext.request.contextPath}/notice/excelDownload"
               class="btn btn-outline-success btn-sm px-2 py-1 d-flex align-items-center"
               id="downExcel">
              <img src="<c:url value='/resources/groupware/images/excel.png'/>" id="excelUrl">
              목록 다운로드
            </a>

            <c:url var="addUrl" value="/notice/new"/>
            <button type="button" id="addRowButton" class="btn btn-sm btn-info"
                    onclick="location.href='${addUrl}'">Add</button>

            <button type="button" id="deleteButton" class="btn btn-sm btn-danger"
                    data-toggle="modal" data-target="#deleteNoticeModal">Delete</button>
          </div>
        </div>

      </security:authorize>
    </c:if>

    <!-- 테이블 -->
    <div class="table-responsive">
      <table id="add-row" class="table table-hover display">
        <thead>
          <tr>
            <th><input type="checkbox" id="select-all" /></th>
            <th>글번호</th>
            <th>분류</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
            <th>조회수</th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${not empty noticeList}">
              <c:forEach items="${noticeList}" var="notice">
                <tr class="notice-row"
                    onclick="location.href='<c:url value='/notice/${notice.noticeNo}' />'">
                  <td onclick="event.stopPropagation();">
                    <input type="checkbox" id="noticeNo" class="noticCheck" name="noticeSelect"
                           value="${notice.noticeNo}" onclick="event.stopPropagation();">
                  </td>
                  <td>${notice.rnum}</td>
                  <td>${notice.noticeCategory}</td>
                  <td>${notice.noticeTitle}</td>
                  <td>${notice.empNm} ${notice.positionName}</td>
                  <td>${notice.noticeCreatedAt}</td>
                  <td>${notice.noticeViewCount}</td>
                </tr>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <tr>
                <td class="text-center" colspan="8">조회된 데이터가 없습니다.</td>
              </tr>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>

      <!-- 페이징 처리 -->
      ${pagingHTML}

      <!-- 검색필터링 -->
      <div class="form-group" id="search-ui">
        <select class="form-select" name="searchType" id="smallSelect" data-init-value="${condition.searchType}">
          <option value>제목+내용</option>
          <option value="title">제목</option>
          <option value="content">내용</option>
        </select>

        <div class="input-icon">
          <input type="text" class="form-control" placeholder="Search for..." name="searchWord" id="searchEnter"
                 data-init-value="${condition.searchWord}"/>
          <span class="input-icon-addon">
            <i class="fa fa-search"></i>
          </span>
        </div>

        <button class="btn btn-info" id="searchBtn">검색</button>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="deleteNoticeModal" tabindex="-1" role="dialog"
     aria-labelledby="deleteTaskModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="deleteTaskModalLabel">공지사항 삭제하기</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>

      <div class="modal-body">
        정말로 이 공지사항을 삭제하시겠습니까? <br>
        삭제된 데이터는 복구 불가합니다.
      </div>

      <div class="modal-footer">
        <form action="${pageContext.request.contextPath}/notice/delete" method="post" name="deleteForm">
          <%-- ✅ CSRF 토큰 (Spring Security POST 보호) --%>
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

          <%-- ✅ CompanyVO/EmployeeVO 상관없이 안전: authentication.name 사용 --%>
          <input type="hidden" name="empId" value="${loginId}"/>
          <input type="hidden" name="noticeNo" id="noticeNoInput"/>

          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
          <button type="submit" class="btn btn-danger" id="confirmDelete">삭제</button>
        </form>
      </div>
    </div>
  </div>
</div>

<form id="search-form">
  <input type="hidden" name="page" />
  <input type="hidden" name="category" value="${category}"/>
  <input type="hidden" name="searchType" value="${condition.searchType}"/>
  <input type="hidden" name="searchWord" value="${condition.searchWord}"/>
</form>

<script>
  var noticeNo = '${noticeNo}';
</script>

<script src="${pageContext.request.contextPath }/resources/groupware/js/notice/noticeList.js"></script>