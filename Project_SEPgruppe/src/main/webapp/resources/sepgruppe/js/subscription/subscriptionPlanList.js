document.addEventListener("DOMContentLoaded", ()=>{
  const myModalEl = document.querySelector('#exampleModal');

  myModalEl.addEventListener('show.bs.modal', event => {
    let aTag = event.relatedTarget;
    let url = aTag.href;

    $.ajax({
      url:url,
      dataType:"html",
      success:function(resp){
        $(myModalEl).find(".modal-body").html(resp);
      }
    });
  });

  $(myModalEl).on("click", ".js-go-pay", function(e){
    e.preventDefault();

    let url = $(this).attr("href"); // /payment/subPayment?what=Basic
    $.ajax({
      url: url + "&fragment=true",
      dataType: "html",
      success: function(resp){
        $(myModalEl).find(".modal-body").html(resp);

        // 2단계 화면 들어왔으니 가격 이벤트 다시 붙이기
        if (typeof initPaymentForm === "function") {
          initPaymentForm();
        }
      }
    });
  });
});
