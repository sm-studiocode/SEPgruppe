/**
 * alarmHome.js
 * - API 기반 렌더링
 */

console.log("✅ alarmHome.js loaded");

$(document).ready(function () {
  const $cfg = $("#alarmConfig");
  const contextPath = $cfg.data("context-path") || "";
  const userId = $cfg.data("user-id") || "";

  console.log("✅ contextPath:", contextPath);
  console.log("✅ userId:", userId);

  let currentFilter = "all";
  let isLoading = false;

  function escapeHtml(str) {
    return String(str ?? "")
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#039;");
  }

  function formatDate(dateStr) {
    if (!dateStr) return "-";
    const d = new Date(dateStr);
    if (isNaN(d.getTime())) return escapeHtml(dateStr);

    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, "0");
    const dd = String(d.getDate()).padStart(2, "0");
    const hh = String(d.getHours()).padStart(2, "0");
    const mi = String(d.getMinutes()).padStart(2, "0");
    const ss = String(d.getSeconds()).padStart(2, "0");
    return `${yyyy}-${mm}-${dd} ${hh}:${mi}:${ss}`;
  }

  function isUnread(alarm) {
    const v = alarm.isAlarmRead;
    return v === null || v === "" || v === "N";
  }

  function loadUnreadBadge() {
    $.ajax({
      url: contextPath + "/alarm/unreadCount",
      type: "GET",
      success: function (cnt) {
        const n = Number(cnt || 0);
        $("#unreadBadge").text(n > 0 ? `(${n})` : "");
      },
      error: function (xhr) {
        console.error("❌ unreadCount fail:", xhr.status, xhr.responseText);
      }
    });
  }

  function loadAlarms() {
    if (isLoading) return;
    isLoading = true;

    const onlyUnread = currentFilter === "unread" ? "Y" : "N";

    $.ajax({
      url: contextPath + "/alarm/list",
      type: "GET",
      data: { onlyUnread, offset: 0, limit: 200 },
      success: function (list) {
        renderTable(Array.isArray(list) ? list : []);
        loadUnreadBadge();
      },
      error: function (xhr) {
        console.error("❌ alarm list fail:", xhr.status, xhr.responseText);
        renderTable([]);
      },
      complete: function () {
        isLoading = false;
      }
    });
  }

  function markRead(alarmNo) {
    return $.ajax({
      url: contextPath + "/alarm/read/" + alarmNo,
      type: "PUT"
    });
  }

  function markReadAll() {
    return $.ajax({
      url: contextPath + "/alarm/readAll",
      type: "PUT"
    });
  }

  function renderTable(list) {
    const $tbody = $("#alarmTbody");
    $tbody.empty();

    if (!list || list.length === 0) {
      $("#alarmTable").hide();
      $("#noAlarm").show();
      return;
    }

    $("#noAlarm").hide();
    $("#alarmTable").show();

    list.forEach((alarm) => {
      const alarmNo = alarm.alarmNo;
      const categoryNo = alarm.alarmCategoryNo;
      const title = alarm.alarmNm || "(제목 없음)";
      const content = alarm.alarmContent || "";
      const date = formatDate(alarm.alarmDate);
      const unread = isUnread(alarm);

      const categoryText =
        alarm.alarmCategoryNm || (categoryNo != null ? `#${categoryNo}` : "-");

      const trClass = unread ? "unread" : "";
      const rowHtml = `
        <tr class="alarm-row ${trClass}" data-alarm-no="${escapeHtml(alarmNo)}" data-category-no="${escapeHtml(categoryNo)}">
          <td><input type="checkbox" class="alarm-check" /></td>
          <td>${escapeHtml(categoryText)}</td>
          <td>
            <span class="alarm-link" style="cursor:pointer; color:#2980b9; text-decoration:underline;">
              ${escapeHtml(title)}
            </span>
            ${content ? `<div class="text-muted" style="font-size:12px; margin-top:4px;">${escapeHtml(content)}</div>` : ""}
          </td>
          <td>${escapeHtml(date)}</td>
        </tr>
      `;
      $tbody.append(rowHtml);
    });
  }

  $(document).on("click", ".menu-item", function (e) {
    e.preventDefault();
    const filter = $(this).data("filter");
    currentFilter = filter;
    loadAlarms();
  });

  $(document).on("click", ".alarm-link", function () {
    const $tr = $(this).closest("tr");
    const alarmNo = $tr.data("alarm-no");
    if (!alarmNo) return;

    markRead(alarmNo).always(function () {
      $tr.removeClass("unread");
      loadUnreadBadge();
    });
  });

  $("#btnReadAll").on("click", function () {
    markReadAll()
      .done(function () {
        $(".alarm-row").removeClass("unread");
        loadUnreadBadge();
      })
      .fail(function (xhr) {
        console.error("❌ readAll fail:", xhr.status, xhr.responseText);
      });
  });

  loadUnreadBadge();
  loadAlarms();
});
