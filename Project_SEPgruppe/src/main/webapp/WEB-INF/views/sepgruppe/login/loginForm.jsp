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

                <c:set var="activeTab" value="${empty activeTab ? 'login' : activeTab}" />

                <!-- 탭 -->
                <div class="tabs mb-3">
                    <button type="button"
                            class="tab ${activeTab eq 'login' ? 'active' : ''}"
                            data-target="loginFields">로그인</button>
                    <button type="button"
                            class="tab ${activeTab eq 'join' ? 'active' : ''}"
                            data-target="joinFields">회원가입</button>
                </div>

                <!-- ================= 로그인 ================= -->
                <form id="loginForm"
                      action="${pageContext.request.contextPath}/login/loginProcess"
                      method="post">

                    <div id="loginFields" style="${activeTab eq 'login' ? '' : 'display:none;'}">

                        <div class="form-floating mb-3">
                            <input type="text"
                                   class="form-control"
                                   id="userId"
                                   name="userId"
                                   placeholder="ID">
                            <label for="userId">ID</label>
                        </div>

                        <div class="form-floating mb-3">
                            <input type="password"
                                   class="form-control"
                                   id="userPw"
                                   name="userPw"
                                   placeholder="Password">
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
                <div id="joinFields" style="${activeTab eq 'join' ? '' : 'display:none;'}">
                    <form:form method="post"
                               action="${pageContext.request.contextPath}/login"
                               modelAttribute="company">

                        <div class="form-floating mb-3">
                            <form:input path="contactId"
                                        cssClass="form-control"
                                        placeholder="아이디"/>
                            <label>아이디</label>
                            <form:errors path="contactId" cssClass="error-msg"/>
                        </div>

                        <div class="form-floating mb-3">
                            <form:password path="contactPw"
                                           cssClass="form-control"
                                           placeholder="비밀번호"/>
                            <label>비밀번호</label>
                            <form:errors path="contactPw" cssClass="error-msg"/>
                        </div>

                        <div class="form-floating mb-3">
                            <input type="password"
                                   name="confirmPw"
                                   class="form-control"
                                   id="confirmPw"
                                   placeholder="비밀번호 확인">
                            <label for="confirmPw">비밀번호 확인</label>
                            <form:errors path="confirmPw" cssClass="error-msg"/>
                        </div>

                        <div class="form-floating mb-3">
                            <form:input path="contactNm"
                                        cssClass="form-control"
                                        placeholder="이름"/>
                            <label>이름</label>
                            <form:errors path="contactNm" cssClass="error-msg"/>
                        </div>

                        <div class="form-floating mb-3 d-flex gap-2">
                            <label>연락처</label>
                            <input type="text" class="form-control phone" maxlength="3" placeholder="010">
                            <span>-</span>
                            <input type="text" class="form-control phone" maxlength="4">
                            <span>-</span>
                            <input type="text" class="form-control phone" maxlength="4">
                        </div>
                        
                        <input type="hidden" name="contactPhone" id="contactPhone">
                        <form:errors path="contactPhone" cssClass="error-msg"/>

						<br>
						
                        <div class="form-floating mb-3">
                            <form:input path="contactEmail"
                                        cssClass="form-control"
                                        placeholder="이메일"/>
                            <label>이메일</label>
                            <form:errors path="contactEmail" cssClass="error-msg"/>
                        </div>

                        <div class="form-floating mb-3">
                            <form:input path="companyName"
                                        cssClass="form-control"
                                        placeholder="고객사명"/>
                            <label>회사명</label>
                            <form:errors path="companyName" cssClass="error-msg"/>
                        </div>

                        <form:hidden path="companyZip" id="companyZip"/>
                        <form:errors path="companyZip" cssClass="error-msg"/>

                        <div class="form-floating mb-1">
                            <form:input path="companyAdd1"
                                        cssClass="form-control"
                                        id="companyAdd1"
                                        placeholder="회사주소"
                                        readonly="true"/>
                            <label for="companyAdd1">회사주소</label>
                        </div>

                        <div class="form-floating mb-3">
                            <form:input path="companyAdd2"
                                        cssClass="form-control"
                                        placeholder="상세주소"/>
                            <label>상세주소</label>
                        </div>

                        <div class="d-grid mb-1">
                            <button type="button"
                                    class="btn btn-outline-light btn-sm"
                                    onclick="execDaumPostcode()">
                                주소 검색
                            </button>
                        </div>

                        <form:errors path="companyAdd2" cssClass="error-msg"/>
                        <form:errors path="companyAdd1" cssClass="error-msg"/>

                        <br>

                        <div class="form-floating mb-3 d-flex gap-2">
                            <label>사업자등록번호</label>
                            <input type="text" class="form-control bizno" maxlength="3">
                            <span>-</span>
                            <input type="text" class="form-control bizno" maxlength="2">
                            <span>-</span>
                            <input type="text" class="form-control bizno" maxlength="5">
                        </div>

                        <input type="hidden" name="businessRegNo" id="businessRegNo">
                        <form:errors path="businessRegNo" cssClass="error-msg"/>

                        <br>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-success">회원가입</button>
                        </div>

                    </form:form>
                </div>

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
<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
