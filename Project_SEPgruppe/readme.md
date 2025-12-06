### 1. servlet spec 4.0
### 2. jsp spec 2.3
### 3. el spec 3.0
### 4. tomcat version 8.5
### 5. JSTL spec 1.2
### 6. base package javax.servlet.XXX
### 7. downgrade check
* 1) pom.xml 의 servlet, jsp, jstl 버전 변경
* 2) project properties -> project facets -> dynamic web module 버전 확인
* 3) web.xml 파일의 버전 확인
* 4) IndexControllerServelt 에서 import 구분의 qualified name 변경
* 5) sidebar.jsp 의 커스텀 태그 로딩시 uri 설정 변경
(jakarta.tags.core -> http://java.sun.com/jsp/jstl/core)



	 
