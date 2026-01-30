document.addEventListener("DOMContentLoaded", function(){

	const $searchForm = $("#search-form");
	const $searchUi = $("#search-ui"); // 검색필터옵션
	const $searchBtn = $("#searchBtn"); // 검색버튼
	const $searchEnter = $('#searchEnter'); // 검색 input 태그

	$searchForm.find(":input[data-init-value]").each(function(idx, input){
		let initValue = input.dataset.initValue;
		$(input).val(initValue);
	});
	$searchUi.find(":input[data-init-value]").each(function(idx, input){
		let initValue = input.dataset.initValue;
		$(input).val(initValue);
	});

	fnPaging = function(page){
		$searchForm.find("[name='page']").val(page);
		$searchForm.submit();
	}

	$searchBtn.on("click", function(){
		console.log("버튼 눌림");
		let $searchUi = $(this).parents("#search-ui");
		$searchUi.find(":input[name]").each(function(idx, input){
			let name = input.name;
			let value = $(input).val();
			$searchForm.find(`[name='${name}']`).val(value);
		}); //each end

		console.log("searchType:", $searchForm.find("[name='searchType']").val());
		console.log("searchWord:", $searchForm.find("[name='searchWord']").val());

		$searchForm.submit();
	});

/*	$searchEnter.on('keypress', function(event){
		if(event.key === 'Enter'){
			event.preventDefault();
			$searchBtn.click();
		}
	});*/
});