function removeFileFromList(fileNo) {
	const attachFileNo = document.querySelector("#attachFileNo");
	const currentValue = attachFileNo.value;
	const fileElement = document.querySelector("#file-" + fileNo);

	attachFileNo.value = currentValue ? currentValue + "," + fileNo : fileNo;

	if (fileElement) {
		fileElement.remove();
	}
}