let sepMessenger = null;

function openMessengerWindow() {
    var width = 570;
    var height = 670;
    var left = (screen.width / 2) - (width / 2);
    var top = (screen.height / 2) - (height / 2);

    sepMessenger = window.open(
        "/sep/testnum001/chat",
        "MessengerWindow",
        `width=${width},height=${height},top=${top},left=${left},` +
        `toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no`
    );
}

function logoutTidio() {
    document.cookie = "_tidioChatVisitorId=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT;";
    document.cookie = "_tidioChatSid=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT;";

    Object.keys(localStorage).forEach(key => {
      if (key.startsWith('tidio_state_')) localStorage.removeItem(key);
    });
    Object.keys(sessionStorage).forEach(key => {
      if (key.startsWith('tidio_state_')) sessionStorage.removeItem(key);
    });

    window.location.href = "/sep/login/logout";
}

function toggleWidgetPanel(tabId) {
  const panel = document.getElementById("widget-edit-panel");
  if (panel.style.display === "none") {
    fetch(`${pageContext.request.contextPath}/dashboard/widgetSelect?tabId=` + tabId)
      .then(res => res.text())
      .then(html => {
        panel.innerHTML = html;
        panel.style.display = "block";
      });
  } else {
    panel.style.display = "none";
    panel.innerHTML = "";
  }
}

function openAddTabModal() {
  alert("탭 추가 모달 열기 구현 필요");
}