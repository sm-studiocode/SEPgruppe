<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/sepgruppe/css/indexSep.css" />

<section class="section-padding" id="section_main">
    <div class="container">
        <div class="row">
            <div class="col-lg-10 col-12 mx-auto">
                <div class="d-flex justify-content-center">
                    <div class="noto-sans-kr-sep">
                        <h2>SEP 과 함께 그룹웨어를 보다</h2>
                        <h2>편리하게 이용해 보세요.</h2>

                        <div class="group-button-wrapper">

                            <!-- 로그인한 사용자 -->
                            <security:authorize access="isAuthenticated()">
                                <security:authentication property="principal" var="principal"/>

                                <c:choose>
                                    <%-- COMPANY 사용자 --%>
                                    <c:when test="${principal.realUser.target eq 'COMPANY'}">
                                        <button type="button"
                                                class="btn btn-outline-secondary btn-lg"
                                                id="group-buttons"
                                                onclick="location.href='${pageContext.request.contextPath}/${principal.realUser.companyNo}/groupware'">
                                            <i class="bi bi-exclude"></i> 그룹웨어 이용하기
                                        </button>
                                    </c:when>

                                    <%-- PROVIDER (관리자) --%>
                                    <c:when test="${principal.realUser.target eq 'PROVIDER'}">
                                        <button type="button"
                                                class="btn btn-outline-secondary btn-lg"
                                                id="group-buttons"
                                                onclick="location.href='${pageContext.request.contextPath}/sep/provider'">
                                            <i class="bi bi-shield-lock"></i> 관리자 페이지 이동
                                        </button>
                                    </c:when>
                                </c:choose>
                            </security:authorize>

                            <!-- 로그인 안 한 사용자 -->
                            <security:authorize access="isAnonymous()">
                                <button type="button"
                                        class="btn btn-outline-secondary btn-lg"
                                        id="group-buttons"
                                        onclick="location.href='${pageContext.request.contextPath}/login'">
                                    <i class="bi bi-exclude"></i> 그룹웨어 이용하기
                                </button>
                            </security:authorize>

                        </div>

                        <br><br><br>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<section class="timeline-section section-padding" id="section_3">
    <div class="section-overlay"></div>
    <div class="container">
        <div class="row">
            <div class="col-12 text-center">
                <h2 class="text-white mb-4">How Do Apply?</h2>
            </div>

            <div class="col-lg-10 col-12 mx-auto">
                <div class="timeline-container">
                    <ul class="vertical-scrollable-timeline" id="vertical-scrollable-timeline">
                        <div class="list-progress">
                            <div class="inner"></div>
                        </div>

                        <li>
                            <h4 class="text-white mb-3">무료 데모 체험을 한다.</h4>
                            <p class="text-white">
                                서비스 사용 전 무료 데모 체험을 통해 실제 기능을 경험해보세요.
                            </p>
                            <div class="icon-holder">
                                <i class="bi-search"></i>
                            </div>
                        </li>

                        <li>
                            <h4 class="text-white mb-3">원하는 옵션의 구독신청을 한다.</h4>
                            <p class="text-white">
                                체험 후 본인에게 맞는 요금제를 선택해 구독 신청을 진행합니다.
                            </p>
                            <div class="icon-holder">
                                <i class="bi-bookmark"></i>
                            </div>
                        </li>

                        <li>
                            <h4 class="text-white mb-3">부여받은 계정으로 그룹웨어 접속</h4>
                            <p class="text-white">
                                계정 발급 후 그룹웨어에 접속해 협업 기능을 이용합니다.
                            </p>
                            <div class="icon-holder">
                                <i class="bi-book"></i>
                            </div>
                        </li>

                    </ul>
                </div>
            </div>
        </div>
    </div>
</section>

<script src="https://cdn.onesignal.com/sdks/web/v16/OneSignalSDK.page.js" defer></script>

<!-- 구독 관련 Modal -->
<div class="modal fade" id="exampleModal" tabindex="-1"
     aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">

            <div class="modal-header">
                <h1 class="modal-title fs-5" id="exampleModalLabel">제품 정보</h1>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
                상품 설명 및 금액 정보 표시
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary"
                        data-bs-dismiss="modal">Close</button>
                <button type="button"
                        class="btn btn-primary"
                        onclick="location.href='/sep/subscriptionPlan'">
                    결제하기
                </button>
            </div>

        </div>
    </div>
</div>
