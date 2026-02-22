<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/groupware/css/notice/form.css" />

<form:form method="post" action="/sep/notice/${noticeNo}/edit" modelAttribute="notice" enctype="multipart/form-data">
    <div class="form-group row">
        <div class="col-md-8">
  			<form:errors path="noticeTitle" class="text-danger error-message" element="div"  />
            <input type="text" id="title" name="noticeTitle" class="form-control" value="${selectNotice.noticeTitle }" >
        </div>

        <div class="col-md-4 d-flex align-items-center">
            <select class="form-select" name="noticeCategory" id="smallSelect" data-init-value="${selectNotice.noticeCategory}">
                <option value>${selectNotice.noticeCategory }</option>
            </select>

            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                <button type="button" class="btn btn-black dropdown-toggle btn-sm"
                        id="isDraftListBtn" data-bs-toggle="dropdown" aria-haspopup="true"
                        aria-expanded="false">
                    임시저장글(${draftCnt})
                </button>
                <ul class="dropdown-menu" role="menu">
                    <li>
                        <c:forEach items="${draftList}" var="draf">
                            <a class="btn dropdown-item"
                               onclick="loadDraftContent('${draf.noticeNo}', '${fn:escapeXml(draf.noticeTitle)}', `${fn:escapeXml(draf.noticeContent)}`)">
                                ${draf.noticeTitle}
                            </a>
                        </c:forEach>
                    </li>
                </ul>
            </div>
        </div>
    </div>

    <div class="form-group">
		<form:errors path="noticeContent" class="text-danger error-message" element="div" />
        <textarea id="editor" name="noticeContent" class="form-control">${selectNotice.noticeContent }</textarea>
    </div>

    <!-- 파일 업로드 입력 필드와 파일 목록 -->
    <div class="form-group">
        <!-- 파일 업로드 -->
        <input type="file" id="fileUpload" name="uploadFiles" class="form-control mb-2" multiple>

        <!-- 업로드된 파일 목록 및 X 아이콘 표시 -->
        <div id="fileList">
            <c:forEach var="file" items="${selectNotice.file}">
                <c:if test="${not empty file.attachOrgFileName}">
                    <div id="file-${file.attachFileNo}" style="display: flex; align-items: center; margin-bottom: 6px;">
                        <!-- 파일명 + 크기 (MB로 변환) -->
                        <div style="min-width: 250px;">

                            ${file.attachOrgFileName}
                            <!-- 파일 크기를 MB로 변환하고 소수점 이하 두 자리까지 표시 -->
                            (<c:choose>
                                <c:when test="${file.attachFileSize == 0}">
                                    1MB
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatNumber value="${file.attachFileSize / 1024 / 1024}" type="number" maxFractionDigits="2"/>MB
                                </c:otherwise>
                            </c:choose>)
                        </div>
                        <!-- X 버튼 -->
                        <button type="button"
                                class="btn btn-link btn-danger"
                                style="margin-left: -50px;"
                                data-bs-toggle="tooltip"
                                title="Remove"
                                data-original-title="Remove"
                                onclick="removeFileFromList('${file.attachFileNo}')">
                            <i class="fa fa-times"></i>
                        </button>
                    </div>
                </c:if>
            </c:forEach>
        </div>
    </div>

    <div class="text-center mt-3">
        <button type="submit" class="btn btn-primary">저장</button>
        <button type="submit" class="btn btn-primary" id="isDraftButton">임시저장</button>
    </div>

    <input type="hidden" name="empId" value="${member.empId}" />
    <input type="hidden" name="isDraft" value="N" id="isDraftInput" />
	<input type="hidden" name="attachFileNo" id="attachFileNo" value="">
</form:form>

<script src="${pageContext.request.contextPath }/resources/groupware/js/notice/noticeEdit.js"></script>