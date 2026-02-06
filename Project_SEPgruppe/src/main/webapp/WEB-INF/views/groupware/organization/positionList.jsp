<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 4. 8.     	JSW            최초 생성
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/groupware/css/organization/positionList.css">
<title>직위 체계</title>

<h2>직위 목록</h2>
<p class="page-guide">핵심 분류체계로 여러 화면에서 멤버의 기준정보로 사용됩니다.</p>

<div class="position-wrapper">
    <div class="btn-group">
        <button id="addBtn"><i class="icon-plus"></i> 추가</button>
        <button id="deleteBtn"><i class="icon-close"></i> 삭제</button>
        <button id="sortBtn">↕ 순서 바꾸기</button>
    </div>

    <table class="position-table">
        <thead>
            <tr>
                <th><input type="checkbox" id="checkAll"/></th>
                <th>명칭</th>
                <th>코드</th>
                <th>사용멤버(명)</th>
            </tr>
        </thead>
        <tbody id="sortablePositionTable">
            <c:forEach var="pos" items="${positionList}">
                <tr data-positioncd="${pos.positionCd}">
                    <td>
                    	<span class="drag-handle"><i class="icon-menu"></i></span>
                    	<input type="checkbox" class="checkOne" value="${pos.positionCd}"/>
                    </td>
                    <td>${pos.positionName}</td>
                    <td>${pos.positionCd}</td>
                    <td>${pos.memberCount}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<div id="positionAddModal" class="custom-modal">
  <div class="custom-modal-content">
    <span class="close-modal">&times;</span>
    <h3>직위 추가</h3>
    <form id="positionAddForm">
      <div class="form-group">
        <label for="positionCd">직위코드</label>
        <input type="text" id="positionCd" name="positionCd" required>
      </div>
      <div class="form-group">
        <label for="positionName">명칭</label>
        <input type="text" id="positionName" name="positionName" required>
      </div>
      <div class="modal-buttons">
      	<div class="left-buttons">
      		<button type="button" class="btn btn-outline-secondary btn-sm" id="mecro">자동완성</button>
      	</div>
      	<div class="right-buttons">
	        <button type="submit" class="btn btn-outline-success btn-sm">등록</button>
	        <button type="button" id="cancelAdd" class="btn btn-outline-danger btn-sm">취소</button>
      	</div>
      </div>
    </form>
  </div>
</div>

<script>
    window.contextPath = "${pageContext.request.contextPath}";
</script>

<script src="${pageContext.request.contextPath}/resources/groupware/js/organization/positionList.js"></script>