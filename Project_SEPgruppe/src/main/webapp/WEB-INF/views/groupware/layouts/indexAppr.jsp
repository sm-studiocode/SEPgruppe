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

    <tiles:insertAttribute name="preScript"/>

    <c:if test="${not empty message }">
        <script>
            alert("${message}");
        </script>
    </c:if>
</head>

<body data-context-path="${pageContext.request.contextPath }/${companyNo}">
    <div class="wrapper">
        <!-- Sidebar -->
        <tiles:insertAttribute name="sidebar" />

        <security:authentication property="principal.realUser" var="chatRealUser"/>
        <div class="main-panel" data-emp-id="${chatRealUser.empId}">
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
