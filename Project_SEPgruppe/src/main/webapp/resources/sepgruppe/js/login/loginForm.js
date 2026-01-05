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
/* DOM 종료 */


/* =========================
 * 4. Daum 주소 API (전역)
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
