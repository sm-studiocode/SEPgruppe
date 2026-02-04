/**
 * sidepopup.js (정석 B안)
 * - mode 제거
 * - 서버가 children에서 "하위부서 있으면 부서 / 없으면 사원" 판단
 * - FancyTreeDto<T> 구조(node.data.data) 사용
 */

const popup = document.getElementById("organizationPopup");
const sideContextPath = popup.dataset.contextPath;

function showOrganizationPopup() {
  popup.style.display = "block";
  if (!$.ui.fancytree.getTree("#depTree")) initFancyTree();
}

function closeOrganizationPopup() {
  popup.style.display = "none";
}

window.addEventListener("click", (e) => {
  if (e.target === popup) closeOrganizationPopup();
});

function initFancyTree() {
  $("#depTree").fancytree({
    source: {
      url: `${sideContextPath}/organization/popup/root`,
      cache: false,
    },

    lazyLoad: function (event, data) {
      const node = data.node;

      // ✅ 부서 노드만 lazy=true 로 내려오므로,
      // 폴더 노드 클릭 시 무조건 children 호출하면 됨
      data.result = {
        url: `${sideContextPath}/organization/popup/children`,
        data: { parent: node.key },
        cache: false,
      };
    },

    renderNode: function (event, data) {
      const node = data.node;
      const $span = $(node.span);

      const payload = node.data && node.data.data ? node.data.data : null;

      $span.find(".fancytree-icon").remove();

      // 사원(또는 검색 결과 OrganizationVO): empNm 존재
      if (payload && payload.empNm) {
        const parentPayload =
          node.parent && node.parent.data && node.parent.data.data
            ? node.parent.data.data
            : null;

        const isManager =
          parentPayload &&
          parentPayload.managerEmpId &&
          payload.empId &&
          parentPayload.managerEmpId === payload.empId;

        const iconClass = isManager ? "fas fa-user-tie" : "fas fa-user";
        const iconHtml = `<i class="${iconClass} fancytree-icon"></i>`;
        const pos = payload.positionName ? ` (${payload.positionName})` : "";

        $span
          .find(".fancytree-title")
          .html(`${iconHtml} ${payload.empNm}${pos}`);
      } else {
        // 부서
        $span
          .find(".fancytree-title")
          .prepend(`<i class="fas fa-building fancytree-icon"></i> `);
      }
    },

    activate: function (event, data) {
      const node = data.node;
      const payload = node.data && node.data.data ? node.data.data : null;

      if (payload && payload.empNm) showEmployeeDetail(payload);
      else if (payload) showDepartmentDetail(payload);
    },
  });

  // 검색
  $("#search-btn").on("click", function () {
    const keyword = $("#employee-search").val().trim();
    if (!keyword) {
      Swal.fire({ icon: "warning", title: "검색어 누락", text: "검색어를 입력해주세요." });
      return;
    }

    $.ajax({
      url: `${sideContextPath}/organization/popup/search`,
      type: "GET",
      data: { keyword },
      success: function (data) {
        renderTree(data);
      },
      error: function (xhr) {
        if (xhr.status === 401) location.href = `${sideContextPath}/login`;
        else Swal.fire({ icon: "error", title: "검색 실패", text: "오류가 발생했습니다." });
      },
    });
  });

  $("#employee-search").on("keyup", function (e) {
    const keyword = $(this).val().trim();
    if (e.key === "Enter") $("#search-btn").click();

    if (keyword === "") {
      $("#depTree").fancytree("getTree").reload({
        url: `${sideContextPath}/organization/popup/root`,
        cache: false,
      });
    }
  });
}

function renderTree(data) {
  $("#depTree").fancytree("getTree").reload(data);
}

function showEmployeeDetail(employee) {
  const detailHtml = `
    <div class="employee-detail">
      <h3>${employee.empNm || ""} ${employee.positionName || ""}</h3>
      <p><strong>사원번호:</strong> ${employee.empNo || "-"}</p>
      <p><strong>이메일:</strong> ${employee.empEmail || "-"}</p>
      <p><strong>전화번호:</strong> ${employee.empPhone || "-"}</p>
      <p><strong>입사일:</strong> ${employee.empRegdate || "-"}</p>
    </div>
  `;
  $("#detailPopupContent").html(detailHtml);
  $("#detailPopup").show();
}

function showDepartmentDetail(department) {
  const detailHtml = `
    <div class="department-detail">
      <h3>${department.deptName || ""}</h3>
      <p><strong>부서코드:</strong> ${department.deptCd || "-"}</p>
    </div>
  `;
  $("#detailPopupContent").html(detailHtml);
  $("#detailPopup").show();
}

function closeDetailPopup() {
  $("#detailPopup").hide();
  $("#detailPopupContent").empty();
}
