/** 
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 3. 14.     	손현진            최초 생성
 * 2025. 3. 14.			손현진			테스트계정 자동 로그인
 *
 * </pre>
 */

document.addEventListener('DOMContentLoaded', function() {
    const tabs = document.querySelectorAll('.tabs .tab');
    const loginFields = document.getElementById('loginFields');
    const joinFields = document.getElementById('joinFields');

    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            tabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');

            const targetId = this.getAttribute('data-target');

            if (targetId === 'loginFields') {
                loginFields.style.display = 'block';
                joinFields.style.display = 'none';
            } else if (targetId === 'joinFields') {
                loginFields.style.display = 'none';
                joinFields.style.display = 'block';
            }
        });
    });
});


document.getElementById("testAccountSelect").addEventListener("change", function() {
	const selectedValue = this.value;
	if (selectedValue) {
		const [userId, userPw] = selectedValue.split("|");
		document.getElementById("floatingID").value = userId;
		document.getElementById("floatingPassword").value = userPw;

		setTimeout(() => {
			document.getElementById("loginForm").submit();
		}, 10);
	} else {
		document.getElementById("floatingID").value = "";
		document.getElementById("floatingPassword").value = "";
	}
});


