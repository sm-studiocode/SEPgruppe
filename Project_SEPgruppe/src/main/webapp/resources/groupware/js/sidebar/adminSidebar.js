

// =========================
// 0) 설정
// =========================
const MAX_MENUS = 5;

// 회사/권한 섞이는 거 방지하고 싶으면 아래처럼 키를 바꿔라.
// (body나 sidebar에 data-company-no 심어두면 됨)
// 예: <body data-company-no="${companyNo}">
const companyNo = document.body?.dataset?.companyNo || ''; 
const RECENT_MENU_KEY = companyNo ? `recentMenus_${companyNo}_ADMIN` : 'recentMenus';

// =========================
// 1) 최근 사용 메뉴
// =========================
function addRecentMenu(menuText, menuUrl) {
  let menus = JSON.parse(localStorage.getItem(RECENT_MENU_KEY)) || [];

  // 중복 제거
  menus = menus.filter(menu => menu.url !== menuUrl);

  // 맨 앞에 추가
  menus.unshift({ text: menuText, url: menuUrl });

  // 최대 개수 초과 시 제거
  if (menus.length > MAX_MENUS) {
    menus = menus.slice(0, MAX_MENUS);
  }

  localStorage.setItem(RECENT_MENU_KEY, JSON.stringify(menus));
  renderRecentMenus();
}

function renderRecentMenus() {
  const menus = JSON.parse(localStorage.getItem(RECENT_MENU_KEY)) || [];
  const list = document.getElementById('recent-menu-list');
  if (!list) return;

  list.innerHTML = '';

  menus.forEach(menu => {
    const li = document.createElement('li');
    const a = document.createElement('a');
    a.href = menu.url;
    a.textContent = menu.text;
    li.appendChild(a);
    list.appendChild(li);
  });
}

function bindRecentMenuTracking() {
  // href="#" 제외 + javascript: 같은 것도 제외(방어)
  const anchors = document.querySelectorAll('.nav a[href]:not([href="#"])');
  anchors.forEach(anchor => {
    anchor.addEventListener('click', function () {
      const hrefAttr = (this.getAttribute('href') || '').trim();

      // javascript:, mailto:, tel: 등은 최근 메뉴로 저장하지 않게 방어
      const lower = hrefAttr.toLowerCase();
      if (lower.startsWith('javascript:') || lower.startsWith('mailto:') || lower.startsWith('tel:')) {
        return;
      }

      const menuText = this.textContent.trim();
      const menuUrl = this.href; // 절대 URL로 저장됨(동작 OK)
      addRecentMenu(menuText, menuUrl);
    });
  });
}

// =========================
// 2) 현재 페이지면 collapse show + 상위 nav-item active 유지
//    (sidebar.js 기능 통합 + 비교 방식 안정화)
// =========================
function keepActiveMenuOpen() {
  const currentPath = window.location.pathname; // 예: /sep/testnum001/approval/admin/docFormList
  const dropdownMenus = document.querySelectorAll(".collapse");

  dropdownMenus.forEach(menu => {
    const menuLinks = menu.querySelectorAll("a[href]");

    menuLinks.forEach(link => {
      // ✅ getAttribute 비교 대신 pathname 비교가 더 안정적
      // link.pathname: 브라우저가 정규화한 path
      if (link.pathname === currentPath) {
        menu.classList.add("show");

        const parentNavItem = menu.closest(".nav-item");
        if (parentNavItem) parentNavItem.classList.add("active");
      }
    });
  });
}

// =========================
// 3) 화살표 토글 (JSP inline script 기능 통합)
// =========================
function bindArrowToggle() {
  document.querySelectorAll('.toggle-arrow').forEach(item => {
    item.addEventListener('click', function() {
      const arrow = this.querySelector('.arrow');
      if (!arrow) return;

      if (arrow.classList.contains('fa-chevron-right')) {
        arrow.classList.remove('fa-chevron-right');
        arrow.classList.add('fa-chevron-down');
      } else {
        arrow.classList.remove('fa-chevron-down');
        arrow.classList.add('fa-chevron-right');
      }
    });
  });
}

// =========================
// 4) 초기화
// =========================
document.addEventListener('DOMContentLoaded', function () {
  renderRecentMenus();
  bindRecentMenuTracking();
  keepActiveMenuOpen();
  bindArrowToggle();
});
