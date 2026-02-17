/**
 * deptSelectPopup.js
 * - employeeList.jsp의 "부서 선택" 팝업 전용
 * - 기존 sidepopup.js (메인 조직도) 절대 건드리지 않음
 * - 메인에서 이미 쓰는 API 그대로 재사용:
 *   /organization/popup/root
 *   /organization/popup/children
 *   /organization/popup/search
 */

console.log("✅ deptSelectPopup.js loaded");

$(document).ready(function () {
  console.log("✅ deptSelectPopup.js document ready");

  const $cfg = $("#gwConfig");
  const contextPath = $cfg.data("context-path") || "";

  const $popup = $("#deptOrgPopup");
  const popupContextPath = $popup.data("context-path") || contextPath;

  // ✅ 여기서 API 고정 (404 뜬 /organization/admin/deptTree 안 씀)
  const ROOT_URL = `${popupContextPath}/organization/popup/root`;
  const CHILD_URL = `${popupContextPath}/organization/popup/children`;
  const SEARCH_URL = `${popupContextPath}/organization/popup/search`;

  console.log("✅ popupContextPath:", popupContextPath);
  console.log("✅ ROOT_URL:", ROOT_URL);

  function showPopup() {
    $popup.css("display", "block");
    if (!$.ui.fancytree.getTree("#deptTree")) initTree();
  }

  function closePopup() {
    $popup.css("display", "none");
  }

  // ✅ 부서 선택 버튼
  $(document).on("click", "#loadDeptBtn", function () {
    console.log("✅ loadDeptBtn clicked -> open dept popup");
    showPopup();
  });

  // ✅ 닫기
  $(document).on("click", "#deptOrgCloseBtn", function () {
    closePopup();
  });

  // ✅ 바깥 클릭 닫기
  $(document).on("click", "#deptOrgPopup", function (e) {
    if (e.target.id === "deptOrgPopup") closePopup();
  });

  function initTree() {
    console.log("✅ initTree start");

    $("#deptTree").fancytree({
      source: {
        url: ROOT_URL,
        cache: false
      },

      lazyLoad: function (event, data) {
        const node = data.node;
        data.result = {
          url: CHILD_URL,
          data: { parent: node.key },
          cache: false
        };
      },

      activate: function (event, data) {
        const node = data.node;
        const payload = node.data && node.data.data ? node.data.data : null;

        // ✅ 사원 클릭은 무시(부서만 선택)
        if (payload && payload.empNm) return;

        const deptCd = (payload && payload.deptCd) ? payload.deptCd : node.key;
        const deptName = (payload && payload.deptName) ? payload.deptName : node.title;

        $("#deptCd").val(deptCd);
        $("#deptSelectedLabel").text(deptName).removeClass("text-muted");

        closePopup();
      }
    });

    // ✅ 검색
    $(document).on("click", "#dept-search-btn", function () {
      const keyword = ($("#dept-employee-search").val() || "").trim();
      if (!keyword) {
        if (window.Swal) Swal.fire({ icon: "warning", title: "검색어 누락", text: "검색어를 입력해주세요." });
        else alert("검색어를 입력해주세요.");
        return;
      }

      $.ajax({
        url: SEARCH_URL,
        type: "GET",
        data: { keyword },
        success: function (data) {
          $("#deptTree").fancytree("getTree").reload(data);
        },
        error: function (xhr) {
          console.error("❌ search failed", xhr.status, xhr.responseText);
          if (xhr.status === 401) location.href = `${popupContextPath}/login`;
          else if (window.Swal) Swal.fire({ icon: "error", title: "검색 실패", text: "오류가 발생했습니다." });
          else alert("검색 실패");
        }
      });
    });

    $(document).on("keydown", "#dept-employee-search", function (e) {
      if (e.key === "Enter") $("#dept-search-btn").click();

      const keyword = ($(this).val() || "").trim();
      if (keyword === "") {
        $("#deptTree").fancytree("getTree").reload({
          url: ROOT_URL,
          cache: false
        });
      }
    });
  }
});
