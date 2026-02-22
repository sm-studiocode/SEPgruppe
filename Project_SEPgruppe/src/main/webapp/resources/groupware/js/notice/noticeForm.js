document.getElementById("fileUpload").addEventListener("change", function () {
    const files = this.files;
    if (files.length > 0) {
        const fileNames = Array.from(files).map(file => file.name).join(", ");
        document.getElementById("fileData").value = fileNames;
    } else {
        document.getElementById("fileData").value = ""; // 파일이 없으면 초기화
    }
});