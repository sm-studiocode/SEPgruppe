document.addEventListener("DOMContentLoaded", () => {
  const id = document.getElementById("contactId");
  const name = document.getElementById("contactNm");
  const email = document.getElementById("contactEmail");
  const checkBtn = document.getElementById("checkAccountBtn");

  // ✅ contextPath (/sep 같은거)
  const ctx = (window.ctx || "").trim();

  // ✅ CSRF meta에서 읽기
  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute("content");
  const csrfHeader = document
    .querySelector('meta[name="_csrf_header"]')
    ?.getAttribute("content");

  checkBtn.addEventListener("click", () => {
    const payload = {
      contactId: id.value.trim(),
      contactNm: name.value.trim(),
      contactEmail: email.value.trim()
    };

    if (!payload.contactId || !payload.contactNm || !payload.contactEmail) {
      alert("아이디/이름/이메일을 모두 입력하세요.");
      return;
    }

    const headers = { "Content-Type": "application/json" };
    // ✅ CSRF 헤더 추가 (정석)
    if (csrfHeader && csrfToken) headers[csrfHeader] = csrfToken;

    fetch(ctx + "/login/findPw", {
      method: "POST",
      headers,
      credentials: "same-origin", // ✅ 세션 쿠키 보장
      body: JSON.stringify(payload)
    })
      .then(async (res) => {
        // 403/500일 때도 원인을 콘솔에 보이게
        if (!res.ok) {
          const text = await res.text();
          console.error("findPw failed:", res.status, text);
          throw new Error("요청 실패(" + res.status + ")");
        }
        return res.json();
      })
      .then(data => {
        if (data.success) {
          alert("임시 비밀번호를 이메일로 발송했습니다. 메일 확인 후 로그인하세요.");
          location.href = ctx + "/login";
        } else {
          alert(data.message || "일치하는 계정이 없습니다.");
        }
      })
      .catch((e) => {
        console.error(e);
        alert("요청 처리 중 오류가 발생했습니다.");
      });
  });
});
