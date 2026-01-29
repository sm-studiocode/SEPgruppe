document.addEventListener('DOMContentLoaded', function () {

    /* =========================
     * 0. (추가) 분할 입력 복원
     * ========================= */
    function restoreSplitInputs(hiddenId, inputSelector, delimiter) {
        const hidden = document.getElementById(hiddenId);
        const inputs = document.querySelectorAll(inputSelector);

        if (!hidden || !inputs.length) return;

        const v = (hidden.value || "").trim();
        if (!v) return;

        const parts = v.split(delimiter);
        parts.forEach((p, i) => {
            if (inputs[i]) inputs[i].value = p;
        });
    }

    /* =========================
     * 1. 로그인 / 회원가입 탭 전환
     * ========================= */
    const tabs = document.querySelectorAll('.tabs .tab');
    const loginFields = document.getElementById('loginFields');
    const joinFields = document.getElementById('joinFields');

    tabs.forEach(tab => {
        tab.addEventListener('click', function () {
            tabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');

            const targetId = this.dataset.target;
            loginFields.style.display = targetId === 'loginFields' ? 'block' : 'none';
            joinFields.style.display = targetId === 'joinFields' ? 'block' : 'none';
        });
    });

    /* =========================
     * 2. 테스트 계정 자동 로그인
     * ========================= */
    const testSelect = document.getElementById("testAccountSelect");
    if (testSelect) {
        testSelect.addEventListener("change", function () {
            if (!this.value) return;

            const [userId, userPw] = this.value.split("|");
            document.getElementById("userId").value = userId;
            document.getElementById("userPw").value = userPw;

            setTimeout(() => {
                document.getElementById("loginForm").submit();
            }, 50);
        });
    }

    /* =========================
     * 3. 연락처 / 사업자번호 공통 처리
     * ========================= */

    // ✅ (추가) 검증 실패 후 다시 렌더링된 hidden 값을 3칸 input으로 복원
    // - 연락처: contactPhone hidden -> .phone 3칸
    // - 사업자번호: businessRegNo hidden -> .bizno 3칸
    restoreSplitInputs("contactPhone", ".phone", "-");
    restoreSplitInputs("businessRegNo", ".bizno", "-");

    // 기존 로직 그대로
    bindMultiNumberInput(".phone", "contactPhone", "-");
    bindMultiNumberInput(".bizno", "businessRegNo", "-");

    /* =========================
     * 4. 회원가입 이메일 인증 (손대지 말라고 해서 그대로)
     * ========================= */
    const btnSendMail = document.getElementById("btnSendMail");
    const btnVerifyMail = document.getElementById("btnVerifyMail");
    const joinEmail = document.getElementById("joinEmail");   // JSP id="joinEmail"
    const mailCode = document.getElementById("mailCode");
    const mailAuthResult = document.getElementById("mailAuthResult");
    const mailVerified = document.getElementById("mailVerified");     // JSP hidden
    const verifiedEmail = document.getElementById("verifiedEmail");   // JSP hidden
    const joinForm = document.getElementById("joinFormMail"); // JSP form:form id="joinFormMail"

    const ctx = (window.ctx || "").trim();

    function getCsrfToken() {
        const input = document.querySelector('#joinFormMail input[name="_csrf"]');
        return input ? input.value : null;
    }

    function setResult(msg) {
        if (mailAuthResult) mailAuthResult.textContent = msg || "";
    }

    function resetMailVerification(msg) {
        if (mailVerified) mailVerified.value = "false";
        if (verifiedEmail) verifiedEmail.value = "";
        if (msg) setResult(msg);
    }

    if (joinEmail) {
        joinEmail.addEventListener("input", () => {
            resetMailVerification("이메일이 변경되었습니다. 인증을 다시 진행해주세요.");
        });
    }

    if (btnSendMail) {
        btnSendMail.addEventListener("click", async () => {
            const email = (joinEmail?.value || "").trim();
            if (!email) {
                setResult("이메일을 입력하세요.");
                return;
            }

            try {
                const form = new URLSearchParams();
                form.append("email", email);

                const csrf = getCsrfToken();
                if (csrf) form.append("_csrf", csrf);

                const res = await fetch(ctx + "/login/join/mail/send", {
                    method: "POST",
                    credentials: "same-origin",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
                    },
                    body: form.toString()
                });

                if (res.ok) {
                    setResult("인증번호를 발송했습니다. 메일함을 확인하세요.");
                    if (mailVerified) mailVerified.value = "false";
                    if (verifiedEmail) verifiedEmail.value = email;
                } else {
                    const text = await res.text();
                    console.error(text);
                    setResult("메일 발송 실패(403/경로/CSRF 확인)");
                }
            } catch (e) {
                console.error(e);
                setResult("메일 발송 실패(네트워크/콘솔 확인)");
            }
        });
    }

    if (btnVerifyMail) {
        btnVerifyMail.addEventListener("click", async () => {
            const email = (joinEmail?.value || "").trim();
            const code = (mailCode?.value || "").trim();

            if (!email) {
                setResult("이메일을 입력하세요.");
                return;
            }
            if (!code) {
                setResult("인증번호를 입력하세요.");
                return;
            }

            try {
                const form = new URLSearchParams();
                form.append("email", email);
                form.append("code", code);

                const csrf = getCsrfToken();
                if (csrf) form.append("_csrf", csrf);

                const res = await fetch(ctx + "/login/join/mail/verify", {
                    method: "POST",
                    credentials: "same-origin",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
                    },
                    body: form.toString()
                });

                if (!res.ok) {
                    const text = await res.text();
                    console.error(text);
                    setResult("인증 확인 실패(403/CSRF 확인)");
                    return;
                }

                const data = await res.json();
                if (data && data.success) {
                    setResult("인증 완료");
                    if (mailVerified) mailVerified.value = "true";
                    if (verifiedEmail) verifiedEmail.value = email;
                } else {
                    setResult("인증번호가 틀렸습니다. (또는 이메일이 변경되었습니다)");
                    if (mailVerified) mailVerified.value = "false";
                    if (verifiedEmail) verifiedEmail.value = "";
                }
            } catch (e) {
                console.error(e);
                setResult("인증 확인 실패(서버 응답/콘솔 확인)");
            }
        });
    }

    if (joinForm) {
        joinForm.addEventListener("submit", (e) => {
            const email = (joinEmail?.value || "").trim();
            const vEmail = (verifiedEmail?.value || "").trim();

            if (!mailVerified || mailVerified.value !== "true") {
                e.preventDefault();
                setResult("이메일 인증을 완료해야 회원가입이 가능합니다.");
                return;
            }

            if (!vEmail || vEmail !== email) {
                e.preventDefault();
                resetMailVerification("인증한 이메일과 입력한 이메일이 다릅니다. 다시 인증해주세요.");
                return;
            }
        });
    }
});


/**
 * 숫자 분할 입력 공통 처리
 */
function bindMultiNumberInput(inputSelector, hiddenId, delimiter) {
    const inputs = document.querySelectorAll(inputSelector);
    const hidden = document.getElementById(hiddenId);

    if (!inputs.length || !hidden) return;

    inputs.forEach((input, idx) => {
        input.addEventListener("input", e => {
            e.target.value = e.target.value.replace(/\D/g, "");

            if (e.target.value.length === e.target.maxLength && inputs[idx + 1]) {
                inputs[idx + 1].focus();
            }

            const values = [...inputs].map(i => i.value);
            if (values.every(v => v.length > 0)) {
                hidden.value = values.join(delimiter);
            }
        });
    });
}


/* =========================
 * Daum 주소 API (전역)
 * ========================= */
function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function (data) {
            document.getElementById("companyZip").value = data.zonecode;
            document.getElementById("companyAdd1").value =
                data.roadAddress || data.jibunAddress;
            document.querySelector("input[name='companyAdd2']").focus();
        }
    }).open();
}
