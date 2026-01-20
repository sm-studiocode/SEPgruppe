document.addEventListener("DOMContentLoaded", () => {
  const btn = document.getElementById("findIdBtn");
  const result = document.getElementById("resultMessage");

  const ctx = (window.ctx || "").trim(); // /sep 같은 contextPath

  function getCsrf() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute("content");
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute("content");
    return { token, header };
  }

  async function handleFindId() {
    const name = document.getElementById("contactNm").value.trim();
    const email = document.getElementById("contactEmail").value.trim();

    result.textContent = "";
    result.style.color = "";

    if (!name || !email) {
      result.textContent = "이름과 이메일을 입력해주세요.";
      result.style.color = "#ff6b6b";
      return;
    }

    const { token, header } = getCsrf();

    try {
      const response = await fetch(ctx + "/login/findId", {
        method: "POST",
        credentials: "same-origin", // ✅ 세션쿠키 포함
        headers: {
          "Content-Type": "application/json",
          ...(token && header ? { [header]: token } : {}) // ✅ CSRF 헤더로 전송
        },
        body: JSON.stringify({
          contactNm: name,
          contactEmail: email
        })
      });

      // 서버가 에러를 HTML로 내릴 수도 있어서 안전하게 처리
      const contentType = response.headers.get("content-type") || "";
      const data = contentType.includes("application/json")
        ? await response.json()
        : { success: false, message: await response.text() };

      if (!response.ok) {
        throw new Error(data.message || "요청 실패");
      }

      if (data.success) {
        result.innerHTML = `
          ✅ <strong>아이디:</strong> ${data.contactId}<br><br>
          <a href="${ctx}/login" class="btn btn-outline-light btn-sm">
            로그인하러 가기
          </a>
        `;
        result.style.color = "#6ff1cb";
      } else {
        result.textContent = "일치하는 계정이 없습니다.";
        result.style.color = "#ff6b6b";
      }
    } catch (err) {
      console.error(err);
      result.textContent = `❌ ${err.message}`;
      result.style.color = "#ff6b6b";
    }
  }

  if (btn) btn.addEventListener("click", handleFindId);
});
