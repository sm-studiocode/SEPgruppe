<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %> 
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <meta content="" name="description">
    <meta content="" name="keywords">
    <meta name="_csrf" content="${_csrf.token}" />
	<meta name="_csrf_header" content="${_csrf.headerName}" />
    <tiles:insertAttribute name="preScript"/>

    <c:if test="${not empty message }">
        <script>
            alert("${message}");
        </script>
    </c:if>
</head>

<%-- ✅ URL에서 companyNo 제거: contextPath는 항상 "/sep" 등 고정 --%>
<body data-context-path="${pageContext.request.contextPath}">
    <div class="wrapper">
        <!-- Sidebar -->
        <tiles:insertAttribute name="sidebar" />

        <%-- 로그인 유저 --%>
        <security:authentication property="principal.realUser" var="chatRealUser"/>

        <%-- ✅ 권한 플래그 --%>
        <security:authorize access="hasAuthority('EMPLOYEE')" var="isEmployee" />
        <security:authorize access="hasAuthority('COMPANY')"  var="isCompany" />
        <security:authorize access="hasAuthority('PROVIDER')" var="isProvider" />

        <%-- ✅ EMPLOYEE일 때만 empId 출력 (COMPANY면 empId 접근 자체를 안 함) --%>
        <div class="main-panel"
             <c:if test="${isEmployee}">
                 data-emp-id="${chatRealUser.empId}"
             </c:if>
        >
            <!-- Header -->
            <div class="main-header">
                <tiles:insertAttribute name="header" />
            </div>

            <!-- Content -->
            <div class="container">
                <div class="page-inner">
                    <div class="d-flex align-items-left align-items-md-center flex-column flex-md-row pt-2 pb-4">
                        <div class="row">
                            <tiles:insertAttribute name="sidebar2" />
                            <div class="col-md-9">
                                <tiles:insertAttribute name="content"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Footer -->
            <tiles:insertAttribute name="footer"/>
        </div><!-- /.main-panel -->
    </div><!-- /.wrapper -->

    <tiles:insertAttribute name="postScript" />
</body>
</html>
