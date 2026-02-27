<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

<!-- ✅ 로그인 아이디는 이걸로 가져와서 쓰자 (CompanyVO/EmployeeVO 타입 꼬여도 안전) -->
<security:authentication property="name" var="loginId" />

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/groupware/css/notice/form.css" />

<div class="notice-form-wrap">
  <div class="notice-form-container">
    <div class="notice-card p-4">

      <div class="notice-header">
        <div>
          <h3 class="notice-title">공지 작성</h3>
          <p class="notice-subtitle">제목, 카테고리, 내용을 입력하고 저장하세요.</p>
        </div>
      </div>

      <form:form method="post" modelAttribute="notice" enctype="multipart/form-data">

        <!-- ✅ 제목(왼쪽) + (오른쪽: 임시저장글 위, 카테고리 아래) -->
        <div class="notice-top-grid">

          <!-- LEFT: 제목 -->
          <div>
            <form:errors path="noticeTitle" class="text-danger error-message" element="div" />
            <input type="text"
                   id="title"
                   name="noticeTitle"
                   class="form-control notice-input-title"
                   value="${fn:escapeXml(notice.noticeTitle)}"
                   placeholder="제목을 입력해주세요">
          </div>

          <!-- RIGHT -->
          <div class="notice-top-right">

            <!-- ✅ 임시저장 목록 버튼: 카테고리 위로 이동 -->
            <div class="dropdown">
              <button type="button"
                      class="btn btn-dark dropdown-toggle btn-sm notice-draft-btn"
                      id="isDraftListBtn"
                      data-bs-toggle="dropdown"
                      aria-haspopup="true"
                      aria-expanded="false">
                임시저장글(${draftCnt})
              </button>

              <ul class="dropdown-menu notice-draft-menu" role="menu" style="max-height: 260px; overflow:auto;">
                <c:choose>
                  <c:when test="${empty draftList}">
                    <li class="dropdown-item text-muted">임시저장 글이 없습니다.</li>
                  </c:when>
                  <c:otherwise>
                    <c:forEach items="${draftList}" var="draf">
                      <li class="dropdown-item">
                        <div class="d-flex align-items-center justify-content-between">
                          <a href="javascript:void(0);"
                             class="btn btn-link p-0 text-start flex-grow-1"
                             data-draft-no="${draf.noticeNo}"
                             data-draft-title="${fn:escapeXml(draf.noticeTitle)}"
                             data-draft-content="${fn:escapeXml(draf.noticeContent)}"
                             onclick="loadDraftFromEl(this);">
                            ${fn:escapeXml(draf.noticeTitle)}
                          </a>

                          <button type="button"
                                  class="btn btn-sm btn-link p-0 ms-2"
                                  title="임시저장 삭제"
                                  onclick="deleteDraft && deleteDraft('${draf.noticeNo}')">
                            <i class="fas fa-eraser"></i>
                          </button>
                        </div>
                      </li>
                    </c:forEach>
                  </c:otherwise>
                </c:choose>
              </ul>
            </div>

            <!-- ✅ 카테고리 셀렉트: 임시저장글 아래로 -->
            <select class="form-select notice-select"
                    name="noticeCategory"
                    id="smallSelect"
                    data-init-value="${fn:escapeXml(notice.noticeCategory)}">

              <!-- ✅ 관리자면: 전사 + 부서 둘 다 -->
              <security:authorize access="hasAuthority('ROLE_ADMIN')">
                <option value="전사공지사항">전사공지사항</option>
                <option value="부서공지사항">부서공지사항</option>
              </security:authorize>

              <!-- ✅ 관리자가 아니면: 부서만 -->
              <security:authorize access="!hasAuthority('ROLE_ADMIN')">
                <option value="부서공지사항">부서공지사항</option>
              </security:authorize>

            </select>

          </div>
        </div>

        <!-- 본문 -->
        <div class="mt-3">
          <form:errors path="noticeContent" class="text-danger error-message" element="div" />
          <textarea id="editor"
                    name="noticeContent"
                    class="form-control notice-editor-textarea">${fn:escapeXml(notice.noticeContent)}</textarea>
        </div>

        <!-- 첨부파일 -->
        <div class="notice-file-card p-3 mt-3">
          <div class="notice-file-title">첨부파일</div>
          <div class="notice-file-sub mb-2">여러 파일을 선택할 수 있어요.</div>

          <input type="file" id="fileUpload" name="uploadFiles" class="form-control" multiple>
          <input type="text" id="fileData" class="form-control notice-file-names mt-2" readonly
                 placeholder="선택된 파일 없음">
        </div>

        <!-- 버튼 -->
        <div class="notice-actions">
          <button type="submit" class="btn btn-primary" id="saveBtn">저장</button>
          <button type="button" class="btn btn-outline-primary" id="isDraftButton">임시저장</button>
        </div>

        <!-- ✅ empId는 항상 로그인 아이디로 -->
        <input type="hidden" name="empId" value="${loginId}" />
        <input type="hidden" name="isDraft" value="N" id="isDraftInput" />

        <!-- ✅ CSRF 토큰 (폼 submit 403 방지 핵심) -->
        <security:csrfInput/>

      </form:form>

    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/resources/groupware/js/notice/noticeForm.js"></script>