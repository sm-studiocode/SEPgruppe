<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 3. 21.     	손현진            최초 생성
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
</head>
<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/sepgruppe/css/subscriptionPlan.css" />
<body>

<div class="explanation">
  <h2>${plan.planType} 플랜 안내</h2>
  <p>
    ○ ${plan.planType} 플랜 가용 인원 : ${plan.maximumPeople }명 제한<br>
    ○ ${plan.planType} 플랜 월 결제 가격 : 1개월 ${plan.monthlyPrice }원<br>
    ○ ${plan.planType} 플랜 연 결제 가격 : 1개월 ${plan.annualPrice }원<br>
    각 옵션을 선택하면 우측에 요금 정보가 갱신됩니다.
  </p>
</div>

	<div class="subcontainer">
		<!-- 좌측 입력 폼 영역 -->
		<div class="form-left">

			<!-- 제품 유형 선택 -->
			<h2>구독 신청 유형</h2>
			<div class="input-group">
				<div>
					<label> <input type="radio" name="productType"
						value="${plan.planType}" checked/> ${plan.planType}
					</label>
				</div>
			</div>

			<!-- 결제 방식 선택 -->
			<h2>결제 방식</h2>
			<div class="input-group">
				<div>
					<label> 
					<input type="radio" name="paymentMethod" value="monthly" data-price="${plan.monthlyPrice }" /> 월 결제
					</label>
					<label> 
					<input type="radio" name="paymentMethod" value="yearly" data-price="${plan.annualPrice }" /> 1년 결제 (15% 할인)
					</label> 
				</div>
			</div>

		</div>

		<!-- 우측 요약 영역 -->
		<div class="form-right">
			<h3>월 결제 금액</h3>
			<!-- 실제 로직으로 동적으로 변경될 수 있는 영역 -->
			<div class="price" id="priceDisplay"></div>

			<div class="notice">구독 플랜 공지</div>
			<div>
			</br>
			<c:url value="/payment/subPayment" var="paymentUrl">
				<c:param name="what" value="${plan.planType }" />
			</c:url>
			<a href="${paymentUrl }" class="btn btn-primary">구독하기</a>
			</div>
		</div>
	</div>


</body>
</html>
<script src="${pageContext.request.contextPath }/resources/sepgruppe/js/subscription/subscriptionInsertForm.js"></script>
