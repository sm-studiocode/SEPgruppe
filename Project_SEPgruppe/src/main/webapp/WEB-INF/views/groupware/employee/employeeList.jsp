<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 3. 13.     	JYS            최초 생성
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link
	href="${pageContext.request.contextPath }/resources/groupware/css/employee/employeeList.css"
	rel="stylesheet" />
<h4 class="title">사원통합관리</h4>

<div class="content_info" id="count_info">
	<ul class="info_summary">
		<li class="first"><span class="tit">현재 사원 수</span> <span
			class="txt"><strong>${pageData.totalRecord }</strong> 명</span> <span
			class="desc"> 정상 ${pageData.totalRecord }명 중지된 멤버 0명 </span></li>
		<li style="display: none;"><span class="tit">추가 가능한 멤버 수</span> <span
			class="txt"><strong>19</strong> 명</span></li>
		<span class="wrap_action" style="display: block;"> <span
			class="ic_adm ic_accordion_open" title="펼치기"></span>
		</span>
		<span class="wrap_action" style="display: none;"> <span
			class="ic_adm ic_accordion_close" title="접기"></span>
		</span>
	</ul>
</div>
<div class="container-fluid">
	<div class="col-md-12">
		<div class="d-flex justify-content-start mb-3 gap-2 align-items-center">
			<button type="button" class="btn btn-secondary btn-sm mb-3"
				data-bs-toggle="modal" data-bs-target="#employeeModal">
				+ 사원 추가</button>
				<!-- 메인 버튼 -->
				<button class="btn btn-secondary btn-sm mb-3" type="button" id="mainPosition">
					<i class="bi icon-user"></i> 직위 변경
				</button>
				<button class="btn btn-secondary btn-sm mb-3" id="bulkDelete">
				  X 사원 삭제
				</button>

			<a href="#" class="btn btn-outline-success btn-sm mb-3"
				id="downExcel" ><img src="<c:url value='/resources/groupware/images/excel.png'/>" id="ic-excel"> 목록 다운로드 </a>
		</div>
		<div class="card">
			<div class="card-body">
				<div class="table-responsive">
					<table id="multi-filter-select"
						class="display table table-striped table-hover">
						<thead>
							<tr>
								<th><input type="checkbox" id="checkAll" value="${employee.empId }" /></th>
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

<!-- 사원 등록 모달 -->
<div class="modal fade" id="employeeModal" tabindex="-1"
	aria-labelledby="employeeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg modal-dialog-centered">
		<div class="modal-content p-4">
			<div class="modal-header border-0">
				<h5 class="modal-title" id="employeeModalLabel">사원 등록</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="닫기"></button>
			</div>
			<div class="modal-body">
				<!-- 프로필 이미지 업로드 -->
				<div class="text-center mb-4">
					<label for="empImgInput"> <img id="empImgPreview"
						src="/resources/images/default-profile.png" alt="프로필 이미지"
						class="rounded-circle" width="150" height="150"
						style="cursor: pointer;" /> <input type="file" id="empImgInput"
						name="attachFile" accept="image/*" hidden />
					</label>
					<div class="text-muted mt-2">※ 사진은 자동으로 150x150 사이즈로 적용됩니다.</div>
				</div>

				<!-- 사원 등록 폼 -->
				<form id="employeeForm" method="post" enctype="multipart/form-data">
					<div class="mb-3">
						<label class="form-label">* 이름(한글)</label> <input type="text"
							name="empNm" id="empNm" class="form-control" required />
					</div>
					<div class="mb-3">
						<label class="form-label">* 아이디</label> <input type="text"
							name="empId" id="empId" class="form-control" required />
					</div>
					<div class="mb-3">
						<label class="form-label">* 비밀번호</label> <input type="password"
							name="empPw" id="empPw" class="form-control" required />
					</div>
					<div class="mb-3">
						<label class="form-label">* 비밀번호 확인</label> <input type="password"
							name="empPwConfirm" id="empPwConfirm" class="form-control" required />
					</div>
					<div class="mb-3">
						<label class="form-label">* 이메일</label> <input type="email"
							name="empEmail" id="empEmail" class="form-control" required />
					</div>
					<div class="mb-3">
						<label class="form-label">직위</label> <select name="positionCd"
							class="form-select">
							<c:forEach var="pos" items="${positionList}">
								<option value="${pos.positionCd}">${pos.positionName}</option>
							</c:forEach>
						</select>
					</div>
					<div class="mb-3">
						<label class="form-label">부서-직책</label>
						<div id="dept-select-wrap">
							<button type="button" class="btn btn-outline-secondary btn-sm"
								id="loadDeptBtn">+ 부서 추가</button>
						</div>
					</div>

					<!-- 하단 버튼 -->
					<div class="modal-buttons">
						<div class="left-buttons">
				      		<button type="button" class="btn btn-outline-secondary btn-sm" id="mecro">자동완성</button>
				      	</div>
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
        <button class="btn btn-primary" id="confirmBulkUpdate">변경</button>
      </div>
    </div>
  </div>
</div>
<script>
	const companyNo = "${companyNo}";
	const contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath }/resources/groupware/js/employee/employeeList.js"></script>
