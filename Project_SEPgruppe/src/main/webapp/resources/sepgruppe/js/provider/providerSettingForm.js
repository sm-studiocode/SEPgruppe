document.addEventListener("DOMContentLoaded", function () {
  // Chart.js 로딩 확인
  if (typeof Chart === "undefined") {
    console.error("[Chart] Chart.js가 로딩되지 않았습니다. (Chart is undefined)");
    return;
  }

  /* ====================================================
     0. 공통: 안전 JSON 파서
     ==================================================== */
  function safeParseJson(raw, label) {
    if (!raw) {
      console.warn(`[${label}] 데이터 없음 (attribute empty)`);
      return [];
    }
    try {
      // 혹시라도 HTML 엔티티가 섞였을 경우 최소 복구
      const fixed = raw
        .replaceAll("&quot;", '"')
        .replaceAll("&#34;", '"')
        .replaceAll("&apos;", "'")
        .replaceAll("&#39;", "'")
        .replaceAll("&amp;", "&");

      return JSON.parse(fixed);
    } catch (e) {
      console.error(`[${label}] JSON.parse 실패`, e, raw.slice(0, 200));
      return [];
    }
  }

  function toDateObj(dateLike) {
    // "yyyy-MM-dd" / ISO / Date 모두 처리
    if (!dateLike) return null;
    if (dateLike instanceof Date) return dateLike;
    const d = new Date(dateLike);
    return isNaN(d.getTime()) ? null : d;
  }

  function formatDate(dateObj) {
    const yyyy = dateObj.getFullYear();
    let mm = dateObj.getMonth() + 1;
    let dd = dateObj.getDate();
    if (mm < 10) mm = "0" + mm;
    if (dd < 10) dd = "0" + dd;
    return `${yyyy}-${mm}-${dd}`;
  }

  /* ====================================================
     1. 매출 차트 (paymentChart)
     ==================================================== */
  const paymentChartEl = document.getElementById("paymentChart");
  if (!paymentChartEl) {
    console.warn("[paymentChart] canvas 없음");
    // 이 페이지가 아닌 곳에서 JS가 공용으로 로딩될 수도 있으니 return 하지 않음
  }

  const paymentList = paymentChartEl
    ? safeParseJson(paymentChartEl.getAttribute("data-payment-list"), "paymentList")
    : [];

  const initialRange = "1month";
  let paymentChart = null;

  function filterPayments(list, range) {
    const now = new Date();
    let fromDate = new Date(now);

    switch (range) {
      case "1week": fromDate.setDate(now.getDate() - 7); break;
      case "1month": fromDate.setMonth(now.getMonth() - 1); break;
      case "3months": fromDate.setMonth(now.getMonth() - 3); break;
      case "6months": fromDate.setMonth(now.getMonth() - 6); break;
      default: fromDate = new Date(0);
    }

    const filtered = (list || []).filter((item) => {
      // paymentDate가 없으면 걸러
      const d = toDateObj(item.paymentDate);
      return d && d >= fromDate && d <= now;
    });

    return { fromDate, toDate: now, filtered };
  }

  function groupPaymentsByDay(list, fromDate, toDate) {
    const grouped = {};

    (list || []).forEach((item) => {
      const d = (item.paymentDate || "").toString().substring(0, 10);
      if (!d) return;
      if (!grouped[d]) grouped[d] = 0;
      grouped[d] += Number(item.paymentAmount || 0);
    });

    const labels = [];
    let current = new Date(fromDate);
    while (current <= toDate) {
      labels.push(formatDate(current));
      current.setDate(current.getDate() + 1);
    }

    const salesData = labels.map((d) => grouped[d] || 0);
    return { labels, salesData };
  }

  if (paymentChartEl) {
    const { fromDate: payFromDate, toDate: payToDate, filtered: payFiltered } =
      filterPayments(paymentList, initialRange);

    const payAggregated = groupPaymentsByDay(payFiltered, payFromDate, payToDate);

    paymentChart = new Chart(paymentChartEl.getContext("2d"), {
      type: "line",
      data: {
        labels: payAggregated.labels,
        datasets: [
          {
            label: "매출 현황",
            data: payAggregated.salesData,
            fill: false,
            backgroundColor: "rgba(75, 192, 192, 0.2)",
            borderColor: "rgba(75, 192, 192, 1)",
            borderWidth: 1,
            tension: 0.4,
          },
        ],
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: true },
          tooltip: { mode: "index", intersect: false },
        },
        scales: {
          x: {
            type: "category",
            ticks: { autoSkip: false, stepSize: 7 },
            title: { display: true, text: "날짜" },
          },
          y: {
            beginAtZero: true,
            title: { display: true, text: "매출액" },
          },
        },
      },
    });
  }

  /* ====================================================
     2. 구독 차트 (라인 + 도넛)
     ==================================================== */
  const activeLineEl = document.getElementById("activeLineChart");
  const activeDonutEl = document.getElementById("activeDonutChart");

  const subscriptionList = activeLineEl
    ? safeParseJson(activeLineEl.getAttribute("data-subscription-list"), "subscriptionList")
    : [];

  // ✅ 여기서 전역으로 확인 가능 (디버깅용)
  window.subscriptionList = subscriptionList;

  // ✅ 활성 구독 판정: end가 없거나, end >= 오늘
  // (네 서버에서 subscriptionEnd를 "한 달 뒤"로 넣고 있으니 이게 가장 자연스러움)
  const todayStr = formatDate(new Date());
  const activeSubscriptions = subscriptionList.filter((item) => {
    const end = (item.subscriptionEnd || "").toString().substring(0, 10);
    if (!end) return true;              // end가 없으면 활성로 취급
    return end >= todayStr;             // 문자열 비교(yyyy-MM-dd)는 안전함
  });

  window.activeSubscriptions = activeSubscriptions;

  function filterSubscriptions(list, range) {
    const now = new Date();
    let fromDate = new Date(now);

    switch (range) {
      case "1week": fromDate.setDate(now.getDate() - 7); break;
      case "1month": fromDate.setMonth(now.getMonth() - 1); break;
      case "3months": fromDate.setMonth(now.getMonth() - 3); break;
      case "6months": fromDate.setMonth(now.getMonth() - 6); break;
      default: fromDate = new Date(0);
    }

    const filtered = (list || []).filter((item) => {
      const d = toDateObj(item.subscriptionStart);
      return d && d >= fromDate && d <= now;
    });

    return { fromDate, toDate: now, filtered };
  }

  function groupSubscriptionsByDay(list, fromDate, toDate) {
    const grouped = {};
    (list || []).forEach((item) => {
      const d = (item.subscriptionStart || "").toString().substring(0, 10);
      if (!d) return;
      if (!grouped[d]) grouped[d] = 0;
      grouped[d] += 1;
    });

    const labels = [];
    let current = new Date(fromDate);
    while (current <= toDate) {
      labels.push(formatDate(current));
      current.setDate(current.getDate() + 1);
    }

    const counts = labels.map((d) => grouped[d] || 0);
    return { labels, counts };
  }

  let subscriptionLineChart = null;
  if (activeLineEl) {
    const { fromDate: subFromDate, toDate: subToDate, filtered: subFiltered } =
      filterSubscriptions(activeSubscriptions, initialRange);

    const subAggregated = groupSubscriptionsByDay(subFiltered, subFromDate, subToDate);

    subscriptionLineChart = new Chart(activeLineEl.getContext("2d"), {
      type: "line",
      data: {
        labels: subAggregated.labels,
        datasets: [
          {
            label: "날짜별 활성 구독 수",
            data: subAggregated.counts,
            fill: false,
            borderColor: "rgba(75, 192, 192, 1)",
            backgroundColor: "rgba(75, 192, 192, 0.2)",
            borderWidth: 2,
            tension: 0.4,
          },
        ],
      },
      options: {
        responsive: true,
        scales: {
          x: {
            type: "category",
            ticks: { autoSkip: false, stepSize: 7 },
            title: { display: true, text: "날짜" },
          },
          y: {
            beginAtZero: true,
            title: { display: true, text: "구독 수" },
          },
        },
      },
    });
  }

  // ✅ 도넛 (플랜 유형별) - planType 기준
  let activeDonutChart = null;
  if (activeDonutEl) {
    const donutDataMap = activeSubscriptions.reduce((acc, cur) => {
      const type = cur.planType || "기타";
      acc[type] = (acc[type] || 0) + 1;
      return acc;
    }, {});

    const donutLabels = Object.keys(donutDataMap);
    const donutCounts = donutLabels.map((k) => donutDataMap[k]);

    activeDonutChart = new Chart(activeDonutEl.getContext("2d"), {
      type: "doughnut",
      data: {
        labels: donutLabels,
        datasets: [
          {
            label: "플랜 유형별 활성 구독",
            data: donutCounts,
            backgroundColor: [
              "rgba(255, 99, 132, 0.6)",
              "rgba(54, 162, 235, 0.6)",
              "rgba(255, 206, 86, 0.6)",
              "rgba(75, 192, 192, 0.6)",
            ],
          },
        ],
      },
      options: {
        responsive: true,
        plugins: { legend: { position: "right" } },
      },
    });
  }

  /* ====================================================
     3. 버튼 업데이트
     ==================================================== */
  function stepSizeFor(range) {
    switch (range) {
      case "1week": return 1;
      case "1month": return 7;
      case "3months": return 7;
      case "6months": return 30;
      default: return 7;
    }
  }

  function updatePaymentChart(range) {
    if (!paymentChart) return;
    const { fromDate, toDate, filtered } = filterPayments(paymentList, range);
    const aggregated = groupPaymentsByDay(filtered, fromDate, toDate);

    paymentChart.data.labels = aggregated.labels;
    paymentChart.data.datasets[0].data = aggregated.salesData;
    paymentChart.options.scales.x.ticks.stepSize = stepSizeFor(range);
    paymentChart.update();
  }

  function updateSubscriptionLineChart(range) {
    if (!subscriptionLineChart) return;
    const { fromDate, toDate, filtered } = filterSubscriptions(activeSubscriptions, range);
    const aggregated = groupSubscriptionsByDay(filtered, fromDate, toDate);

    subscriptionLineChart.data.labels = aggregated.labels;
    subscriptionLineChart.data.datasets[0].data = aggregated.counts;
    subscriptionLineChart.options.scales.x.ticks.stepSize = stepSizeFor(range);
    subscriptionLineChart.update();
  }

  const btn1Week = document.getElementById("btn1Week");
  const btn1Month = document.getElementById("btn1Month");
  const btn3Months = document.getElementById("btn3Months");
  const btn6Months = document.getElementById("btn6Months");

  if (btn1Week) btn1Week.addEventListener("click", () => { updatePaymentChart("1week"); updateSubscriptionLineChart("1week"); });
  if (btn1Month) btn1Month.addEventListener("click", () => { updatePaymentChart("1month"); updateSubscriptionLineChart("1month"); });
  if (btn3Months) btn3Months.addEventListener("click", () => { updatePaymentChart("3months"); updateSubscriptionLineChart("3months"); });
  if (btn6Months) btn6Months.addEventListener("click", () => { updatePaymentChart("6months"); updateSubscriptionLineChart("6months"); });
});
