document.getElementById("findIdBtn").addEventListener("click", async () => {
    const name = document.getElementById("contactNm").value.trim();
    const email = document.getElementById("contactEmail").value.trim();
    const result = document.getElementById("resultMessage");

    result.textContent = "";
    result.style.color = "";

    if (!name || !email) {
        result.textContent = "이름과 이메일을 입력해주세요.";
        result.style.color = "#ff6b6b";
        return;
    }

    try {
        const response = await fetch("/login/findId", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                contactNm: name,
                contactEmail: email
            })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message);
        }

        result.innerHTML = `
            ✅ <strong>아이디:</strong> ${data.contactId}<br><br>
            <a href="/login" class="btn btn-outline-light btn-sm">
                로그인하러 가기
            </a>
        `;
        result.style.color = "#6ff1cb";

    } catch (err) {
        result.textContent = `❌ ${err.message}`;
        result.style.color = "#ff6b6b";
    }
});
