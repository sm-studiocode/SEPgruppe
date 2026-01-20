/** 
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 4. 10.     	SJH            최초 생성
 *
 * </pre>
 */

// 필터링 기능
document.addEventListener("DOMContentLoaded", function() {
	const table = new simpleDatatables.DataTable("#datatablesSimple");
	const planFilter = document.getElementById("planFilter");

	planFilter.addEventListener("change", function() {
		const selected = this.value;
		if (selected === "") {
			table.search(""); // 전체보기
		} else {
			table.search(selected); // planType 컬럼 기준 검색
		}
	});
});
