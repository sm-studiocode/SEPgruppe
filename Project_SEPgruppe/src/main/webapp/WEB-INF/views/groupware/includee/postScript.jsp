<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!-- script 로딩 -->
<script src = "https://cdnjs.cloudflare.com/ajax/libs/axios/1.7.8/axios.min.js"></script>

<!-- Fonts and icons -->

<script src="${pageContext.request.contextPath }/resources/groupware/kaiadmin/assets/js/plugin/webfont/webfont.min.js"></script>
<script>
  WebFont.load({
    google: { families: ["Public Sans:300,400,500,600,700"] },
    custom: {
      families: [
        "Font Awesome 5 Solid",
        "Font Awesome 5 Regular",
        "Font Awesome 5 Brands",
        "simple-line-icons",
      ],
      urls: ["${pageContext.request.contextPath }/resources/groupware/kaiadmin/assets/css/fonts.min.css"],
    },
    active: function () {
      sessionStorage.fonts = true;
    },
  });
</script>
<!-- ckEditor -->
<script src="${pageContext.request.contextPath }/resources/ckeditor/ckeditor.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.5.3/dist/sweetalert2.all.min.js"></script>

<!-- fancytree -->
<script src="${pageContext.request.contextPath }/resources/fancytree/jquery.fancytree-all-deps.min.js"></script>
<script src="${pageContext.request.contextPath }/resources/fancytree/modules/jquery.fancytree.dnd.js"></script>	<!-- drag and drop -->

<!--   Core JS Files   -->
<script src="${pageContext.request.contextPath }/resources/groupware/kaiadmin/assets/js/core/popper.min.js"></script>
<script src="${pageContext.request.contextPath }/resources/groupware/kaiadmin/assets/js/core/bootstrap.min.js"></script>

<!-- jQuery Scrollbar -->
<script src="${pageContext.request.contextPath }/resources/groupware/kaiadmin/assets/js/plugin/jquery-scrollbar/jquery.scrollbar.min.js"></script>

<!-- Chart JS -->
<script src="${pageContext.request.contextPath }/resources/groupware/kaiadmin/assets/js/plugin/chart.js/chart.min.js"></script>

<!-- jQuery Sparkline -->
<script src="${pageContext.request.contextPath }/resources/groupware/kaiadmin/assets/js/plugin/jquery.sparkline/jquery.sparkline.min.js"></script>

<!-- Chart Circle -->
<script src="${pageContext.request.contextPath }/resources/groupware/kaiadmin/assets/js/plugin/chart-circle/circles.min.js"></script>

<!-- Datatables -->
<script src="${pageContext.request.contextPath }/resources/groupware/kaiadmin/assets/js/plugin/datatables/datatables.min.js"></script>

<!-- Bootstrap Notify -->
<script src="${pageContext.request.contextPath }/resources/groupware/kaiadmin/assets/js/plugin/bootstrap-notify/bootstrap-notify.min.js"></script>

<!-- jQuery Vector Maps -->
<script src="${pageContext.request.contextPath }/resources/groupware/kaiadmin/assets/js/plugin/jsvectormap/jsvectormap.min.js"></script>
<script src="${pageContext.request.contextPath }/resources/groupware/kaiadmin/assets/js/plugin/jsvectormap/world.js"></script>

<!-- Sweet Alert -->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<!-- Kaiadmin JS -->
<script src="${pageContext.request.contextPath }/resources/groupware/kaiadmin/assets/js/kaiadmin.min.js"></script>
<script src="${pageContext.request.contextPath }/resources/groupware/kaiadmin/assets/js/content.js"></script>

<!-- Daum Address API -->
<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src="${pageContext.request.contextPath }/resources/daumaddress/daumAPI.js"></script>

<!-- SockJS 및 Stomp.js 라이브러리 로드 -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.0/sockjs.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>

<!-- 페이징처리 -->
<script src="${pageContext.request.contextPath }/resources/groupware/js/paging.js"></script>

<!-- 차트와 테이블 부트스트랩 -->
<script src="https://cdn.jsdelivr.net/npm/simple-datatables@7.1.2/dist/umd/simple-datatables.min.js" crossorigin="anonymous"></script>
<script src="${pageContext.request.contextPath}/resources/sepgruppe/startbootstrap-sb-admin-gh-pages/js/scripts.js"></script>
<script src="${pageContext.request.contextPath}/resources/sepgruppe/startbootstrap-sb-admin-gh-pages/js/datatables-simple-demo.js"></script>

<!-- 알람 -->
<script src="${pageContext.request.contextPath }/resources/groupware/js/alarm/presence.js"></script>
<%-- <script src="${pageContext.request.contextPath }/resources/groupware/js/alarm/alarm.js"></script> --%>
