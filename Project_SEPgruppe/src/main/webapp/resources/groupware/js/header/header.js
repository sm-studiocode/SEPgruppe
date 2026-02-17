/* =========================
 * ✅ 알람(헤더) 기능 - Offcanvas 버전 (EMPLOYEE/COMPANY 공통)
 * ========================= */
(function () {
  // ---------- utils ----------
  function escapeHtml(str) {
    return String(str ?? "")
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#039;");
  }

  function formatDate(dateStr) {
    if (!dateStr) return "";
    const d = new Date(dateStr);
    if (isNaN(d.getTime())) return escapeHtml(dateStr);

    const mm = String(d.getMonth() + 1).padStart(2, "0");
    const dd = String(d.getDate()).padStart(2, "0");
    const hh = String(d.getHours()).padStart(2, "0");
    const mi = String(d.getMinutes()).padStart(2, "0");
    return `${mm}-${dd} ${hh}:${mi}`;
  }

  function getCfg() {
    const el = document.getElementById("alarmConfig");
    if (!el) return null;
    return {
      contextPath: el.dataset.contextPath || "",
      userId: el.dataset.userId || ""
    };
  }

  async function fetchJson(url, opt = {}) {
    const res = await fetch(url, {
      ...opt,
      headers: { Accept: "application/json", ...(opt.headers || {}) }
    });
    if (!res.ok) throw new Error(res.status + " " + (await res.text()));
    // 204 같은 경우 대비
    const ct = res.headers.get("content-type") || "";
    if (!ct.includes("application/json")) return null;
    return await res.json();
  }

  // ---------- dom getters ----------
  function $id(id) {
    return document.getElementById(id);
  }

  // ---------- badge ----------
  function setHeaderBadge(cnt) {
    const badge = $id("notifBadge");
    if (!badge) return;

    const n = Number(cnt || 0);
    if (n > 0) {
      badge.style.display = "inline-block";
      badge.textContent = n;
    } else {
      badge.style.display = "none";
      badge.textContent = "";
    }
  }

  function setOffBadge(cnt) {
    const badge = $id("alarmOffBadge");
    if (!badge) return;

    const n = Number(cnt || 0);
    if (n > 0) {
      badge.style.display = "inline-block";
      badge.textContent = n;
    } else {
      badge.style.display = "none";
      badge.textContent = "";
    }
  }

  // ---------- state ----------
  const state = {
    filter: "all",     // all | unread
    offset: 0,
    limit: 10,
    loading: false,
    hasMore: true
  };

  // ---------- render ----------
  function renderEmpty(show) {
    const empty = $id("alarmPanelEmpty");
    const list = $id("alarmPanelList");
    if (!empty || !list) return;

    empty.style.display = show ? "block" : "none";
    if (show) list.innerHTML = "";
  }

  function appendCards(list) {
    const box = $id("alarmPanelList");
    if (!box) return;

    const cfg = getCfg();
    const base = cfg?.contextPath || "";

    list.forEach((alarm) => {
      const alarmNo = alarm.alarmNo;
      const title = alarm.alarmNm || "알림";
      const content = alarm.alarmContent || "";
      const time = formatDate(alarm.alarmDate);

      const isUnread = (alarm.isAlarmRead == null || alarm.isAlarmRead === "" || alarm.isAlarmRead === "N");

      const card = document.createElement("div");
      card.className = "card shadow-sm";
      card.style.borderRadius = "14px";
      card.style.cursor = "pointer";
      card.dataset.alarmNo = alarmNo;

      // 미읽음 강조
      if (isUnread) {
        card.style.border = "1px solid rgba(220,53,69,.35)";
      }

      card.innerHTML = `
        <div class="card-body py-2">
          <div class="d-flex align-items-start justify-content-between gap-2">
            <div style="min-width:0;">
              <div class="d-flex align-items-center gap-2">
                <span class="badge bg-${isUnread ? "danger" : "secondary"}" style="font-weight:600;">
                  ${isUnread ? "NEW" : "READ"}
                </span>
                <div class="fw-semibold text-truncate" style="max-width:260px;">
                  ${escapeHtml(title)}
                </div>
              </div>
              <div class="text-muted mt-1 text-truncate" style="max-width:320px; font-size:12px;">
                ${escapeHtml(content)}
              </div>
            </div>
            <div class="text-muted" style="font-size:12px; white-space:nowrap;">
              ${escapeHtml(time)}
            </div>
          </div>
        </div>
      `;

      // 클릭: 읽음 처리 → (선택) /alarm 이동
      card.addEventListener("click", async function () {
        try {
          if (alarmNo) {
            await fetch(base + "/alarm/read/" + alarmNo, { method: "PUT" });
          }
        } catch (e) {
          // 읽음 실패해도 UX는 유지
          console.warn("alarm read fail:", e);
        } finally {
          // 갱신
          refreshCounts();
          // 필요하면 전용 페이지로 이동
          window.location.href = base + "/alarm";
        }
      });

      box.appendChild(card);
    });
  }

  // ---------- api ----------
  async function refreshCounts() {
    const cfg = getCfg();
    if (!cfg) return;

    try {
      const cnt = await fetchJson(cfg.contextPath + "/alarm/unreadCount");
      setHeaderBadge(cnt);
      setOffBadge(cnt);
    } catch (e) {
      console.error("alarm unreadCount fail:", e);
    }
  }

  async function loadList(reset = false) {
    const cfg = getCfg();
    if (!cfg) return;

    if (state.loading) return;
    state.loading = true;

    const listBox = $id("alarmPanelList");
    const moreBtn = $id("btnAlarmMore");

    try {
      if (reset) {
        state.offset = 0;
        state.hasMore = true;
        if (listBox) listBox.innerHTML = "";
      }

      const onlyUnread = (state.filter === "unread") ? "Y" : "N";

      const url =
        cfg.contextPath +
        `/alarm/list?onlyUnread=${encodeURIComponent(onlyUnread)}&offset=${state.offset}&limit=${state.limit}`;

      const list = await fetchJson(url);
      const arr = Array.isArray(list) ? list : [];

      // empty 처리
      if (reset && arr.length === 0) {
        renderEmpty(true);
        state.hasMore = false;
        if (moreBtn) moreBtn.disabled = true;
        return;
      }
      renderEmpty(false);

      appendCards(arr);

      // hasMore 판단 (limit보다 적게 오면 끝)
      if (arr.length < state.limit) state.hasMore = false;
      state.offset += arr.length;

      if (moreBtn) moreBtn.disabled = !state.hasMore;

    } catch (e) {
      console.error("alarm list fail:", e);
      if (reset) renderEmpty(true);
      if (moreBtn) moreBtn.disabled = true;
    } finally {
      state.loading = false;
    }
  }

  async function readAll() {
    const cfg = getCfg();
    if (!cfg) return;

    try {
      await fetch(cfg.contextPath + "/alarm/readAll", { method: "PUT" });
    } catch (e) {
      console.error("alarm readAll fail:", e);
      if (window.Swal) Swal.fire("오류", "전체 읽음 처리 실패", "error");
    } finally {
      // 뱃지/리스트 갱신
      await refreshCounts();
      await loadList(true);
    }
  }

  // ---------- offcanvas ----------
  function openOffcanvas() {
    const el = $id("alarmOffcanvas");
    if (!el || !window.bootstrap?.Offcanvas) return;

    const inst = window.bootstrap.Offcanvas.getOrCreateInstance(el);
    inst.show();
  }

  // ---------- events ----------
  function bindEvents() {
    // 종 클릭 → offcanvas 열고 최신화
    const bell = $id("notifDropdown");
    if (bell) {
      bell.addEventListener("click", function (e) {
        e.preventDefault();
        openOffcanvas();
        refreshCounts();
        loadList(true);
      });
    }

    // 탭(전체/미읽음)
    const tabs = $id("alarmTabs");
    if (tabs) {
      tabs.addEventListener("click", function (e) {
        const btn = e.target.closest("button[data-filter]");
        if (!btn) return;

        // active 토글
        tabs.querySelectorAll("button.nav-link").forEach((b) => b.classList.remove("active"));
        btn.classList.add("active");

        // filter 적용
        state.filter = btn.dataset.filter || "all";
        loadList(true);
      });
    }

    // 더 보기
    const moreBtn = $id("btnAlarmMore");
    if (moreBtn) {
      moreBtn.addEventListener("click", function () {
        if (!state.hasMore) return;
        loadList(false);
      });
    }

    // 전체 읽음
    const readAllBtn = $id("btnAlarmReadAll");
    if (readAllBtn) {
      readAllBtn.addEventListener("click", function () {
        readAll();
      });
    }

    // offcanvas 열릴 때도 갱신 (사용자가 다른 방식으로 열었을 때 대비)
    const offEl = $id("alarmOffcanvas");
    if (offEl) {
      offEl.addEventListener("shown.bs.offcanvas", function () {
        refreshCounts();
        loadList(true);
      });
    }
  }

  // ---------- websocket ----------
  function bindWebSocket() {
    const cfg = getCfg();
    if (!cfg || !cfg.userId) return;

    try {
      if (window.SockJS && window.Stomp) {
        const sock = new SockJS(cfg.contextPath + "/stomp");
        const client = Stomp.over(sock);
        client.debug = null;

        client.connect({}, function () {
          client.subscribe("/topic/alarm/" + cfg.userId, function () {
            // 새 알림 → 배지 갱신
            refreshCounts();

            // offcanvas가 열려 있으면 리스트도 갱신
            const offEl = $id("alarmOffcanvas");
            if (offEl && offEl.classList.contains("show")) {
              loadList(true);
            }

            // (선택) 토스트는 다음 단계에서 붙이면 됨
          });
        });
      }
    } catch (e) {
      console.warn("alarm websocket skip:", e);
    }
  }

  // ---------- init ----------
  document.addEventListener("DOMContentLoaded", function () {
    const cfg = getCfg();
    if (!cfg) return;

    // 초기 배지
    refreshCounts();

    // 이벤트 바인딩
    bindEvents();

    // 실시간
    bindWebSocket();
  });
})();
