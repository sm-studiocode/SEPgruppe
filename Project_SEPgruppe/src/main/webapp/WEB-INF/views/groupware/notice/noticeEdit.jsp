<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/groupware/css/notice/form.css" />

<div class="notice-form-wrap">
  <div class="notice-form-container">
    <div class="notice-card p-4">

      <!-- 헤더 -->
      <div class="notice-header">
        <div>
          <h3 class="notice-title">공지 수정</h3>
          <p class="notice-subtitle">제목, 카테고리, 내용을 수정하고 저장하세요.</p>
        </div>
      </div>

      <form:form method="post"
                 action="${pageContext.request.contextPath}/notice/${noticeNo}/edit"
                 modelAttribute="notice"
                 enctype="multipart/form-data">

        <!-- ✅ 상단: 제목(왼쪽) + (임시저장 버튼 위/카테고리 아래)(오른쪽) -->
        <div class="notice-top-grid">

          <!-- LEFT: 제목 -->
          <div>
            <form:errors path="noticeTitle" class="text-danger error-message" element="div" />
            <input type="text"
                   id="title"
                   name="noticeTitle"
                   class="form-control notice-input-title"
                   value="${fn:escapeXml(selectNotice.noticeTitle)}"
                   placeholder="제목을 입력해주세요">
          </div>

          <!-- RIGHT -->
          <div class="notice-top-right">

            <!-- ✅ 임시저장글 버튼(카테고리 위에 떠있게) -->
            <div class="dropdown">
              <button type="button"
                      class="btn btn-dark dropdown-toggle btn-sm notice-draft-btn"
                      id="isDraftListBtn"
                      data-bs-toggle="dropdown"
                      aria-haspopup="true"
                      aria-expanded="false">
                임시저장글(${draftCnt})
              </button>

              <ul class="dropdown-menu notice-draft-menu" role="menu" style="max-height:260px; overflow:auto;">
                <c:choose>
                  <c:when test="${empty draftList}">
                    <li class="dropdown-item text-muted">임시저장 글이 없습니다.</li>
                  </c:when>
                  <c:otherwise>
                    <c:forEach items="${draftList}" var="draf">
                      <li class="dropdown-item">
                        <a href="javascript:void(0);"
                           class="btn btn-link p-0 text-start w-100"
                           onclick="loadDraftContent(
                             '${draf.noticeNo}',
                             '${fn:escapeXml(draf.noticeTitle)}',
                             `${fn:escapeXml(draf.noticeContent)}`
                           )">
                          ${fn:escapeXml(draf.noticeTitle)}
                        </a>
                      </li>
                    </c:forEach>
                  </c:otherwise>
                </c:choose>
              </ul>
            </div>

            <!-- ✅ 카테고리(제목과 윗라인 맞춰짐) -->
            <select class="form-select notice-select"
                    name="noticeCategory"
                    id="smallSelect"
                    data-init-value="${fn:escapeXml(selectNotice.noticeCategory)}">
              <option value="${fn:escapeXml(selectNotice.noticeCategory)}">
                ${fn:escapeXml(selectNotice.noticeCategory)}
              </option>
            </select>

          </div>
        </div>

        <!-- 본문 -->
        <div class="mt-3">
          <form:errors path="noticeContent" class="text-danger error-message" element="div" />
          <textarea id="editor"
                    name="noticeContent"
                    class="form-control notice-editor-textarea">${fn:escapeXml(selectNotice.noticeContent)}</textarea>
        </div>

        <!-- 첨부파일 -->
        <div class="notice-file-card p-3 mt-3">
          <div class="notice-file-title">첨부파일</div>
          <div class="notice-file-sub mb-2">기존 파일은 X로 제거할 수 있고, 새 파일을 추가할 수 있어요.</div>

          <!-- 새 파일 업로드 -->
          <input type="file" id="fileUpload" name="uploadFiles" class="form-control" multiple>

          <!-- 기존 파일 목록 -->
          <div id="fileList" class="mt-3">
            <c:choose>
              <c:when test="${empty selectNotice.file}">
                <div class="text-muted" style="font-size:13px;">첨부파일이 없습니다.</div>
              </c:when>
              <c:otherwise>
                <c:forEach var="file" items="${selectNotice.file}">
                  <c:if test="${not empty file.attachOrgFileName}">
                    <div id="file-${file.attachFileNo}"
                         class="d-flex align-items-center justify-content-between"
                         style="padding:10px 12px; border:1px solid rgba(0,0,0,0.06); border-radius:10px; margin-bottom:8px;">

                      <div style="min-width: 0;">
                        <div style="font-weight:600; font-size:14px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">
                          ${fn:escapeXml(file.attachOrgFileName)}
                        </div>
                        <div class="text-muted" style="font-size:12px;">
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

                      <button type="button"
                              class="btn btn-sm btn-outline-danger"
                              onclick="removeFileFromList('${file.attachFileNo}')">
                        삭제
                      </button>
                    </div>
                  </c:if>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </div>
        </div>

        <!-- 버튼 -->
        <div class="notice-actions">
          <button type="submit" class="btn btn-primary">저장</button>
          <button type="button" class="btn btn-outline-primary" id="isDraftButton">임시저장</button>
        </div>

        <!-- hidden -->
        <input type="hidden" name="empId" value="${member.empId}" />
        <input type="hidden" name="isDraft" value="N" id="isDraftInput" />
        <input type="hidden" name="attachFileNo" id="attachFileNo" value="">

      </form:form>

    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/resources/groupware/js/notice/noticeEdit.js"></script>