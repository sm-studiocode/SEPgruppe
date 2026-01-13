/**
 * paymentForm.js 역할(초보용):
 * 1) 월/연 라디오를 바꾸면 화면에 표시되는 가격(priceDisplay)을 바꿔줌
 * 2) "정기결제" 버튼 클릭 시 requestPay()가 실행되고,
 *    포트원 카드등록창을 띄운 뒤 billingKey를 서버에 저장
 * 3) billingKey 저장 성공 후 schedulePayment()를 호출해서
 *    서버(/payment/schedule)에 AJAX 요청을 보내 정기결제 스케줄 + DB 저장이 진행됨
 */

document.addEventListener("DOMContentLoaded", function() {
    const priceDisplay = document.getElementById("priceDisplay");
    const monthly = document.getElementById("monthly");
    const yearly = document.getElementById("yearly");

    if (monthly && priceDisplay) {
        priceDisplay.innerHTML = monthly.value + "원 <span>(부가세 별도)</span>";
    }

    const radios = document.getElementsByName("amount");
    radios.forEach(function(radio) {
        radio.addEventListener("change", function() {
            if (priceDisplay) {
                priceDisplay.innerHTML = this.value + "원 <span>(부가세 별도)</span>";
            }
        });
    });
});

/**
 * [카드 등록 + billingKey 저장]
 *
 * 동작 흐름:
 * 1) 포트원 결제창(카드등록)을 띄움
 * 2) 성공하면 customer_uid(billingKey)를 받음
 * 3) /payment/saveBillingKey 로 billingKey 저장
 * 4) 저장 성공하면 schedulePayment() 호출
 */
function requestPay() {
    var ctx = document.body.getAttribute("data-ctx");
    if (!ctx) ctx = "";

    if (typeof IMP === "undefined") {
        alert("포트원 SDK(IMP)가 로드되지 않았습니다. 스크립트 경로를 확인하세요.");
        return;
    }

    IMP.init('imp17236348');

    IMP.request_pay({
        pg: 'tosspayments',
        pay_method: 'card',
        merchant_uid: 'order_' + new Date().getTime(),
        name: '정기결제용 카드 등록',
        amount: 0,
        customer_uid: 'my_customer_' + new Date().getTime()
    }, function(rsp) {

        if (rsp.success) {
            var xhr = new XMLHttpRequest();
            xhr.open('POST', ctx + '/payment/saveBillingKey', true);
            xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');

            xhr.onload = function() {
                if (xhr.status === 200) {
                    // billingKey 저장 성공 -> 이제 정기결제 스케줄 등록 요청
                    schedulePayment();
                } else {
                    alert('billingKey 저장 실패: ' + xhr.responseText);
                }
            };

            xhr.send(JSON.stringify({
                customerUid: rsp.customer_uid
            }));

        } else {
            alert('카드 등록 실패: ' + rsp.error_msg);
        }
    });
}

/**
 * [정기결제 스케줄 등록 요청]
 *
 * 동작 흐름:
 * 1) hidden input의 planType을 읽는다
 * 2) 컨텍스트 경로(ctx)를 읽는다 (예: /sep)
 * 3) /payment/schedule?planType=... 로 POST 요청을 보낸다
 * 4) 서버 응답(JSON)을 파싱해서 성공/실패 알림을 띄운다
 */
function schedulePayment() {
    var planTypeEl = document.querySelector("input[name='planType']");
    var planType = planTypeEl ? planTypeEl.value : "";

    var ctx = document.body.getAttribute("data-ctx");
    if (!ctx) ctx = "";

    var xhr = new XMLHttpRequest();
    xhr.open('POST', ctx + '/payment/schedule?planType=' + encodeURIComponent(planType), true);

    xhr.onload = function() {
        try {
            var res = JSON.parse(xhr.responseText);

            if (xhr.status === 200 && res.success) {
                alert('정기결제 스케줄 등록 성공');
                location.href = ctx + '/';
            } else {
                alert(res.message || '스케줄 등록 실패');
            }
        } catch(e) {
            alert('응답 파싱 실패');
        }
    };

    xhr.send();
}
