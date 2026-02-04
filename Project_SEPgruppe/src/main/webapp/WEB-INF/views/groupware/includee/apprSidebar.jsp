
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/groupware/css/approval/apprHome.css" >
<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/groupware/css/approval/apprSidebar.css" >

<!-- Sidebar Menu -->
<div class="col-md-3" id="apprSidebar">
    <div class="apprSidebar-wrapper">
        <div class="apprSidebar-content">
            <ul class="nav nav-secondary">
                 <li class="nav-section">
                   <h4 class="text-section">📑&nbsp;전자결재</h4>
                </li>

                <!-- 새 문서 작성 -->
                <li class="nav-item apprBtn-li">
                    <div>
                        <button
                            id="newApprDocBtn"
                            type="button"
                            class="btn btn-primary"
                            onclick="location.href='<c:url value="/approval/new"/>'"
                            data-company-no="#">
                            새 문서 작성
                        </button>
                    </div>
                </li>

               <!-- 결재하기 -->
                <li class="nav-item appr-item">
                    <a data-bs-toggle="collapse" href="#apprDoc" class="d-flex"> 
                        <span class="apprMenu-text"><i class="fas fa-file-signature"></i>결재하기</span><span class="caret"></span>
                    </a>
                    <div class="collapse show" id="apprDoc">
                        <ul class="nav nav-collapse">
                            <li><a href="<c:url value='#'/>">
                                <p class="sub-item">결재 대기 문서</p>
                            </a></li>
                            <li><a href="<c:url value='#'/>">
                                <p class="sub-item">결재 예정 문서</p>
                            </a></li>
                        </ul>
                    </div>
                </li>

                <!-- 개인 문서함 -->
                <li class="nav-item appr-i
