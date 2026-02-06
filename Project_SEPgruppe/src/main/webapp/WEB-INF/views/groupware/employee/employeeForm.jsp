<!-- <!--  -->
<!--  * == 개정이력(Modification Information) == -->
<!--  *    -->
<!--  *   수정일      			수정자           수정내용 -->
<!--  *  ============   	============== ======================= -->
<!--  *  2025. 3. 13.     	JYS            최초 생성 -->
<!--  * -->
<!-- --> -->
<%-- <%@ page language="java" contentType="text/html; charset=UTF-8" --%>
<%--     pageEncoding="UTF-8"%> --%>
<%-- <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%> --%>
<!-- <link -->
<%--    href="${pageContext.request.contextPath}/resources/sepgruppe/TopicListing-1.0.0/css/bootstrap.min.css" --%>
<!--    rel="stylesheet"> -->
<!-- <link -->
<%--    href="${pageContext.request.contextPath}/resources/sepgruppe/css/clientForm.css" --%>
<!--    rel="stylesheet"> -->
<!-- <h4>관리자 - 인사 정보 등록 폼</h4> -->
<!-- <!-- 회원가입 Form (tiles 타지 않음) --> -->
<%-- <form:form method="post" modelAttribute="member" --%>
<%--    action="${pageContext.request.contextPath}/${companyNo}/dclz/new/employee"> --%>
<!--    <div id="joinForm"> -->
<!--       <div class="sepJoinimg"> -->
<!--          <img -->
<%--             src="${pageContext.request.contextPath}/resources/sepgruppe/images/LOGO.png" --%>
<!--             class="img-thumbnail" id="sepJoin"> -->
<!--       </div> -->

<!-- 		<div class="form-group col-6 mx-auto"> -->
<!-- 			<label for="empId" class="form-label">사원 아이디  -->
<!-- 				<span class="required">*</span> -->
<!-- 			</label> -->
<!-- 			<div class="form-floating"> -->
<!-- 				<input type="text" class="form-control" name="empId" -->
<%-- 					id="empId" value="${employee.empId}" required> <span --%>
<!-- 					id="contactIdError" class="text-danger"></span> -->
<%-- 				<form:errors path="contactId" class="text-danger" element="span" /> --%>
<!-- 			</div> -->
<!-- 		</div> -->
		
<!-- 		<div class="form-group col-6 mx-auto"> -->
<!-- 			<label for="companyNo" class="form-label">고객사 번호  -->
<!-- 				<span class="required">*</span> -->
<!-- 			</label> -->
<!-- 			<div class="form-floating"> -->
<!-- 				<input type="text" class="form-control" name="companyNo" -->
<%-- 					id="companyNo" value="${companyNo}" required readonly> <span --%>
<!-- 					id="contactIdError" class="text-danger"></span> -->
<%-- 				<form:errors path="contactId" class="text-danger" element="span" /> --%>
<!-- 			</div> -->
<!-- 		</div> -->

<!-- 		<div class="form-group col-6 mx-auto"> -->
<!-- 			<label for="contactPw" class="form-label">패스워드 <span -->
<!-- 				class="required">*</span></label> -->
<!-- 			<div class="form-floating"> -->
<!-- 				<input type="password" class="form-control" name="contactPw" -->
<!-- 					id="contactPw" required> -->
<%-- 				<form:errors path="contactPw" class="text-danger" element="span" /> --%>
<!-- 			</div> -->
<!-- 		</div> -->

<!-- 		<div class="form-group col-6 mx-auto"> -->
<!-- 			<label for="confirmPw" class="form-label">패스워드 확인 <span -->
<!-- 				class="required">*</span></label> -->
<!-- 			<div class="form-floating"> -->
<!-- 				<input type="password" class="form-control" name="confirmPw" -->
<!-- 					id="confirmPw" required> <span id="passwordMatchError" -->
<!-- 					class="text-danger"></span> -->
<%-- 				<form:errors path="confirmPw" class="text-danger" element="span" /> --%>
<!-- 			</div> -->
<!-- 		</div> -->

<!-- 		<div class="form-group col-6 mx-auto"> -->
<!-- 			<label for="contactNm" class="form-label">이름 <span -->
<!-- 				class="required">*</span></label> -->
<!-- 			<div class="form-floating"> -->
<!-- 				<input type="text" class="form-control" name="contactNm" -->
<%-- 					id="contactNm" value="${companies.contactNm}" required> --%>
<%-- 				<form:errors path="contactNm" class="text-danger" element="span" /> --%>
<!-- 			</div> -->
<!-- 		</div> -->
<!-- 		<div class="form-group col-6 mx-auto"> -->
<!-- 			<label for="contactPhone" class="form-label">연락처 <span -->
<!-- 				class="required">*</span></label> -->
<!-- 			<div class="form-floating"> -->
<!-- 				<input type="text" class="form-control" name="contactPhone" -->
<%-- 					id="contactPhone" value="${companies.contactPhone}" required> --%>
<%-- 				<form:errors path="contactPhone" class="text-danger" element="span" /> --%>
<!-- 			</div> -->
<!-- 		</div> -->
<!-- 		<div class="form-group col-6 mx-auto"> -->
<!-- 			<label for="contactEmail" class="form-label">이메일 <span -->
<!-- 				class="required">*</span></label> -->
<!-- 			<div class="form-floating"> -->
<!-- 				<input type="text" class="form-control" name="contactEmail" -->
<%-- 					id="contactEmail" value="${companies.contactEmail}" required> --%>
<%-- 				<form:errors path="contactEmail" class="text-danger" element="span" /> --%>
<!-- 			</div> -->
<!-- 		</div> -->

<!-- 		<div class="form-group col-6 mx-auto"> -->
<!-- 			<label for="companyName" class="form-label">고객사명</label> -->
<!-- 			<div class="form-floating"> -->
<!-- 				<input type="text" class="form-control" name="companyName" -->
<%-- 					id="companyName" value="${companies.companyName}"> --%>
<%-- 				<form:errors path="companyName" class="text-danger" element="span" /> --%>
<!-- 			</div> -->
<!-- 		</div> -->

<!-- 		<div class="form-group col-6 mx-auto mb-3"> -->
<!-- 			<label for="businessRegNo" class="form-label">사업자 등록번호</label> -->
<!-- 			<div class="form-floating"> -->
<!-- 				<input type="number" class="form-control" name="businessRegNo" -->
<%-- 					id="businessRegNo" value="${companies.businessRegNo}"> <span --%>
<!-- 					id="businessRegNoError" class="text-danger"></span> -->
<%-- 				<form:errors path="businessRegNo" class="text-danger" element="span" /> --%>
<!-- 			</div> -->
<!-- 		</div> -->

<!-- 		<div class="form-floating col-6 mx-auto"> -->
<!--          <button type="submit" class="btn btn-success" id="submitBtn" disabled>회원가입</button> -->
<!--       </div> -->
<!--    </div> -->
<%-- </form:form> --%>

