<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/sepgruppe/css/footer.css" />

<footer class="site-footer section-padding section-footer-fix">
    <div class="container">
        <div class="row">

            <div class="col-lg-3 col-12 mb-4 pb-2">
                <a class="navbar-brand mb-2" href="<c:url value='/'/>">
                    <i class="bi-back"></i>
                    <span>SEP</span>
                </a>
            </div>

            <div class="col-lg-3 col-md-4 col-6">
                <h6 class="site-footer-title mb-3">Resources</h6>
                <ul class="site-footer-links">
                    <li><a href="<c:url value='/'/>" class="site-footer-link">Home</a></li>
                    <li><a href="#" class="site-footer-link">How it works</a></li>
                    <li><a href="#" class="site-footer-link">FAQs</a></li>
                    <li><a href="#" class="site-footer-link">Contact</a></li>
                </ul>
            </div>

            <div class="col-lg-3 col-md-4 col-6">
                <h6 class="site-footer-title mb-3">Information</h6>
                <p class="text-white mb-1">
                    <a href="tel:000-0000-0000" class="site-footer-link">000-0000-0000</a>
                </p>
                <p class="text-white">
                    <a href="mailto:support@sep.com" class="site-footer-link">support@sep.com</a>
                </p>
            </div>

            <div class="col-lg-3 col-md-4 col-12 ms-auto">
                <p class="copyright-text mt-4">
                    © 2025 SEP. All rights reserved.
                </p>
            </div>

        </div>
    </div>
</footer>
