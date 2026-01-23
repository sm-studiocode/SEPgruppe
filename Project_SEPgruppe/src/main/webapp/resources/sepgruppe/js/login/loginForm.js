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
    const joinForm = document.getElementById("joinFormMail"); // JSP form:form에 id="joinFormMail" 필수

    // ✅ 아이디 input (JSP에서 form:input path="contactId"면 보통 id="contactId")
    // 혹시 null이면 JSP에서 <form:input ... id="contactId"/>로 명시해줘.
    const joinIdInput = document.getElementById("contactId");

    function hasAdmin(v) {
        return (v || "").toLowerCase().includes("admin");
    }

    // ✅ 입력 중 admin 포함 즉시 차단(브라우저 기본 메시지)


    // JSP에서 <script>window.ctx='${pageContext.request.contextPath}';</script> 넣은 전제
    const ctx = (window.ctx || "").trim();

    // ✅ CSRF 토큰 읽기 (joinFormMail 안의 hidden _csrf)
    function getCsrfToken() {
        const input = document.querySelector('#joinFormMail input[name="_csrf"]');
        return input ? input.value : null;
    }

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

                // ✅ CSRF 추가
                const csrf = getCsrfToken();
                if (csrf) form.append("_csrf", csrf);

                const res = await fetch(ctx + "/login/join/mail/send", {
                    method: "POST",
                    credentials: "same-origin", // ✅ 세션 쿠키 유지(세션에 인증코드 저장하니까 필수)
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
                    },
                    body: form.toString()
                });

                if (res.ok) {
                    if (mailAuthResult) mailAuthResult.textContent = "인증번호를 발송했습니다. 메일함을 확인하세요.";
                    if (mailVerified) mailVerified.value = "false";
                } else {
                    const text = await res.text();
                    console.error(text);
                    if (mailAuthResult) mailAuthResult.textContent = "메일 발송 실패(403/경로/CSRF 확인)";
                }
            } catch (e) {
                console.error(e);
                if (mailAuthResult) mailAuthResult.textContent = "메일 발송 실패(네트워크/콘솔 확인)";
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

                // ✅ CSRF 추가
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
                    if (mailAuthResult) mailAuthResult.textContent = "인증 확인 실패(403/CSRF 확인)";
                    return;
                }

                const data = await res.json();
                if (data && data.success) {
                    if (mailAuthResult) mailAuthResult.textContent = "인증 완료";
                    if (mailVerified) mailVerified.value = "true";
                } else {
                    if (mailAuthResult) mailAuthResult.textContent = "인증번호가 틀렸습니다.";
                    if (mailVerified) mailVerified.value = "false";
                }
            } catch (e) {
                console.error(e);
                if (mailAuthResult) mailAuthResult.textContent = "인증 확인 실패(서버 응답/콘솔 확인)";
            }
        });
    }

    if (joinForm) {
        joinForm.addEventListener("submit", (e) => {


            // ✅ 2) 이메일 인증 체크(기존 로직 유지)
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
