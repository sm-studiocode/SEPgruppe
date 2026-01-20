/**
 * paymentForm.js
 */

function initPaymentForm() {
    const priceDisplay = document.getElementById("priceDisplay");
    const monthly = document.getElementById("monthly");

    if (monthly && priceDisplay) {
        priceDisplay.innerHTML = monthly.value + "원 <span>(부가세 별도)</span>";
    }

    const radios = document.getElementsByName("amount");
    radios.forEach(function (radio) {
        radio.addEventListener("change", function () {
            if (priceDisplay) {
                priceDisplay.innerHTML = this.value + "원 <span>(부가세 별도)</span>";
            }
        });
    });
}

document.addEventListener("DOMContentLoaded", function () {
    initPaymentForm();
});

function getCtx() {
    // body data-ctx가 있으면 그걸 쓰고, 없으면 window.ctx fallback
    let ctx = document.body.getAttribute("data-ctx");
    if (!ctx) ctx = (window.ctx || "").trim();
    if (!ctx) ctx = "";
    return ctx;
}

function getCsrf() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute("content");
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute("content");
    return { token, header };
}

/**
 * [카드 등록 + billingKey 저장]
 */
function requestPay() {
    var ctx = getCtx();

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
    }, function (rsp) {

        if (rsp.success) {
            var xhr = new XMLHttpRequest();
            xhr.open('POST', ctx + '/payment/saveBillingKey', true);
            xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');

            // ✅ CSRF 헤더 추가
            const { token, header } = getCsrf();
            if (token && header) {
                xhr.setRequestHeader(header, token);
            }

            xhr.withCredentials = true; // ✅ 세션쿠키

            xhr.onload = function () {
                if (xhr.status === 200) {
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
 */
function schedulePayment() {
    var planTypeEl = document.querySelector("input[name='planType']");
    var planType = planTypeEl ? planTypeEl.value : "";

    var ctx = getCtx();

    var xhr = new XMLHttpRequest();
    xhr.open('POST', ctx + '/payment/schedule?planType=' + encodeURIComponent(planType), true);

    // ✅ CSRF 헤더 추가
    const { token, header } = getCsrf();
    if (token && header) {
        xhr.setRequestHeader(header, token);
    }

    xhr.withCredentials = true;

    xhr.onload = function () {
        try {
            var res = JSON.parse(xhr.responseText);

            if (xhr.status === 200 && res.success) {
                alert('정기결제 스케줄 등록 성공');
                location.href = ctx + '/';
            } else {
                alert(res.message || '스케줄 등록 실패');
            }
        } catch (e) {
            alert('응답 파싱 실패');
        }
    };

    xhr.send();
}
