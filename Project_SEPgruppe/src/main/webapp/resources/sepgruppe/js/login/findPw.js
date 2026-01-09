document.addEventListener("DOMContentLoaded", () => {

    const el = {
        id: document.getElementById("contactId"),
        name: document.getElementById("contactNm"),
        email: document.getElementById("contactEmail"),
        authCode: document.getElementById("authCode"),
        sendBtn: document.getElementById("sendAuthCode"),
        verifyBtn: document.getElementById("verifyAuthCode"),
        checkBtn: document.getElementById("checkAccountBtn"),
        resetBtn: document.getElementById("resetPwBtn"),
        newPw: document.getElementById("newPw"),
        confirmPw: document.getElementById("confirmPw"),
        authResult: document.getElementById("authResult"),
        pwError: document.getElementById("pwMismatchError"),
        verifySection: document.getElementById("verifySection"),
        resetSection: document.getElementById("resetSection"),
    };

    let isAuthVerified = false;

    /* ================= 이메일 인증 ================= */

    el.sendBtn.addEventListener("click", () => {
        if (!el.email.value.trim()) {
            alert("이메일을 입력하세요.");
            return;
        }

        fetch("/sep/company/mailAuth", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({ mail: el.email.value.trim() }),
        })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                alert("📩 인증번호가 발송되었습니다.");
            } else {
                alert("인증번호 발송 실패");
            }
        });
    });

    el.verifyBtn.addEventListener("click", () => {
        fetch(`/sep/company/mailCheck?userNumber=${el.authCode.value}`)
        .then(res => res.json())
        .then(isMatch => {
            isAuthVerified = isMatch;
            el.authResult.textContent = isMatch ? "✅ 인증 성공" : "❌ 인증 실패";
            el.checkBtn.disabled = !isMatch;
        });
    });

    /* ================= 계정 확인 ================= */

    el.checkBtn.addEventListener("click", () => {
        fetch("/sep/login/findPw", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                contactId: el.id.value,
                contactNm: el.name.value,
                contactEmail: el.email.value
            })
        })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                el.verifySection.style.display = "none";
                el.resetSection.style.display = "block";
            } else {
                alert(data.message);
            }
        });
    });

    /* ================= 비밀번호 검증 ================= */

    function validatePw() {
        if (el.newPw.value !== el.confirmPw.value) {
            el.pwError.textContent = "비밀번호가 일치하지 않습니다.";
            el.resetBtn.disabled = true;
        } else {
            el.pwError.textContent = "";
            el.resetBtn.disabled = false;
        }
    }

    el.newPw.addEventListener("input", validatePw);
    el.confirmPw.addEventListener("input", validatePw);

    /* ================= 비밀번호 변경 ================= */

    el.resetBtn.addEventListener("click", () => {
        fetch("/sep/login/updatePw", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                contactId: el.id.value,
                contactPw: el.newPw.value
            })
        })
        .then(() => {
            alert("✅ 비밀번호가 변경되었습니다.");
            location.href = "/sep/login";
        });
    });

});
