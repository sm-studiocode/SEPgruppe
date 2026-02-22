function showErrorModal(message) {
	const modalBody = document.getElementById('errorModalBody');
	modalBody.textContent = message;

	const errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
	errorModal.show();
}

function downloadFile(event, element) {
	event.preventDefault();

	const fileNo = element.dataset.fileNo;
	const noticeNo = '${noticeNo}';
	const downloadUrl = "/sep/notice/" + noticeNo + "/download?attachFileNo=" + fileNo;

	// HEAD 요청으로 파일 존재 여부 확인
	fetch(downloadUrl, { method: 'HEAD' })
		.then(response => {
			if (response.ok) {
				window.location.href = downloadUrl;
			} else if (response.status === 404) {
				showErrorModal("❗ 파일을 찾을 수 없습니다. 관리자에게 문의하세요.");
			} else {
				showErrorModal("⚠️ 파일 다운로드 중 오류가 발생했습니다. (" + response.status + ")");
			}
		})
		.catch(error => {
			console.error("에러 발생:", error);
			showErrorModal("🚫 서버에 접속할 수 없습니다. 네트워크 상태를 확인하세요.");
		});
}