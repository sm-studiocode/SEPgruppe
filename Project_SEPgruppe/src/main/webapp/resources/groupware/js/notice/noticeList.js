/**
 * 공지사항 분류 선택
 */
function fn_change(obj) {
	var category = document.querySelector('input[name="category"]:checked').value;
	location.href = `/sep/notice?category=` + category;
}

document.addEventListener('DOMContentLoaded', function(){
	const deleteBtn = document.querySelector('#deleteButton');
	const confirmDelete = document.querySelector('#confirmDelete');
	const noticeInput = document.querySelector('#noticeNoInput');
	const deleteNoticeModal = document.querySelector('#deleteNoticeModal');

	let noticeNo = [];

	const selectAll = document.querySelector('#select-all');
	const noticCheck = document.querySelectorAll('.noticCheck');

	if(deleteBtn){
		deleteBtn.addEventListener('click', function(){
			noticeNo = [];

			const selectNotice = document.querySelectorAll("input[name='noticeSelect']:checked");

			if(selectNotice.length === 0){
				Swal.fire({
					title: "삭제 실패",
					text: "선택된 공지사항이 없습니다.",
					icon: "question"
				});
				return;
			}

			selectNotice.forEach(function(checkbox){
				noticeNo.push(checkbox.value);
			});

			noticeInput.value = noticeNo.join(',');
			$('#deleteNoticeModal').modal('show');
		});
	}

	if(deleteNoticeModal){
		deleteNoticeModal.addEventListener('hidden.bs.modal', function(){
			noticeNo = [];
			noticeInput.value = '';
		});
	}

	if(confirmDelete){
		confirmDelete.addEventListener('click', function(event){
			event.preventDefault();

			Swal.fire({
				title: "삭제하시겠습니까?",
				icon: "warning",
				showCancelButton: true,
				confirmButtonText: "삭제",
				cancelButtonText: "취소"
			}).then((result) => {
				if(result.isConfirmed){
					const deleteForm = document.forms['deleteForm'];
					deleteForm.submit();
				}
			});
		});
	}

	if(selectAll){
		selectAll.addEventListener('click', function(){
			noticCheck.forEach(function(checkbox){
				checkbox.checked = selectAll.checked;
			});
		});
	}
});