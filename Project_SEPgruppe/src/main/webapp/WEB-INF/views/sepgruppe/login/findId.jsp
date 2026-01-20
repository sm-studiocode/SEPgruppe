<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<link href="${pageContext.request.contextPath}/resources/sepgruppe/css/login/loginBackground.css" rel="stylesheet">
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<section class="section-padding">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6 text-center">

                <h2 class="fw-bold">아이디 찾기</h2>
                <p class="mb-4">회원가입 시 입력한 정보를 입력해주세요</p>

                <form id="findIdForm" onsubmit="return false;">
                    <div class="form-floating mb-3">
                        <input type="text"
                               class="form-control"
                               id="contactNm"
                               placeholder="이름">
                        <label for="contactNm">이름</label>
                    </div>

                    <div class="form-floating mb-3">
                        <input type="email"
                               class="form-control"
                               id="contactEmail"
                               placeholder="이메일">
                        <label for="contactEmail">이메일</label>
                    </div>

                    <div class="d-grid gap-2 mb-3">
                        <button type="button"
                                class="btn btn-success"
                                id="findIdBtn">
                            아이디 찾기
                        </button>
                    </div>
                </form>

                <div id="resultMessage" class="mt-3"></div>

                <div class="d-flex justify-content-between mt-4">
                    <a href="<c:url value='/login'/>"
                       class="text-decoration-none text-white">로그인</a>
                    <a href="<c:url value='/login/findPw'/>"
                       class="text-decoration-none text-white">비밀번호 찾기</a>
                </div>

            </div>
        </div>
    </div>
</section>
<script>window.ctx='${pageContext.request.contextPath}';</script>

<script src="${pageContext.request.contextPath}/resources/sepgruppe/js/login/findId.js"></script>
