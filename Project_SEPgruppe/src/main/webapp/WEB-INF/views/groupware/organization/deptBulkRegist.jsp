<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 4. 8.     	JSW            최초 생성
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link href="${pageContext.request.contextPath }/resources/groupware/css/organization/deptBulkRegist.css" rel="stylesheet" type="text/css">
 <title>부서 일괄 등록</title>
 
 <h2>부서 일괄 등록</h2>

    <div class="upload-box">
        <p><strong>샘플 양식 다운로드 후, 부서를 작성하여 업로드하세요.</strong></p>

        <!-- 샘플 양식 다운로드 -->
        <button class="btn btn-outline-success mb-2"
                onclick="window.location.href='${pageContext.request.contextPath}/resources/groupware/sample/부서목록_샘플.xlsx'">
            <img src="<c:url value='/resources/groupware/images/excel.png'/>" id="ic-excel"> 샘플 양식 다운로드
        </button>
        	<pre class="sampleExcelInfo">최초 등록 시, 샘플양식을 다운로드 후<br>형식에 맞게 내용을 수정하여 일괄등록할 수 있습니다.</pre>

        <!-- 엑셀 업로드 폼 (Ajax 처리) -->
        <span style="font-size: 12px; font-weight: bold;">부서 일괄 등록</span>
        <form id="excelUploadForm" enctype="multipart/form-data">
            <input type="file" name="file" id="excelFile" accept=".xlsx" required />
            <button type="button" class="btn-green" id="uploadBtn">업로드 등록</button>
        </form>

        <p style="font-size: 11.5px; margin-top: 5px; color: gray;">
            새 부서코드는 신규 등록되며, 기존 부서코드는 수정 처리됩니다.
        </p>
    </div>

    <!-- 결과 테이블 -->
    <h4>등록 결과</h4>
    <table>
        <thead>
            <tr>
                <th>상태</th>
                <th>부서코드</th>
                <th>상위 부서코드</th>
                <th>부서명</th>
                <th>부서장 ID</th>
                <th>생성일</th>
                <th>회사코드</th>
            </tr>
        </thead>
        <tbody id="resultTableBody">
            <!-- Ajax 결과 삽입 -->
        </tbody>
    </table>
    
    <script>
    	const contextPath = "${pageContext.request.contextPath}";
    </script>
    <script src="${pageContext.request.contextPath}/resources/groupware/js/organization/deptBulkRegist.js"></script>