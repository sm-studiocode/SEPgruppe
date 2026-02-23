function showErrorModal(message) {
	const modalBody = document.getElementById('errorModalBody');
	modalBody.textContent = message;

	const errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
	errorModal.show();
}

function downloadFile(event, element) {
	event.preventDefault();

	const fileNo = element.dataset.fileNo;

	// ✅ JSP에서 내려준 전역변수 사용 (noticeNo/contextPath)
	if (!window.noticeNo) {
		showErrorModal("⚠️ noticeNo를 찾을 수 없습니다. (페이지 변수 누락)");
		return;
	}
	if (!fileNo) {
		showErrorModal("⚠️ attachFileNo를 찾을 수 없습니다.");
		return;
	}

	const downloadUrl =
		(window.contextPath || "") +
		"/notice/" + encodeURIComponent(window.noticeNo) +
		"/download?attachFileNo=" + encodeURIComponent(fileNo);

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