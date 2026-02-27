/**
 * employeeRoleManage.js
 * - employeeList.jsp의 권한 관리 모달 전용
 * - 전사 공지 관리자 / 부서 공지 관리자 부여/회수
 *
 * 전제:
 * - #gwConfig(data-context-path)
 * - 체크박스: .row-checkbox(name="empCheck")
 * - 서버 API:
 *   GET  /employee/admin/roles?empId=...
 *   POST /employee/admin/roles/grant   {empId, roleName, deptCd}
 *   POST /employee/admin/roles/revoke  {empId, roleName, deptCd}
 *   GET  /employee/departments
 *   ✅ GET  /employee/admin/summary?empId=...  (추가 추천)
 */

console.log("✅ employeeRoleManage.js loaded");

$(document).ready(function () {
  const $cfg = $("#gwConfig");
  const contextPath = $cfg.data("context-path") || "";

  let targetEmpId = null;
  let loadedNoticeRoles = [];     // 기존 공지 관련 role들(저장 전 revoke용)
  let targetDeptCd = null;        // ✅ 대상 직원 실제 부서코드

  function getCheckedEmpIds() {
    return $(".row-checkbox:checked").map((_, e) => e.value).get();
  }

  function toastWarn(msg) {
    if (window.Swal) {
      Swal.fire({ toast:true, position:"top", icon:"warning", title: msg, showConfirmButton:false, timer:2200 });
    } else alert(msg);
  }

  function toastOk(msg) {
    if (window.Swal) {
      Swal.fire({ toast:true, position:"top", icon:"success", title: msg, showConfirmButton:false, timer:1800 });
    } else alert(msg);
  }

  function toastErr(msg) {
    if (window.Swal) {
      Swal.fire({ toast:true, position:"top", icon:"error", title: msg, showConfirmButton:false, timer:2400 });
    } else alert(msg);
  }

  function setSaveEnabled(enabled) {
    $("#saveRolesBtn").prop("disabled", !enabled);
  }

  // ✅ 대상 직원 요약(부서) 로딩
  function loadTargetSummary(empId) {
    return $.ajax({
      url: contextPath + "/employee/admin/summary",
      method: "GET",
      data: { empId }
    }).then((res) => {
      targetDeptCd = res?.deptCd || null;
      return res;
    });
  }

  // 부서 목록 로딩
  function loadDepartments() {
    return $.ajax({
      url: contextPath + "/employee/departments",
      method: "GET"
    }).then((list) => {
      const $sel = $("#roleDeptSelect");
      $sel.empty().append(`<option value="">(부서 선택)</option>`);
      (list || []).forEach((d) => {
        $sel.append(`<option value="${d.deptCd}">${d.deptName}</option>`);
      });
    });
  }

  // 대상 직원 role 로딩
  function loadRoles(empId) {
    return $.ajax({
      url: contextPath + "/employee/admin/roles",
      method: "GET",
      data: { empId }
    }).then((roles) => {
      const roleNames = (roles || []).map(r => r.roleName);
      $("#chkNoticeAdmin").prop("checked", roleNames.includes("ROLE_NOTICE_ADMIN"));

      const deptRole = (roles || []).find(r => r.roleName === "ROLE_NOTICE_DEPT_ADMIN");
      $("#chkNoticeDeptAdmin").prop("checked", !!deptRole);
      $("#roleDeptSelect").val(deptRole?.deptCd || (targetDeptCd || ""));

      loadedNoticeRoles = (roles || []).filter(r =>
        r.roleName === "ROLE_NOTICE_ADMIN" || r.roleName === "ROLE_NOTICE_DEPT_ADMIN"
      );
    });
  }

  // 권한 부여/회수 호출
  function grantRole(empId, roleName, deptCd) {
    return $.ajax({
      url: contextPath + "/employee/admin/roles/grant",
      method: "POST",
      contentType: "application/json",
      data: JSON.stringify({ empId, roleName, deptCd: deptCd || null })
    });
  }

  function revokeRole(empId, roleName, deptCd) {
    return $.ajax({
      url: contextPath + "/employee/admin/roles/revoke",
      method: "POST",
      contentType: "application/json",
      data: JSON.stringify({ empId, roleName, deptCd: deptCd || null })
    });
  }

  function parseXhrMessage(xhr) {
    try {
      if (!xhr) return "";
      if (typeof xhr.responseText === "string" && xhr.responseText) return xhr.responseText;
      if (xhr.responseJSON) return JSON.stringify(xhr.responseJSON);
    } catch (e) {}
    return "";
  }

  // ✅ 부서공지 체크 시, 부서 셀렉트 자동 세팅 + 불일치 방지
  function syncDeptScopeUi() {
    const wantDeptAdmin = $("#chkNoticeDeptAdmin").is(":checked");

    if (!wantDeptAdmin) {
      // 부서공지 권한 안 쓰면 dept 선택은 그냥 둬도 됨
      setSaveEnabled(true);
      return;
    }

    if (!targetDeptCd) {
      toastWarn("대상 직원의 부서 정보를 확인할 수 없습니다. (부서 미지정?)");
      setSaveEnabled(false);
      return;
    }

    const selected = $("#roleDeptSelect").val() || "";
    if (!selected) {
      // 기본은 대상 직원 부서로 자동 선택
      $("#roleDeptSelect").val(targetDeptCd);
      setSaveEnabled(true);
      return;
    }

    if (selected !== targetDeptCd) {
      // 🔥 핵심: 타부서 선택은 서버에서 400이니까 UI에서 선제 차단
      toastWarn(`부서 공지 관리 권한은 대상 직원의 부서(${targetDeptCd})로만 부여할 수 있어요.`);
      setSaveEnabled(false);
    } else {
      setSaveEnabled(true);
    }
  }

  // ✅ 버튼 클릭 -> 모달 오픈
  $(document).on("click", "#roleManageBtn", function () {
    const ids = getCheckedEmpIds();
    if (ids.length !== 1) {
      toastWarn("권한 관리는 1명만 선택해서 진행하세요.");
      return;
    }

    targetEmpId = ids[0];
    targetDeptCd = null;
    loadedNoticeRoles = [];
    $("#roleTargetEmpId").text(targetEmpId);

    // 모달 초기화
    $("#chkNoticeAdmin").prop("checked", false);
    $("#chkNoticeDeptAdmin").prop("checked", false);
    $("#roleDeptSelect").val("");
    setSaveEnabled(true);

    // ✅ summary(부서) -> departments -> roles 로딩 후 모달 띄움
    Promise.resolve()
      .then(() => loadTargetSummary(targetEmpId)) // ✅ 대상 직원 부서 확보
      .then(() => loadDepartments())
      .then(() => loadRoles(targetEmpId))
      .then(() => {
        // 부서공지 권한 체크 상태/선택 값 검증
        syncDeptScopeUi();
        $("#roleModal").modal("show");
      })
      .catch((xhr) => {
        console.error("❌ role modal load error:", xhr);
        toastWarn("권한/부서 정보를 불러오지 못했습니다.");
      });
  });

  // ✅ 부서공지 체크/부서 선택 변경 시 즉시 검증
  $(document).on("change", "#chkNoticeDeptAdmin", function () {
    // 체크하면 대상 부서로 자동 맞춰주기
    if ($(this).is(":checked") && targetDeptCd) {
      $("#roleDeptSelect").val(targetDeptCd);
    }
    syncDeptScopeUi();
  });

  $(document).on("change", "#roleDeptSelect", function () {
    syncDeptScopeUi();
  });

  // ✅ 저장
  $(document).on("click", "#saveRolesBtn", function () {
    if (!targetEmpId) return;

    // 버튼 disabled 상태면 막기
    if ($(this).prop("disabled")) {
      toastWarn("부서 스코프 설정을 확인해주세요.");
      return;
    }

    const wantNoticeAdmin = $("#chkNoticeAdmin").is(":checked");
    const wantDeptAdmin = $("#chkNoticeDeptAdmin").is(":checked");

    let deptCd = $("#roleDeptSelect").val() || null;

    // 부서공지면 deptCd 필수 + 대상부서 일치 강제
    if (wantDeptAdmin) {
      if (!deptCd) {
        toastWarn("부서 공지 관리자 권한은 부서를 선택해야 합니다.");
        return;
      }
      if (targetDeptCd && deptCd !== targetDeptCd) {
        toastWarn(`부서 공지 관리 권한은 대상 직원 부서(${targetDeptCd})로만 저장할 수 있어요.`);
        return;
      }
    }

    // 1) 기존 공지 관련 role들만 회수
    const revokeJobs = loadedNoticeRoles.map(r => revokeRole(targetEmpId, r.roleName, r.deptCd));

    // 2) 원하는 role 부여
    const grantJobs = [];
    if (wantNoticeAdmin) {
      grantJobs.push(grantRole(targetEmpId, "ROLE_NOTICE_ADMIN", null));
    }
    if (wantDeptAdmin) {
      // ✅ 최종적으로도 targetDeptCd로 강제
      deptCd = targetDeptCd || deptCd;
      grantJobs.push(grantRole(targetEmpId, "ROLE_NOTICE_DEPT_ADMIN", deptCd));
    }

    Promise.allSettled([...revokeJobs, ...grantJobs])
      .then((results) => {
        // 실패가 있으면 사용자에게 표시
        const failed = results.filter(r => r.status === "rejected");
        if (failed.length > 0) {
          const msg = failed
            .map(f => parseXhrMessage(f.reason))
            .filter(Boolean)
            .join(" / ");

          console.error("❌ saveRoles failed:", failed);

          // 서버에서 dept mismatch 막으면 이 메시지가 보이게
          toastErr(msg || "저장 중 일부 요청이 실패했습니다.");
          return;
        }

        toastOk("권한이 저장되었습니다.");
        $("#roleModal").modal("hide");
      })
      .catch((e) => {
        console.error("❌ saveRoles error:", e);
        toastErr("저장 중 오류가 발생했습니다.");
      });
  });
});