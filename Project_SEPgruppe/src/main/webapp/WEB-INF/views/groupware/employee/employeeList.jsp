<!-- 
 * == 개정이력(Modification Information) ==
 *  2025. 3. 13.     	JYS            최초 생성
 *  2026. 2. 12.     	(수정)         사원 등록 시 비밀번호 입력 제거(임시비밀번호 자동 발급/메일 발송)
 *  2026. 2. 12.        (수정)         부서 선택: 조직도 FancyTree 팝업 연동 (부서선택 전용 JS 분리)
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<link href="${pageContext.request.contextPath }/resources/groupware/css/employee/employeeList.css" rel="stylesheet" />
<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/groupware/css/sidebar/sidepopup.css" />

<h4 class="title">사원통합관리</h4>

<!-- ✅ 전역 변수 금지: data로만 내림 -->
<div id="gwConfig"
     data-context-path="${pageContext.request.contextPath}"
     data-company-no="${companyNo}">
</div>

<div class="content_info" id="count_info">
	<ul class="info_summary">
		<li class="first">
			<span class="tit">현재 사원 수</span>
			<span class="txt"><strong>${pageData.totalRecord }</strong> 명</span>
			<span class="desc"> 정상 ${pageData.totalRecord }명 중지된 멤버 0명 </span>
		</li>
	</ul>
</div>

<div class="container-fluid">
	<div class="col-md-12">
		<div class="d-flex justify-content-start mb-3 gap-2 align-items-center">
			<button type="button" class="btn btn-secondary btn-sm mb-3"
				data-bs-toggle="modal" data-bs-target="#employeeModal">
				+ 사원 추가
			</button>
			
			<button class="btn btn-secondary btn-sm mb-3" id="bulkDelete" type="button">
				X 사원 삭제
			</button>
			
			<button class="btn btn-secondary btn-sm mb-3" type="button" id="mainDept">
			  <i class="bi icon-diagram-3"></i> 부서 변경
			</button>
			
			<button class="btn btn-secondary btn-sm mb-3" type="button" id="mainPosition">
				<i class="bi icon-user"></i> 직위 변경
			</button>

			<a href="#" class="btn btn-outline-success btn-sm mb-3" id="downExcel">
				<img src="<c:url value='/resources/groupware/images/excel.png'/>" id="ic-excel"> 목록 다운로드
			</a>
		</div>

		<div class="card">
			<div class="card-body">
				<div class="table-responsive">
					<table id="multi-filter-select" class="display table table-striped table-hover">
						<thead>
							<tr>
								<th><input type="checkbox" id="checkAll" /></th>
								<th>사번</th>
								<th>이름</th>
								<th>부서</th>
								<th>직위</th>
								<th>이메일</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
		</div>

	</div>
</div>

<!-- 부서 변경 모달 -->
<div class="modal fade" id="deptBulkModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered modal-lg justify-content-center">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">부서 변경</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>

      <div><h6 class="bulkTotal">선택한 사원 <span id="deptBulkCount">0</span>명 에 대해서,</h6></div>

      <div class="modal-body">
        <select id="deptBulkValue" class="form-select">
          <option value="">(미지정)</option>
        </select>
      </div>

      <div class="modal-footer">
        <button class="btn btn-primary" id="confirmDeptBulkUpdate" type="button">변경</button>
      </div>
    </div>
  </div>
</div>

<!-- 직위 변경 모달 -->
<div class="modal fade" id="bulkModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-lg justify-content-center">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">사원 정보 수정</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>

      <div><h6 class="bulkTotal">선택한 사원 <span id="bulkCount">0</span>명 에 대해서,</h6></div>

      <div class="modal-body">
        <select id="bulkValue" class="form-select">
          <c:forEach var="pos" items="${positionList}">
            <option value="${pos.positionCd}">${pos.positionName}</option>
          </c:forEach>
        </select>
      </div>

      <div class="modal-footer">
        <button class="btn btn-primary" id="confirmBulkUpdate" type="button">변경</button>
      </div>
    </div>
  </div>
