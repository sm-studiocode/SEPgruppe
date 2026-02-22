<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<security:authentication property="principal.realUser" var="realUser"/> <!-- Provider 시큐리티 정보 -->
<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/groupware/css/notice/form.css" />
<%-- 리얼유저 ${advice } --%>

<form:form method="post" modelAttribute="notice" enctype="multipart/form-data">
    <div class="form-group row">
        <div class="col-md-8">
			<form:errors path="noticeTitle" class="text-danger error-message" element="div"  />
            <input type="text" id="title" name="noticeTitle" class="form-control" value="${notice.noticeTitle }" placeholder="제목을 입력해주세요">
        </div>

        <div class="col-md-4 d-flex align-items-center">
		    <select class="form-select" name="noticeCategory" id="smallSelect" data-init-value="${notice.noticeCategory}">
		        <c:choose>
		            <c:when test="${fn:startsWith(realUser.empId, 'admin')}">
		                <option value="전사공지사항">전사공지사항</option>
		            </c:when>

		            <c:when test="${not empty advice.managerEmpId}">
		                <option value="부서공지사항">부서공지사항</option>
		            </c:when>
		        </c:choose>
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
							<div class="d-flex align-items-center">
								<button class="btn btn-sm btn-link p-0 me-2">
									<i class="fas fa-eraser"></i>
								</button>
	                            <a class="btn dropdown-item"
	                               onclick="loadDraftContent('${draf.noticeNo}', '${fn:escapeXml(draf.noticeTitle)}', `${fn:escapeXml(draf.noticeContent)}`)">
	                                ${draf.noticeTitle}
	                            </a>
							</div>
                        </c:forEach>
                    </li>
                </ul>
            </div>
        </div>
    </div>

    <div class="form-group">
        <form:errors path="noticeContent" class="text-danger error-message" element="div"  />
        <textarea id="editor" name="noticeContent" class="form-control" >${notice.noticeContent }</textarea>
    </div>

    <div class="form-group">
        <input type="file" id="fileUpload" name="uploadFiles" class="fileInput" multiple>
        <input type="hidden" id="fileData" name="fileData" readonly>
    </div>

    <div class="text-center mt-3">
        <button type="submit" class="btn btn-primary">저장</button>
        <button type="submit" class="btn btn-primary" id="isDraftButton">임시저장</button>
    </div>

    <input type="hidden" name="empId" value="${member.empId}" />
    <input type="hidden" name="isDraft" value="N" id="isDraftInput" />
</form:form>

<script src="${pageContext.request.contextPath }/resources/groupware/js/notice/noticeForm.js"></script>
