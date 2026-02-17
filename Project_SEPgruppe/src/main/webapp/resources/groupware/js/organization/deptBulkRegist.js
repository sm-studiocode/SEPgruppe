$(function () {

  // ✅ CSRF 토큰/헤더명 읽기 (index.jsp <head>의 meta)
  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

  $("#uploadBtn").on("click", function () {

    const fileInput = $("#excelFile")[0];

    if (!fileInput.files.length) {
      showToast("파일을 선택해주세요.", "warning");
      return;
    }

    const formData = new FormData();
    formData.append("file", fileInput.files[0]);

    $.ajax({
      url: contextPath + "/department/bulkInsertExcel",
      type: "POST",
      data: formData,
      processData: false,
      contentType: false,

      // ✅ CSRF 헤더 추가 (필수)
      beforeSend: function (xhr) {
        if (csrfToken && csrfHeader) {
          xhr.setRequestHeader(csrfHeader, csrfToken);
        }
      },

      success: function (deptList) {

        const tbody = $("#resultTableBody");
        tbody.empty();

        if (!deptList || deptList.length === 0) {
          tbody.append("<tr><td colspan='7'>등록된 데이터가 없습니다.</td></tr>");
          showToast("등록된 데이터가 없습니다.", "info");
          return;
        }

        deptList.forEach(function (dept) {

          const row = `
            <tr>
              <td class="status-box">${dept.status || "성공"}</td>
              <td>${dept.deptCd || ""}</td>
              <td>${dept.parentDeptCd || ""}</td>
              <td>${dept.deptName || ""}</td>
              <td>${dept.managerEmpId || ""}</td>
              <td>${dept.createAt || ""}</td>
              <td>${dept.companyNo || ""}</td>
            </tr>
          `;

          tbody.append(row);
        });

        showToast("엑셀 업로드 및 등록 완료!", "success");
      },

      error: function (xhr) {
        console.log("status:", xhr.status, "response:", xhr.responseText);
        showToast("등록 실패. 관리자에게 문의하세요.", "error");
      }
    });
  });
});

function showToast(message, icon = 'info') {
  Swal.fire({
    toast: true,
    position: 'top',
    icon: icon,
    title: message,
    showConfirmButton: false,
    timer: 2500
  });
}