</div>

<!-- =========================
     사원 등록 모달
========================= -->
<div class="modal fade" id="employeeModal" tabindex="-1"
	aria-labelledby="employeeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg modal-dialog-centered">
		<div class="modal-content p-4">
			<div class="modal-header border-0">
				<h5 class="modal-title" id="employeeModalLabel">사원 등록</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
			</div>

			<div class="modal-body">
				<div class="text-center mb-4">
					<label for="empImgInput">
						<img id="empImgPreview"
							 src="${pageContext.request.contextPath}/resources/images/default-profile.png"
							 alt="프로필 이미지"
							 class="rounded-circle" width="150" height="150"
							 style="cursor: pointer;" />
						<input type="file" id="empImgInput" name="attachFile" accept="image/*" hidden />
					</label>
					<div class="text-muted mt-2">※ 사진은 자동으로 150x150 사이즈로 적용됩니다.</div>
				</div>

				<form id="employeeForm" method="post"
					  action="${pageContext.request.contextPath}/employee/admin/new"
					  enctype="multipart/form-data">

					<div class="mb-3">
						<label class="form-label">* 이름(한글)</label>
						<input type="text" name="empNm" id="empNm" class="form-control" required />
					</div>

					<div class="mb-3">
						<label class="form-label">* 아이디</label>
						<input type="text" name="empId" id="empId" class="form-control" required />
					</div>

					<div class="mb-3">
						<label class="form-label">* 이메일</label>
						<input type="email" name="empEmail" id="empEmail" class="form-control" required />
					</div>

					<div class="alert alert-info py-2" role="alert">
						비밀번호는 자동으로 생성되어 등록된 이메일로 발송됩니다. (첫 로그인 후 변경 권장)
					</div>

					<div class="mb-3">
						<label class="form-label">직위</label>
						<select name="positionCd" class="form-select">
							<c:forEach var="pos" items="${positionList}">
								<option value="${pos.positionCd}">${pos.positionName}</option>
							</c:forEach>
						</select>
					</div>

					<div class="mb-3">
						<label class="form-label">부서</label>
						<div id="dept-select-wrap" class="d-flex align-items-center gap-2">
							<button type="button" class="btn btn-outline-secondary btn-sm" id="loadDeptBtn">
								+ 부서 선택
							</button>
							<span class="text-muted" id="deptSelectedLabel">부서 미선택</span>
						</div>
						<input type="hidden" name="deptCd" id="deptCd" />
					</div>

					<div class="modal-buttons">
						<div class="right-buttons">
							<button type="submit" class="btn btn-outline-success btn-sm">저장</button>
							<button type="button" class="btn btn-danger btn-sm" data-bs-dismiss="modal">취소</button>
						</div>
					</div>

				</form>
			</div>

		</div>
	</div>
</div>

<!-- =========================
     ✅ 부서 선택 팝업 DOM
========================= -->
<div id="deptOrgPopup" class="popup"
     data-context-path="${pageContext.request.contextPath}"
     style="display:none;">
	<div class="popup-content">
		<span class="close-btn" id="deptOrgCloseBtn">&times;</span>
		<h2>부서 선택</h2>

		<div class="search-box">
			<input type="text" id="dept-employee-search" placeholder="부서명 검색" />
			<button id="dept-search-btn" class="btn btn-primary btn-sm" type="button">검색</button>
		</div>

		<div class="popup-body">
			<div id="deptTree" class="tree"></div>
		</div>
	</div>
</div>


<!-- ✅ 목록/등록 등 기본 기능 -->
<script src="${pageContext.request.contextPath}/resources/groupware/js/employee/employeeList.js"></script>

<!-- ✅ 부서선택 전용 팝업/트리 -->
<script src="${pageContext.request.contextPath}/resources/groupware/js/employee/deptSelectPopup.js"></script>

