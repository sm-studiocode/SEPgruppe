document.addEventListener("DOMContentLoaded", () => {
  const id = document.getElementById("contactId");
  const name = document.getElementById("contactNm");
  const email = document.getElementById("contactEmail");
  const checkBtn = document.getElementById("checkAccountBtn");

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

    fetch("/sep/login/findPw", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    })
    .then(res => res.json())
    .then(data => {
      if (data.success) {
        alert("임시 비밀번호를 이메일로 발송했습니다. 메일 확인 후 로그인하세요.");
        location.href = "/sep/login";
      } else {
        alert(data.message || "일치하는 계정이 없습니다.");
      }
    })
    .catch(() => alert("요청 처리 중 오류가 발생했습니다."));
  });
});
