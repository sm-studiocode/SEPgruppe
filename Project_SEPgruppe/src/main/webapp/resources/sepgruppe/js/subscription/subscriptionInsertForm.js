/** 
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 3. 21.     	손현진            최초 생성
 *
 * </pre>
 */
document.addEventListener("DOMContentLoaded", function() {
	const myModalEl = document.querySelector('#exampleModal');
	myModalEl.addEventListener('shown.bs.modal', event => {
		const priceDisplay = document.getElementById("priceDisplay");
		// JSP EL 변수는 페이지 로딩 시 값이 치환됨

		// 결제 방식 변경 시 가격 정보 갱신
		const paymentRadios = document.getElementsByName("paymentMethod");
		paymentRadios.forEach(function(radio) {
			radio.addEventListener("change", function() {
				if (this.value === "monthly") {
					priceDisplay.innerHTML = this.dataset.price + "원 <span>(부가세 별도)</span>";
				} else if (this.value === "yearly") {
					priceDisplay.innerHTML = this.dataset.price + "원 <span>(부가세 별도)</span>";
				}
			});
		});
	});
});