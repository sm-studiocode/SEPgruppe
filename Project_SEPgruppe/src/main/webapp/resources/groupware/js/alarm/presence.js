/* =========================
 * ✅ presence ping (접속중 표시)
 * - 30초마다 /alarm/presence 호출
 * - 단일 서버 포폴 기준: 접속중이면 push 스킵 용도
 * ========================= */
(function () {
  function getContextPath() {
    // index.jsp: <body data-context-path="...">
    const cp = document.body && document.body.dataset ? document.body.dataset.contextPath : "";
    return cp || "";
  }

  function getCsrf() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute("content") || "";
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute("content") || "";
    return { token, header };
  }

  async function pingPresence() {
    try {
      const contextPath = getContextPath();
      const { token, header } = getCsrf();

      const headers = { "Content-Type": "application/json" };
      // ✅ CSRF 켜져 있으면 반드시 포함
      if (token && header) headers[header] = token;

      await fetch(contextPath + "/alarm/presence", {
        method: "POST",
        headers
      });
    } catch (e) {
      // 조용히 무시 (presence는 실패해도 서비스 영향 X)
    }
  }

  // DOM 준비되면 시작
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", () => {
      pingPresence();
      setInterval(pingPresence, 30000);
    });
  } else {
    pingPresence();
    setInterval(pingPresence, 30000);
  }
})();