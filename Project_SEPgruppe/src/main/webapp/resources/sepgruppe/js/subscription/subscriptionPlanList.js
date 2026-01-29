document.addEventListener("DOMContentLoaded", () => {
  const myModalEl = document.querySelector("#exampleModal");
  if (!myModalEl) return;

  // ✅ 플랜 카드 클릭 시: 링크 이동 방지 + 모달 오픈 + AJAX 로드
  document.addEventListener("click", (e) => {
    const aTag = e.target.closest("a.js-plan-open");
    if (!aTag) return;

    e.preventDefault(); // ★ 이거 없으면 페이지 이동해버림

    const url = aTag.dataset.url;              // /subscriptionPlan/{planType}
    const planType = aTag.dataset.planType || "";

    myModalEl.dataset.planType = planType;

    // 모달 열기(수동)
    const modal = bootstrap.Modal.getOrCreateInstance(myModalEl);
    modal.show();

    // 내용 로드
    $.ajax({
      url: url,
      dataType: "html",
      success: function (resp) {
        $(myModalEl).find(".modal-body").html(resp);
      }
    });
  });

  // ✅ 모달 내부 "구독하기(다음단계)" 버튼
  $(myModalEl).on("click", ".js-go-pay", function (e) {
    e.preventDefault();

    const url = $(this).attr("href"); // /payment/subPayment?what=Basic 등
    $.ajax({
      url: url + "&fragment=true",
      dataType: "html",
      success: function (resp) {
        $(myModalEl).find(".modal-body").html(resp);

        if (typeof initPaymentForm === "function") initPaymentForm();
      }
    });
  });

  // ✅ 혹시 form submit으로 튀는 것 방지(모달 내 submit은 다 막기)
  $(myModalEl).on("submit", "form", function (e) {
    e.preventDefault();
    return false;
  });
});
