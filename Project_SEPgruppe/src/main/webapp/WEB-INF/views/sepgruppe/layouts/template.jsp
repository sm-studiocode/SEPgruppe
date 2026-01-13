<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="author" content="">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>SEP</title>

    <tiles:insertAttribute name="preScript" />
</head>

<!-- ✅ body는 여기 “한 번만” -->
<body data-ctx="${pageContext.request.contextPath}">

    <tiles:insertAttribute name="header" />

    <c:if test="${not empty message}">
        <script type="text/javascript">
            alert("${message}")
        </script>
    </c:if>

    <c:if test="${not empty error}">
        <script type="text/javascript">
            alert("${error}")
        </script>
    </c:if>

    <main>
        <tiles:insertAttribute name="content" />
    </main>

    <footer id="footer" class="footer">
        <tiles:insertAttribute name="footer" />
    </footer>

    <a href="#" class="back-to-top d-flex align-items-center justify-content-center">
        <i class="bi bi-arrow-up-short"></i>
    </a>

    <tiles:insertAttribute name="postScript" />
</body>
</html>
