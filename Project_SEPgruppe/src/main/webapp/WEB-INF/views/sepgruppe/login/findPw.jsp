<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<link href="${pageContext.request.contextPath}/resources/sepgruppe/css/login/loginBackground.css" rel="stylesheet">

<section class="section-padding">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6 text-center">

                <h2 class="fw-bold">비밀번호 찾기</h2>
                <p class="mb-4">회원 정보 확인 후 비밀번호를 재설정할 수 있습니다.</p>

                <!-- ===== 계정 확인 ===== -->
                <div id="verifySection">

                    <div class="form-floating mb-3">
                        <input type="text" class="form-control" id="contactId" placeholder="아이디">
                        <label>아이디</label>
                    </div>

                    <div class="form-floating mb-3">
                        <input type="text" class="form-control" id="contactNm" placeholder="이름">
                        <label>이름</label>
                    </div>

                    <div class="form-floating mb-3">
                        <input type="email" class="form-control" id="contactEmail" placeholder="이메일">
                        <label>이메일</label>
                    </div>

                    <div class="d-grid">
                        <button type="button" class="btn btn-success" id="checkAccountBtn">
                            계정 확인
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
<script>window.ctx='${pageContext.request.contextPath}';</script>
<script src="${pageContext.request.contextPath}/resources/sepgruppe/js/login/findPw.js"></script>
