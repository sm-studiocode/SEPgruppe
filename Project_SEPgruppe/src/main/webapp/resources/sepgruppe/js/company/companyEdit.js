document.addEventListener('DOMContentLoaded', function() {


    /* myPage 값 검증 */
    $("#button").click(function(event) {
        event.preventDefault();

        const contactPw = $("#contactPw").val();
        const pwErrorCk = $("#contactPwCk").val();
        const pwErrorSpan = $("#pwErrorCk");

        if (contactPw !== pwErrorCk) {
            pwErrorSpan.text("비밀번호가 일치하지 않습니다.");
        } else {
            pwErrorSpan.text("");
        }

        /* 이메일 형식 검증 */
        const contactEmail = $("input[name='contactEmail']").val();
        const emailErrorSpan = $("#emailError");
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailPattern.test(contactEmail)) {
            emailErrorSpan.text("유효한 이메일 형식이 아닙니다.");
        } else {
            emailErrorSpan.text("");
        }

        /* 휴대폰 번호 형식 검증 */
        const contactPhone = $("input[name='contactPhone']").val();
        const phoneErrorSpan = $("#phoneError");
        const phonePattern = /^\d{3}-\d{4}-\d{4}$/;

        if (!phonePattern.test(contactPhone)) {
            phoneErrorSpan.text("유효한 휴대폰 번호 형식이 아닙니다. (000-0000-0000)");
        } else {
            phoneErrorSpan.text("");
        }

        // 모든 검증이 통과하면 폼 제출
        if (pwErrorSpan.text() == "" && emailErrorSpan.text() == "" && phoneErrorSpan.text() == "") {
            $("form[name='frm'] input").each(function() {
                if ($(this).val() == "") {
                    $(this).prop('disabled', true); // 비어 있는 필드를 비활성화
                }
            });

            $("form[name='frm']").submit(); // 폼 제출
        }
    });

	// 모달 띄우기
    const triggerTabList = document.querySelectorAll('#mypageBtn button');
    triggerTabList.forEach(triggerEl => {
        const tabTrigger = new bootstrap.Tab(triggerEl);

        triggerEl.addEventListener('click', event => {
            event.preventDefault();
            if (event.target.id === 'changeModal') {
                myModalAlternative.show();
            } else {
                tabTrigger.show();
				triggerTabList.forEach(tab => {
                    if (tab === event.target) {
                        tab.style.backgroundColor = 'rgb(44, 217, 1)'; // 활성화된 탭 배경색 흰색으로 설정
						tab.style.color = 'black';
                    } else {
                        tab.style.backgroundColor = ''; // 나머지 탭 배경색 기본값으로 설정
                    }
                });
            }
        });
    });


	// 모달 Enter키 = 확인버튼
	document.getElementById('confirmPw').addEventListener('keypress', function(event) {
	    if (event.key === 'Enter') {
	        event.preventDefault(); // 기본 엔터 키 동작 방지
	        document.getElementById('verifyBtn').click(); // 확인 버튼 클릭
	    }
	});

	// 모달이 열릴 때 입력 필드 초기화
	staticBackdrop.addEventListener('shown.bs.modal', event => {
	    $('#confirmPw').val(''); // 입력 필드 초기화
	    $('#pwErrorCk').text(''); // 에러 메시지 초기화
	});

	// 모달이 닫힐 때 입력 필드 초기화
	staticBackdrop.addEventListener('hidden.bs.modal', event => {
	    $('#confirmPw').val(''); // 입력 필드 초기화
	    $('#pwErrorCk').text(''); // 에러 메시지 초기화
	});

    /* 마이페이지 정보수정 진입 시 비밀번호 검증 */
	const myModalAlternative = new bootstrap.Modal('#staticBackdrop');
    $('#verifyBtn').click(function() {
        const password = $('#confirmPw').val();

        $.ajax({
            url: '/sep/company/mypage/verifyPassword',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ contactPw: password }),
            success: function() {
                myModalAlternative.hide();
                const triggerEl = document.querySelector('#changeModal');
                bootstrap.Tab.getInstance(triggerEl).show();

                console.log("성공");
            },
            error: function(jqXHR) {
                if (jqXHR.status === 401) {
                    Swal.fire({
						icon: "error",
						title: "비밀번호 인증 실패",
						text: "비밀번호가 올바르지 않습니다!",
						footer: '<a href="/sep/login/findPw">비밀번호 찾기</a>'
                    });
                }
            }
        });
    });
	
	// 현재 시간 표시
	function displayTime() {
	  const now = new Date();
	  let hours = now.getHours();
	  const minutes = now.getMinutes();
	  const seconds = now.getSeconds();
	  const ampm = hours >= 12 ? 'PM' : 'AM';

	  // 12시간 형식으로 변환
	  hours = hours % 12;
	  hours = hours ? hours : 12; // 0시는 12시로 표시

	  const timeString = `${hours}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')} ${ampm}`;
	  document.querySelector('.time').textContent = timeString;
	}

	// 초기 시간 표시
	displayTime();

	// 1초마다 시간 업데이트
	setInterval(displayTime, 1000);

});
