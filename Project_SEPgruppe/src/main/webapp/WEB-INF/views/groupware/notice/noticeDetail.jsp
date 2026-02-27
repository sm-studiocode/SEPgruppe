<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

<security:authentication property="principal.realUser" var="realUser"/>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/groupware/css/notice/noticeDetail.css" />
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">

<div class="noticeDetailContainer">
  <div class="page-header">
    <h3 class="fw-bold mb-3">공지사항</h3>
    <ul class="breadcrumbs mb-3">
      <li class="nav-home">
        <a href="<c:url value='/groupware'/>"><i class="icon-home"></i></a>
      </li>
      <li class="separator"><i class="icon-arrow-right"></i></li>
      <li class="nav-item">
        <a href="<c:url value='/notice'/>">공지사항</a>
      </li>
    </ul>
  </div>

  <!-- ✅ 작성폼/수정폼 톤과 맞추는 카드형 레이아웃 -->
  <div class="notice-detail-wrap">
    <div class="notice-detail-container">
      <div class="notice-detail-card">

        <!-- 상단: 카테고리 + 버튼 -->
        <div class="notice-detail-top">
          <div class="notice-badge">
            ${detailNotice.noticeCategory}
          </div>

          <div class="notice-detail-actions">
            <c:if test="${realUser.userId eq detailNotice.empId}">
              <c:url var="updateUrl" value="/notice/${noticeNo}/editForm"/>
              <button type="button" class="btn btn-sm btn-info" onclick="location.href='${updateUrl}'">
                Update
              </button>
            </c:if>
          </div>
        </div>

        <!-- 제목 -->
        <h2 class="notice-detail-title">
          ${detailNotice.noticeTitle}
        </h2>

        <!-- 메타 -->
        <div class="notice-detail-meta">
          <div class="meta-left">
            <span class="meta-writer">${detailNotice.empNm} ${detailNotice.positionName}</span>
          </div>
          <div class="meta-right">
            <span class="meta-date">${detailNotice.noticeCreatedAt}</span>
            <span class="meta-dot">•</span>
            <span class="meta-view">조회수 ${detailNotice.noticeViewCount}</span>
          </div>
        </div>

        <!-- 본문 -->
        <div class="notice-detail-content">
          ${detailNotice.noticeContent}
        </div>

        <!-- 첨부파일 -->
        <div class="notice-file-card mt-3">
          <div class="notice-file-head">
            <div class="notice-file-title">첨부파일</div>
            <div class="notice-file-sub">다운로드 아이콘을 눌러 받을 수 있어요.</div>
          </div>

          <div class="notice-file-list">
            <c:choose>
              <c:when test="${empty detailNotice.file}">
                <div class="notice-file-empty">첨부파일이 없습니다.</div>
              </c:when>
              <c:otherwise>
                <c:forEach var="file" items="${detailNotice.file}">
                  <c:if test="${not empty file.attachOrgFileName}">
                    <div class="notice-file-item">
                      <div class="file-info">
                        <div class="file-name" title="${file.attachOrgFileName}">
                          ${file.attachOrgFileName}
                        </div>
                        <div class="file-size">
                          (<c:choose>
                            <c:when test="${file.attachFileSize == 0}">
                              1MB
                            </c:when>
                            <c:otherwise>
                              <fmt:formatNumber value="${file.attachFileSize / 1024 / 1024}" type="number" maxFractionDigits="2"/>MB
                            </c:otherwise>
                          </c:choose>)
                        </div>
                      </div>

                      <a href="#"
                         class="file-download-btn"
                         data-file-no="${file.attachFileNo}"
                         onclick="downloadFile(event, this)"
                         title="다운로드">
                        <i class="bi bi-download"></i>
                      </a>
                    </div>
                  </c:if>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </div>
        </div>

      </div>
    </div>
  </div>
</div>

<!-- 모달 -->
<div class="modal fade" id="errorModal" tabindex="-1" aria-labelledby="errorModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header bg-danger text-white">
        <h5 class="modal-title" id="errorModalLabel">에러 발생</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body" id="errorModalBody"></div>
      <div class="modal-footer">
        <button type="button" class="btn btn-danger" data-bs-dismiss="modal">확인</button>
      </div>
    </div>
  </div>
</div>

<script>
  var companyNo = '${companyNo}';
  var noticeNo = '${noticeNo}';
  var contextPath = '${pageContext.request.contextPath}';
</script>

<script src="${pageContext.request.contextPath}/resources/groupware/js/notice/noticeDetail.js"></script>