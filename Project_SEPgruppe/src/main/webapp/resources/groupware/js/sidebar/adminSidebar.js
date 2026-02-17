// =========================
// 0) 설정
// =========================
const MAX_MENUS = 5;

// ✅ sidebar DOM에서 companyNo 읽기 (전역 const companyNo 금지!)
const sidebarEl = document.querySelector(".sidebar[data-company-no]");
const sidebarCompanyNo = sidebarEl?.dataset?.companyNo || "";

// 회사별 최근메뉴 키
const RECENT_MENU_KEY = sidebarCompanyNo
  ? `recentMenus_${sidebarCompanyNo}_ADMIN`
  : "recentMenus_ADMIN";

// =========================
// 1) 최근 사용 메뉴
// =========================
function addRecentMenu(menuText, menuUrl) {
  let menus = JSON.parse(localStorage.getItem(RECENT_MENU_KEY)) || [];

  // 중복 제거
  menus = menus.filter((menu) => menu.url !== menuUrl);

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
  const list = document.getElementById("recent-menu-list");
  if (!list) return;

  list.innerHTML = "";

  menus.forEach((menu) => {
    const li = document.createElement("li");
    const a = document.createElement("a");
    a.href = menu.url;
    a.textContent = menu.text;
    li.appendChild(a);
    list.appendChild(li);
  });
}

function bindRecentMenuTracking() {
  const anchors = document.querySelectorAll('.nav a[href]:not([href="#"])');

  anchors.forEach((anchor) => {
    anchor.addEventListener("click", function () {
      const hrefAttr = (this.getAttribute("href") || "").trim();

      const lower = hrefAttr.toLowerCase();
      if (
        lower.startsWith("javascript:") ||
        lower.startsWith("mailto:") ||
        lower.startsWith("tel:")
      ) {
        return;
      }

      const menuText = this.textContent.trim();
      const menuUrl = this.href;
      addRecentMenu(menuText, menuUrl);
    });
  });
}

// =========================
// 2) 현재 페이지면 collapse show + 상위 nav-item active 유지
// =========================
function keepActiveMenuOpen() {
  const currentPath = window.location.pathname;
  const dropdownMenus = document.querySelectorAll(".collapse");

  dropdownMenus.forEach((menu) => {
    const menuLinks = menu.querySelectorAll("a[href]");
    menuLinks.forEach((link) => {
      if (link.pathname === currentPath) {
        menu.classList.add("show");

        const parentNavItem = menu.closest(".nav-item");
        if (parentNavItem) parentNavItem.classList.add("active");
      }
    });
  });
}

// =========================
// 3) 화살표 토글
// =========================
function bindArrowToggle() {
  document.querySelectorAll(".toggle-arrow").forEach((item) => {
    item.addEventListener("click", function () {
      const arrow = this.querySelector(".arrow");
      if (!arrow) return;

      if (arrow.classList.contains("fa-chevron-right")) {
        arrow.classList.remove("fa-chevron-right");
        arrow.classList.add("fa-chevron-down");
      } else {
        arrow.classList.remove("fa-chevron-down");
        arrow.classList.add("fa-chevron-right");
      }
    });
  });
}

// =========================
// 4) 초기화
// =========================
document.addEventListener("DOMContentLoaded", function () {
  renderRecentMenus();
  bindRecentMenuTracking();
  keepActiveMenuOpen();
  bindArrowToggle();
});
