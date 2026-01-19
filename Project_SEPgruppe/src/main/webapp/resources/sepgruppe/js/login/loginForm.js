document.addEventListener('DOMContentLoaded', function () {

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
    bindMultiNumberInput(".phone", "contactPhone", "-");
    bindMultiNumberInput(".bizno", "businessRegNo", "-");

    /* =========================
     * 4. 회원가입 이메일 인증
     * ========================= */
    const btnSendMail = document.getElementById("btnSendMail");
    const btnVerifyMail = document.getElementById("btnVerifyMail");
    const joinEmail = document.getElementById("joinEmail");   // JSP에서 id="joinEmail" 필수
    const mailCode = document.getElementById("mailCode");
    const mailAuthResult = document.getElementById("mailAuthResult");
    const mailVerified = document.getElementById("mailVerified");
    const joinForm = document.getElementById("joinFormMail");     // JSP form:form에 id="joinForm" 필수

    // ✅ ctx는 body data-ctx 없으면, 그냥 contextPath를 JSP에서 전역변수로 박는 방식이 제일 안전함
    // JSP에서 <script>window.ctx='${pageContext.request.contextPath}';</script> 추가 추천
    const ctx = (window.ctx || "").trim();

    if (btnSendMail) {
        btnSendMail.addEventListener("click", async () => {
            const email = (joinEmail?.value || "").trim();
            if (!email) {
                if (mailAuthResult) mailAuthResult.textContent = "이메일을 입력하세요.";
                return;
            }

            try {
                const form = new URLSearchParams();
                form.append("email", email);

                const res = await fetch(ctx + "/login/join/mail/send", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                    body: form.toString()
                });

                if (res.ok) {
                    if (mailAuthResult) mailAuthResult.textContent = "인증번호를 발송했습니다. 메일함을 확인하세요.";
                    if (mailVerified) mailVerified.value = "false";
                } else {
                    if (mailAuthResult) mailAuthResult.textContent = "메일 발송 실패(서버 오류/경로 확인)";
                }
            } catch (e) {
                if (mailAuthResult) mailAuthResult.textContent = "메일 발송 실패(네트워크/콘솔 확인)";
                console.error(e);
            }
        });
    }

    if (btnVerifyMail) {
        btnVerifyMail.addEventListener("click", async () => {
            const email = (joinEmail?.value || "").trim();
            const code = (mailCode?.value || "").trim();

            if (!email) {
                if (mailAuthResult) mailAuthResult.textContent = "이메일을 입력하세요.";
                return;
            }
            if (!code) {
                if (mailAuthResult) mailAuthResult.textContent = "인증번호를 입력하세요.";
                return;
            }

            try {
                const form = new URLSearchParams();
                form.append("email", email);
                form.append("code", code);

                const res = await fetch(ctx + "/login/join/mail/verify", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                    body: form.toString()
                });

                // 서버가 JSON으로 내려줘야 함: {success:true/false}
                const data = await res.json();

                if (data && data.success) {
                    if (mailAuthResult) mailAuthResult.textContent = "인증 완료";
                    if (mailVerified) mailVerified.value = "true";
                } else {
                    if (mailAuthResult) mailAuthResult.textContent = "인증번호가 틀렸습니다.";
                    if (mailVerified) mailVerified.value = "false";
                }
            } catch (e) {
                if (mailAuthResult) mailAuthResult.textContent = "인증 확인 실패(서버 응답/콘솔 확인)";
                console.error(e);
            }
        });
    }

    if (joinForm) {
        joinForm.addEventListener("submit", (e) => {
            if (!mailVerified || mailVerified.value !== "true") {
                e.preventDefault();
                if (mailAuthResult) mailAuthResult.textContent = "이메일 인증을 완료해야 회원가입이 가능합니다.";
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
