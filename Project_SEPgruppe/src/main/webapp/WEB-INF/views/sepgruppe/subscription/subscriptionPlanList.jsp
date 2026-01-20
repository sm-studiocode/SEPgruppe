<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<security:csrfMetaTags/>
<section class="explore-section section-padding" id="section_2">
	<div class="container">
		<div class="col-12 text-center">
			<h2 class="mb-4">구독 플랜 정보</h2>
		</div>
	</div>
	<div class="container">
		<div class="row">
			<div class="col-12">
				<div class="tab-content" id="myTabContent">
					<div class="tab-pane fade show active" id="design-tab-pane"
						role="tabpanel" aria-labelledby="design-tab" tabindex="0">
						<div class="row">
							<c:choose>
								<c:when test="${not empty planList }">
									<c:forEach items="${planList }" var="plan">
										<div class="col-lg-4 col-md-6 col-12 mb-4 mb-lg-0">
											<div class="custom-block bg-white shadow-lg">
												<c:url value="/subscriptionPlan/${plan.planType}" var="detailSub">
												</c:url>
												<a href="${detailSub }" class="btn" data-bs-toggle="modal"
													data-bs-target="#exampleModal">
													<div class="d-flex">
														<div>
															<h5 class="mb-2">${plan.planType}</h5>
															<p class="mb-0">인원 ${plan.maximumPeople }명 제한</p>
														</div>
														<h6>
															<span class="badge text-bg-success">${plan.monthlyPrice }원</span>
														</h6>
													</div> <img
													src="${pageContext.request.contextPath}/resources/sepgruppe/images/PlanA.png"
													class="plan-class">
												</a>
											</div>
										</div>
									</c:forEach>
								</c:when>
							</c:choose>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<!-- Modal -->
<div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h1 class="modal-title fs-5" id="exampleModalLabel">플랜 정보</h1>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
      ...
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
	
</section>
<script>window.ctx='${pageContext.request.contextPath}';</script>
<script src="https://cdn.iamport.kr/js/iamport.payment-1.2.0.js"></script>
<script src="${pageContext.request.contextPath }/resources/sepgruppe/js/payment/paymentForm.js"></script>
<script src="${pageContext.request.contextPath }/resources/sepgruppe/js/subscription/subscriptionPlanList.js"></script>
<script src="${pageContext.request.contextPath }/resources/sepgruppe/js/subscription/subscriptionInsertForm.js"></script>