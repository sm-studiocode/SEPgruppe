/** 
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 3. 21.     	JSW            최초 생성
 *
 * </pre>
 */
const popup = document.getElementById('organizationPopup');
const sideCompanyNo = popup.dataset.companyNo;
const sideContextPath = popup.dataset.contextPath;

function showOrganizationPopup() {
  popup.style.display = 'block';

  // 팝업이 처음 열릴 때만 FancyTree 초기화
  if (!$.ui.fancytree.getTree("#depTree")) {
    initFancyTree(); // 아래 함수 호출
  }
}

function closeOrganizationPopup() {
  popup.style.display = 'none';
}

// 팝업 바깥 클릭 시 닫기
window.addEventListener("click", (e) => {
  if (e.target === popup) closeOrganizationPopup();
});

function initFancyTree() {
  $("#depTree").fancytree({
    source: {
      url: `${sideContextPath}/${sideCompanyNo}/organization/admin/parentDep`,
      cache: false
    },
    lazyLoad: function (event, data) {
      const node = data.node;
      let mode = node.data.parentDeptCd ? "employee" : "department";

      data.result = {
        url: `${sideContextPath}/${sideCompanyNo}/organization/admin/childeDep`,
        data: { mode, parent: node.key },
        cache: false
      };
    },
    renderNode: function (event, data) {
      const node = data.node;
      const $span = $(node.span);

      $span.find(".fancytree-icon").remove(); // 아이콘 중복 방지
      if (node.data.empNm) {
		const isManager = node.parent && node.parent.data.managerEmpId === node.data.empId;

       	const iconClass = isManager ? "fas fa-user-tie" : "fas fa-user";
       	const iconHtml = `<i class="${iconClass} fancytree-icon"></i>`;

       	$span.find(".fancytree-title").html(
           `${iconHtml} ${node.data.empNm} (${node.data.positionName})`
       	);
      } else {
        $span.find(".fancytree-title").prepend(
          `<i class="fas fa-building fancytree-icon"></i> `
        );
      }
    },
    activate: function (event, data) {
      const node = data.node;
      if (node.data.empNm) {
        showEmployeeDetail(node.data);
      } else {
        showDepartmentDetail(node.data);
      }
    }
  });

  // 검색
  $("#search-btn").on("click", function () {
    const keyword = $("#employee-search").val().trim();
    if (!keyword) {
      Swal.fire({
        icon: 'warning',
        title: '검색어 누락',
        text: '검색어를 입력해주세요.'
      });
      return;
    }

    $.ajax({
      url: `${sideContextPath}/${sideCompanyNo}/organization/admin/search`,
      type: "GET",
      data: { keyword },
      success: function (data) {
        renderTree(data);
      },
      error: function () {
        Swal.fire({
          icon: 'error',
          title: '검색 실패',
          text: '오류가 발생했습니다.'
        });
      }
    });
  });

  $("#employee-search").on("keyup", function (e) {
      const keyword = $(this).val().trim();

      if (e.key === "Enter") {
          $("#search-btn").click();
      }

      // ✅ 검색어 지워졌을 때 전체 트리 다시 로드
      if (keyword === "") {
          // 전체 트리 다시 불러오기
          $("#depTree").fancytree("getTree").reload({
              url: `${sideContextPath}/${sideCompanyNo}/organization/admin/parentDep`,
              cache: false
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
            <h3>${employee.empNm} ${employee.positionName}</h3>
            <p><strong>사원번호:</strong> ${employee.empNo}</p>
            <p><strong>이메일:</strong> ${employee.empEmail || '-'}</p>
            <p><strong>전화번호:</strong> ${employee.empPhone || '-'}</p>
            <p><strong>입사일:</strong> ${employee.empRegdate || '-'}</p>
        </div>
    `;
    $("#detailPopupContent").html(detailHtml);
    $("#detailPopup").show(); // 팝업 띄우기
}

function showDepartmentDetail(department) {
    const detailHtml = `
        <div class="department-detail">
            <h3>${department.deptName}</h3>
            <p><strong>부서코드:</strong> ${department.deptCd}</p>
        </div>
    `;
    $("#detailPopupContent").html(detailHtml);
    $("#detailPopup").show(); // 팝업 띄우기
}

function closeDetailPopup() {
    $("#detailPopup").hide();
    $("#detailPopupContent").empty();
};
