document.addEventListener("DOMContentLoaded", function () {

    // =========================
    // 파일 업로드 이름 표시
    // =========================
    const fileUpload = document.getElementById("fileUpload");
    const fileData = document.getElementById("fileData");

    if (fileUpload) {
        fileUpload.addEventListener("change", function () {
            const files = this.files;

            if (files.length > 0) {
                const fileNames = Array.from(files)
                    .map(file => file.name)
                    .join(", ");

                fileData.value = fileNames;
            } else {
                fileData.value = "";
            }
        });
    }

    // =========================
    // 임시저장 버튼 처리
    // =========================
    const draftBtn = document.getElementById("isDraftButton");
    const draftInput = document.getElementById("isDraftInput");

    if (draftBtn) {
        draftBtn.addEventListener("click", function () {
            draftInput.value = "Y";
            this.closest("form").submit();
        });
    }

});


// =========================
// 드래프트 로드 함수
// =========================
function loadDraftFromEl(el) {
    const title = el.getAttribute("data-draft-title") || "";
    const content = el.getAttribute("data-draft-content") || "";

    const titleEl = document.getElementById("title");
    const editorEl = document.getElementById("editor");

    if (titleEl) titleEl.value = title;
    if (editorEl) editorEl.value = content;
}