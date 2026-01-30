document.addEventListener("DOMContentLoaded", function() {
  const myModalEl = document.querySelector('#exampleModal');
  if (!myModalEl) return;

  myModalEl.addEventListener("change", function(e){
    if (e.target && e.target.name === "paymentMethod") {
      const priceDisplay = myModalEl.querySelector("#priceDisplay");
      if (!priceDisplay) return;
      priceDisplay.innerHTML = e.target.dataset.price + '원 <span>(부가세 별도)</span>';
    }
  });
});
