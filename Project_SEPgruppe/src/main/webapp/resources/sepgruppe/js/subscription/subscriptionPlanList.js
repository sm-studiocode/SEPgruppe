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

document.addEventListener("DOMContentLoaded", ()=>{
	const myModalEl = document.querySelector('#exampleModal');
	myModalEl.addEventListener('show.bs.modal', event => {
		let aTag = event.relatedTarget;
		let url = aTag.href;
		
		$.ajax({
			url:url,
			dataType:"html",
			success:function(resp){
				$(myModalEl).find(".modal-body").html(resp);
			}
		});
	})
});

