/**
 * employeeList.js (Refactored + Dept Bulk Update)
 * - 전역변수 금지: #gwConfig data-* 로 contextPath/companyNo 읽기
 * - DataTables 서버사이드 목록
 * - 사원 등록(FormData)
 * - 엑셀 다운로드
 * - 직위 변경(일괄 수정) : #mainPosition -> #bulkModal 오픈 -> #confirmBulkUpdate PUT
 * - ✅ 부서 변경(일괄 수정) : #mainDept -> #deptBulkModal 오픈 -> #confirmDeptBulkUpdate PUT
 * - 사원 삭제(일괄 삭제)
 *
 * ✅ 전제:
 * - JSP에 #gwConfig(data-context-path, data-company-no) 존재
 * - JSP에 #bulkModal, #bulkCount, #bulkValue, #confirmBulkUpdate 존재
 * - ✅ JSP에 #deptBulkModal, #deptBulkCount, #deptBulkValue, #confirmDeptBulkUpdate 존재
 * - 테이블 체크박스는 name="empCheck", class="row-checkbox"
 */

console.log("✅ employeeList.js loaded");

$(document).ready(function () {
  console.log("✅ employeeList.js document ready");

  // =========================
  // 0) 설정값
  // =========================
  const $cfg = $("#gwConfig");
  const contextPath = $cfg.data("context-path") || "";
  const companyNo = $cfg.data("company-no") || ""; // 로그/확장용

  console.log("✅ contextPath:", contextPath);
  console.log("✅ companyNo:", companyNo);

  // =========================
  // 1) DataTable
  // =========================
  const table = $("#multi-filter-select").DataTable({
    serverSide: true,
    processing: true,
    dom: "lfrtip",
    ajax: {
      url: contextPath + "/employee/admin/ajaxList",
      type: "GET",
      data: function (d) {
        d.searchType = $('select[name="searchType"]').val();
        d.searchWord = $("#custom-search-input").val();
      },
      error: function (xhr) {
        console.error("❌ DataTables ajax error:", xhr.status, xhr.responseText);
      }
    },
    columns: [
      {
        data: "empId",
        render: (data) =>
          `<input type="checkbox" class="row-checkbox" name="empCheck" value="${data}"/>`,
        orderable: false,
        searchable: false
      },
      { data: "empNo" },
      { data: "empNm" },
      { data: "deptName" },
      { data: "positionName" },
      { data: "empEmail" }
    ],
    lengthMenu: [10, 15, 20, 25],
    info: false
  });

  // 기본 검색 UI 교체
  $(".dataTables_filter").html(`
    <select name="searchType" class="form-select form-select-sm" style="width:120px;">
      <option value="empNm">이름</option>
      <option value="deptName">부서</option>
      <option value="positionName">직책</option>
    </select>
    <input type="text" id="custom-search-input" class="form-control form-control-sm" style="width:150px;">
    <button class="btn btn-primary btn-sm" id="dt-search-btn" type="button">검색</button>
  `);

  $(document).on("click", "#dt-search-btn", () => table.ajax.reload());
  $(document).on("keydown", "#custom-search-input", (e) => {
    if (e.key === "Enter") $("#dt-search-btn").click();
  });

  // =========================
  // 2) 체크박스
  // =========================
  $(document).on("change", "#checkAll", function () {
    $(".row-checkbox").prop("checked", this.checked);
  });

  $(document).on("change", ".row-checkbox", function () {
    const allChecked = $(".row-checkbox").length === $(".row-checkbox:checked").length;
    $("#checkAll").prop("checked", allChecked);
  });

  function getCheckedEmpIds() {
    return $(".row-checkbox:checked").map((_, e) => e.value).get();
  }

  // =========================
  // 3) 사원 등록
  // =========================
  $("#employeeForm").on("submit", function (e) {
    e.preventDefault();
    const formData = new FormData(this);

    $.ajax({
      url: contextPath + "/employee/admin/new",
      type: "POST",
      data: formData,
      processData: false,
      contentType: false,
      success: () => {
        if (window.Swal) {
          Swal.fire({
            icon: "success",
            title: "등록 완료",
            timer: 1500,
            showConfirmButton: false
          });
        }
        $("#employeeModal").modal("hide");
        table.ajax.reload(null, false);
      },
      error: (xhr) => {
        console.error("❌ employee create error:", xhr.status, xhr.responseText);
        if (window.Swal) Swal.fire("오류", "등록 중 문제가 발생했습니다.", "error");
      }
    });
  });

  // =========================
  // 4) 엑셀 다운로드
  // =========================
  $("#downExcel").on("click", function (e) {
    e.preventDefault();

    const q = new URLSearchParams({
      searchType: $('select[name="searchType"]').val() || "",
      searchWord: $("#custom-search-input").val() || ""
    });

    window.location.href = contextPath + "/employee/admin/excelDownload?" + q.toString();
  });

  // =========================
  // 5) 직위 변경 (기존처럼: 버튼 -> 모달 -> 변경)
  // =========================
  $(document).on("click", "#mainPosition", function (e) {
    e.preventDefault();

    const checked = $(".row-checkbox:checked");
    if (checked.length === 0) {
      if (window.Swal) {
        Swal.fire({
          toast: true,
          position: "top",
          icon: "warning",
          title: "먼저 변경할 사원을 선택하세요!",
          showConfirmButton: false,
          timer: 2000
        });
      }
      return;
    }

    $("#bulkCount").text(checked.length);
    $("#bulkModal").modal("show");
  });

  $(document).on("click", "#confirmBulkUpdate", function () {
    const empIds = getCheckedEmpIds();

    if (empIds.length === 0) {
      if (window.Swal) Swal.fire({ icon: "warning", title: "선택된 사원이 없습니다." });
      return;
    }

    const value = $("#bulkValue").val();
    const fieldType = "position";

    let message = `✅ ${empIds.length}명 직위 일괄 수정 완료!`;
    if (empIds.length === 1) {
      const $row = $(`input.row-checkbox[value="${empIds[0]}"]`).closest("tr");
      const empName = $row.find("td:eq(2)").text() || "";
      if (empName) message = `✅ ${empName} 직위 수정 완료!`;
    }

    $.ajax({
      url: contextPath + "/employee/admin/bulkUpdate",
      method: "PUT",
      contentType: "application/json",
      data: JSON.stringify({ empIds, fieldType, value }),
      success: () => {
        if (window.Swal) {
          Swal.fire({
            toast: true,
            position: "top",
            icon: "success",
            title: message,
            showConfirmButton: false,
            timer: 2000
          });
        }
        $("#bulkModal").modal("hide");
        $("#checkAll").prop("checked", false);
        table.ajax.reload(null, false);
      },
      error: (xhr) => {
        console.error("❌ bulkUpdate(position) error:", xhr.status, xhr.responseText);
        if (window.Swal) {
          Swal.fire({
            toast: true,
            position: "top",
            icon: "error",
            title: "수정 실패!",
            text: "잠시 후 다시 시도해주세요.",
            showConfirmButton: false,
            timer: 2000
          });
        }
      }
    });
  });

  // =========================
  // 5.5) ✅ 부서 변경 (버튼 -> 모달 -> 변경)
  // =========================

  // ✅ 부서 변경 버튼 클릭 -> 모달 열기 + 부서 목록 로딩
  $(document).on("click", "#mainDept", function (e) {
    e.preventDefault();

    const empIds = getCheckedEmpIds();
    if (empIds.length === 0) {
      if (window.Swal) {
        Swal.fire({
          toast: true,
          position: "top",
          icon: "warning",
          title: "먼저 사원을 선택하세요!",
          timer: 2000,
          showConfirmButton: false
        });
      }
      return;
    }

    $("#deptBulkCount").text(empIds.length);
    $("#deptBulkModal").modal("show");

    // 부서 목록 로딩(한번만)
    if ($("#deptBulkValue option").length <= 1) {
      $.ajax({
        url: contextPath + "/employee/departments",
        type: "GET",
        success: function (departments) {
          const $sel = $("#deptBulkValue");
          departments.forEach((d) => {
            $sel.append(`<option value="${d.deptCd}">${d.deptName}</option>`);
          });
        },
        error: function (xhr) {
          console.error("❌ departments load fail:", xhr.status, xhr.responseText);
          if (window.Swal) Swal.fire("오류", "부서 목록을 불러오지 못했습니다.", "error");
        }
      });
    }
  });

  // ✅ 부서 변경 확정 -> bulkUpdate 호출
  $(document).on("click", "#confirmDeptBulkUpdate", function () {
    const empIds = getCheckedEmpIds();

    if (empIds.length === 0) {
      if (window.Swal) Swal.fire({ icon: "warning", title: "선택된 사원이 없습니다." });
      return;
    }

    const deptCd = $("#deptBulkValue").val(); // ""이면 '미지정' 정책(백엔드에서 NULL 처리 권장)
    const fieldType = "department";

    $.ajax({
      url: contextPath + "/employee/admin/bulkUpdate",
      method: "PUT",
      contentType: "application/json",
      data: JSON.stringify({
        empIds,
        fieldType,
        value: deptCd
      }),
      success: function () {
        if (window.Swal) {
          Swal.fire({
            toast: true,
            position: "top",
            icon: "success",
            title: "부서 변경 완료",
            timer: 2000,
            showConfirmButton: false
          });
        }
        $("#deptBulkModal").modal("hide");
        $("#checkAll").prop("checked", false);
        table.ajax.reload(null, false);
      },
      error: function (xhr) {
        console.error("❌ bulkUpdate(department) fail:", xhr.status, xhr.responseText);
        if (window.Swal) {
          Swal.fire({
            toast: true,
            position: "top",
            icon: "error",
            title: "부서 변경 실패",
            timer: 2000,
            showConfirmButton: false
          });
        }
      }
    });
  });

  // =========================
  // 6) 사원 삭제
  // =========================
  $("#bulkDelete").on("click", function () {
    const empIds = getCheckedEmpIds();

    if (empIds.length === 0) {
      if (window.Swal) {
        Swal.fire({
          toast: true,
          position: "top",
          icon: "warning",
          title: "삭제할 사원을 선택해주세요!",
          showConfirmButton: false,
          timer: 2000
        });
      }
      return;
    }

    const doDelete = () => {
      $.ajax({
        url: contextPath + "/employee/admin/delete",
        method: "DELETE",
        contentType: "application/json",
        data: JSON.stringify(empIds),
        success: () => {
          if (window.Swal) {
            Swal.fire({
              toast: true,
              position: "top",
              icon: "success",
              title: "삭제 완료",
              showConfirmButton: false,
              timer: 2000
            });
          }
          $("#checkAll").prop("checked", false);
          table.ajax.reload(null, false);
        },
        error: (xhr) => {
          console.error("❌ delete error:", xhr.status, xhr.responseText);
          if (window.Swal) Swal.fire("오류", "삭제 중 문제가 발생했습니다.", "error");
        }
      });
    };

    if (window.Swal) {
      Swal.fire({
        icon: "warning",
        title: "정말 삭제하시겠습니까?",
        text: `${empIds.length}명 삭제합니다.`,
        showCancelButton: true,
        confirmButtonText: "삭제",
        cancelButtonText: "취소",
        confirmButtonColor: "#dc3545"
      }).then((r) => {
        if (r.isConfirmed) doDelete();
      });
    } else {
      if (confirm(`${empIds.length}명 삭제하시겠습니까?`)) doDelete();
    }
  });
});
