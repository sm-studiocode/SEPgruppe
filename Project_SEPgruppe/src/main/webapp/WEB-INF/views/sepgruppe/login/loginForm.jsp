<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<link href="${pageContext.request.contextPath}/resources/sepgruppe/css/login/loginBackground.css" rel="stylesheet">

<section class="section-padding">
    <div class="container">
        <!-- row 중첩 제거 -->
        <div class="row justify-content-center">
            <div class="col-md-6 text-center">

                <h2 class="fw-bold">WELCOME TO SEPGroupee</h2>

                <!-- 탭 -->
                <div class="tabs mb-3">
                    <button type="button" class="tab active" data-target="loginFields">로그인</button>
                    <button type="button" class="tab" data-target="joinFields">회원가입</button>
                </div>

                <!-- ================= 로그인 ================= -->
                <form id="loginForm"
                      action="${pageContext.request.contextPath}/login/loginProcess"
                      method="post">

                    <div id="loginFields">
                        <div class="form-floating mb-3">
                            <input type="text" class="form-control"
                                   id="userId" name="userId" placeholder="ID">
                            <label for="userId">ID</label>
                        </div>

                        <div class="form-floating mb-3">
                            <input type="password" class="form-control"
                                   id="userPw" name="userPw" placeholder="Password">
                            <label for="userPw">Password</label>
                        </div>

                        <div class="d-grid gap-2">
                            <button class="btn btn-success" type="submit">로그인</button>
                        </div>

                        <div class="form-floating mt-3">
                            <select id="testAccountSelect" class="form-select">
                                <option value="">로그인 사용자 선택</option>
                                <option value="test001|java">사원(test001)</option>
                                <option value="company001|java">고객사 관리자(company001)</option>
                                <option value="provider001|java">서비스 제공자(provider001)</option>
                            </select>
                            <label for="testAccountSelect">테스트 계정</label>
                        </div>
                    </div>
                </form>

                <!-- ================= 회원가입 ================= -->
                <div id="joinFields" style="display:none;">
                    <form:form method="post"
                               action="${pageContext.request.contextPath}/login"
                               modelAttribute="company">

                        <div class="form-floating mb-3">
                            <form:input path="contactId" cssClass="form-control" placeholder="아이디"/>
                            <label>아이디</label>
                            <form:errors path="contactId" cssClass="text-danger"/>
                        </div>

                        <div class="form-floating mb-3">
                            <form:password path="contactPw" cssClass="form-control" placeholder="비밀번호"/>
                            <label>비밀번호</label>
                            <form:errors path="contactPw" cssClass="text-danger"/>
                        </div>

                        <div class="form-floating mb-3">
                            <input type="password" class="form-control"
                                   id="confirmPw" placeholder="비밀번호 확인">
                            <label for="confirmPw">비밀번호 확인</label>
                            <span id="passwordMatchError" class="text-danger"></span>
                        </div>

                        <div class="form-floating mb-3">
                            <form:input path="contactNm" cssClass="form-control" placeholder="이름"/>
                            <label>이름</label>
                            <form:errors path="contactNm" cssClass="text-danger"/>
                        </div>

                        <div class="form-floating mb-3">
                            <form:input path="contactPhone" cssClass="form-control" placeholder="연락처"/>
                            <label>연락처</label>
                            <form:errors path="contactPhone" cssClass="text-danger"/>
                        </div>

                        <div class="form-floating mb-3">
                            <form:input path="contactEmail" cssClass="form-control" placeholder="이메일"/>
                            <label>이메일</label>
                            <form:errors path="contactEmail" cssClass="text-danger"/>
                        </div>

                        <div class="form-floating mb-3">
                            <form:input path="companyName" cssClass="form-control" placeholder="고객사명"/>
                            <label>회사명</label>
                            <form:errors path="companyName" cssClass="text-danger"/>
                        </div>
                        
                        <div class="form-floating mb-3">
                            <form:input path="companyZip" cssClass="form-control" placeholder="우편번호"/>
                            <label>우편번호</label>
                            <form:errors path="companyZip" cssClass="text-danger"/>
                        </div>
                        
                        <div class="form-floating mb-3">
                            <form:input path="companyAdd1" cssClass="form-control" placeholder="회사주소"/>
                            <label>회사주소</label>
                            <form:errors path="companyAdd1" cssClass="text-danger"/>
                        </div>
                        
                        <div class="form-floating mb-3">
                            <form:input path="companyAdd2" cssClass="form-control" placeholder="상세주소"/>
                            <label>상세주소</label>
                            <form:errors path="companyAdd2" cssClass="text-danger"/>
                        </div>

                        <div class="form-floating mb-3">
                            <form:input path="businessRegNo" cssClass="form-control" placeholder="사업자등록번호"/>
                            <label>사업자등록번호</label>
                            <form:errors path="businessRegNo" cssClass="text-danger"/>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-success">회원가입</button>
                        </div>
                    </form:form>
                </div>

                <!-- 공통 링크 -->
                <div class="d-flex justify-content-between mt-3">
                    <a href="<c:url value='/login/findId'/>"
                       class="text-decoration-none text-white">아이디 찾기</a>
                    <a href="<c:url value='/login/findPw'/>"
                       class="text-decoration-none text-white">비밀번호 찾기</a>
                </div>

            </div>
        </div>
    </div>
</section>

<script src="${pageContext.request.contextPath}/resources/sepgruppe/js/login/loginForm.js"></script>
