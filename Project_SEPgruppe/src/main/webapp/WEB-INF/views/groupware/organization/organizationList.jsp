<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 3. 12.     	JYS            최초 생성
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link href="${pageContext.request.contextPath }/resources/groupware/css/organization/organizationList.css" rel="stylesheet" type="text/css">
<title>조직 관리</title>

<div class="org-wrapper">
    <!-- 왼쪽: 조직 트리 -->
    <div class="tree-panel">
        <div class="org-title">조직도</div>
        <p>부서 
            <button id="add-dept-btn">+</button> 
            <button id="delete-dept-btn">-</button>
        </p>
        <div class="search-box">
            <input type="text" id="employee-search" placeholder="사원 이름 검색...">
            <i class="fas fa-search search-icon" id="search-btn"></i>
        </div>
        <div id="depTree" class="tree-content"></div>
    </div>

    <!-- 오른쪽: 상세 정보 -->
    <div class="detail-panel">
        <a href="${pageContext.request.contextPath}/department/bulkInsertForm"
           class="btn btn-outline-secondary btn-sm bulk-insert-btn" id="bulkInsertBtn">
           <i class="icon-share-alt"></i> 부서 일괄등록
        </a>
        <div id="detailContent">
            <p>부서 또는 사원을 선택해주세요.</p>
        </div>
    </div>
</div>

<!-- 부서 추가 모달 -->
<div id="addDeptModal" class="custom-modal">
  <div class="custom-modal-content">
    <span class="close-modal">&times;</span>
    <h2>부서 추가</h2>
    <form id="addDeptForm">
       <div class="form-group">
            <label for="deptCd">부서코드:</label>
            <input type="text" id="deptCd" name="deptCd" required>
       </div>
      <div class="form-group">
        <label for="deptName">부서명:</label>
        <input type="text" id="deptName" name="deptName" required>
      </div>
      <div class="form-group">
        <label for="parentDeptCd">상위 부서 코드:</label>
        <input type="text" id="parentDeptCd" name="parentDeptCd">
      </div>
      <div class="form-group">
        <label for="managerEmpId">부서장:</label>
        <select id="managerEmpId" name="managerEmpId">
            <option value="">선택하세요</option>
        </select>
      </div>

      <div class="modal-buttons">
        <div class="left-buttons">
            <button type="button" class="btn btn-outline-secondary btn-sm" id="mecro">자동완성</button>
        </div>
        <div class="right-buttons">
            <button type="submit" class="btn btn-outline-success btn-sm">등록</button>
            <button type="button" id="cancelAddDept" class="btn btn-outline-danger btn-sm">취소</button>
        </div>
      </div>
    </form>
  </div>
</div>

<script>
    var contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/resources/groupware/js/organization/organizationList.js"></script>
