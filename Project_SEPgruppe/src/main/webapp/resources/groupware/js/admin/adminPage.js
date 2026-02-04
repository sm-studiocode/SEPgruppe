document.addEventListener("DOMContentLoaded", function () {

  // ===== 차트 (존재할 때만) =====
  const canvas = document.getElementById("lineChart");
  if (canvas && typeof Chart !== "undefined") {
    const lineChart = canvas.getContext("2d");

    new Chart(lineChart, {
      type: "line",
      data: {
        labels: ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"],
        datasets: [{
          label: "Active Users",
          borderColor: "#1d7af3",
          pointBorderColor: "#FFF",
          pointBackgroundColor: "#1d7af3",
          pointBorderWidth: 2,
          pointHoverRadius: 4,
          pointHoverBorderWidth: 1,
          pointRadius: 4,
          backgroundColor: "transparent",
          fill: true,
          borderWidth: 2,
          data: [542,480,430,550,530,453,380,434,568,610,700,900],
        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        legend: {
          position: "bottom",
          labels: { padding: 10, fontColor: "#1d7af3" },
        },
        tooltips: {
          bodySpacing: 4,
          mode: "nearest",
          intersect: 0,
          position: "nearest",
          xPadding: 10,
          yPadding: 10,
          caretPadding: 10,
        },
        layout: { padding: { left: 15, right: 15, top: 15, bottom: 15 } },
      },
    });
  }

  // ===== 토글 이벤트 (요소 있을 때만) =====
  document.querySelectorAll(".menu-title").forEach(item => {
    item.addEventListener("click", () => {
      const submenu = item.querySelector(".submenu");
      const icon = item.querySelector("i");

      if (!submenu) return; // submenu 없으면 무시

      const isOpen = submenu.style.display === "block";
      submenu.style.display = isOpen ? "none" : "block";

      if (icon) {
        if (isOpen) {
          icon.classList.remove("fa-angle-double-down");
          icon.classList.add("fa-angle-double-right");
        } else {
          icon.classList.remove("fa-angle-double-right");
          icon.classList.add("fa-angle-double-down");
        }
      }
    });
  });

});
