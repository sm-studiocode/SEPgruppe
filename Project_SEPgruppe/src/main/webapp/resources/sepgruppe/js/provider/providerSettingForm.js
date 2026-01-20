
document.addEventListener("DOMContentLoaded", function() {
	
	const ctxLastYear = document.getElementById('lastYearPaymentChart');
		if (ctxLastYear) {
			// 👇 여기에 하드코딩된 더미 데이터 넣습니다
			const lastYearData = [
				{ date: "2024-03-17", amount: 500000 },
				{ date: "2024-03-18", amount: 200000 },
				{ date: "2024-03-21", amount: 350000 },
				{ date: "2024-03-24", amount: 1200000 },
				{ date: "2024-03-31", amount: 800000 },
				{ date: "2024-04-02", amount: 1300000 },
				{ date: "2024-04-12", amount: 400000 },
				{ date: "2024-04-17", amount: 200000 }
			];

			const labels = lastYearData.map(item => item.date);
			const values = lastYearData.map(item => item.amount);

			new Chart(ctxLastYear, {
				type: 'line',
				data: {
					labels: labels,
					datasets: [{
						label: '작년 매출',
						data: values,
						fill: false,
						backgroundColor: 'rgba(75, 192, 192, 0.2)',
						borderColor: 'rgba(75, 192, 192, 1)',
						borderWidth: 1,
						tension: 0.4
					}]
				},
				options: {
					responsive: true,
					plugins: {
						legend: {
							display: true,
							position: 'top'
						}
					},
					scales: {
						y: {
							beginAtZero: true
						}
					}
				}
			});
		}
    /* ====================================================
       1. 매출 차트 (paymentChart)
       ==================================================== */
    const paymentChartEl = document.getElementById('paymentChart');
    const paymentList = JSON.parse(paymentChartEl.getAttribute("data-payment-list"));
    
    // 초기 기간: 1개월
    const initialRange = '1month';
    const { fromDate: payFromDate, toDate: payToDate, filtered: payFiltered } = filterPayments(paymentList, initialRange);
    const payAggregated = groupPaymentsByDay(payFiltered, payFromDate, payToDate);

    // 초기 stepSize: 1개월 -> 주 단위
    let paymentStepSize = 7;

    // 매출 차트 생성
    const paymentChart = new Chart(paymentChartEl.getContext('2d'), {
        type: 'line',
        data: {
            labels: payAggregated.labels,
            datasets: [{
                label: '매출 현황',
                data: payAggregated.salesData,
                fill: false,
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: true },
                tooltip: { mode: 'index', intersect: false }
            },
            scales: {
                x: {
                    type: 'category',       // 명시적으로 카테고리 축 설정
                    ticks: {
                        autoSkip: false,    // 자동으로 라벨을 건너뛰지 않도록
                        stepSize: paymentStepSize
                    },
                    title: { display: true, text: '날짜' }
                },
                y: {
                    beginAtZero: true,
                    display: true,
                    title: { display: true, text: '매출액' }
                }
            }
        }
    });
    
    /* ====================================================
       2. 구독 차트 (라인 및 도넛)
       ==================================================== */
    const subscriptionDataJson = document.getElementById("activeLineChart").getAttribute("data-subscription-list");
    const subscriptionList = JSON.parse(subscriptionDataJson);
    
    // 활성 구독 필터링 (예: 'Y'를 활성으로 가정)
    const activeSubscriptions = subscriptionList.filter(item => item.subscriptionsActive === null);

    // 초기 기간: 1개월
    const { fromDate: subFromDate, toDate: subToDate, filtered: subFiltered } = filterSubscriptions(activeSubscriptions, initialRange);
    const subAggregated = groupSubscriptionsByDay(subFiltered, subFromDate, subToDate);

    // 초기 stepSize: 1개월 -> 주 단위
    let subscriptionStepSize = 7;

    // 구독 라인 차트
    const subscriptionLineChart = new Chart(document.getElementById("activeLineChart").getContext("2d"), {
        type: 'line',
        data: {
            labels: subAggregated.labels,
            datasets: [{
                label: "날짜별 활성 구독 수",
                data: subAggregated.salesData,
                fill: false,
                borderColor: "rgba(75, 192, 192, 1)",
                backgroundColor: "rgba(75, 192, 192, 0.2)",
                borderWidth: 2,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            scales: {
                x: {
                    type: 'category',
                    ticks: {
                        autoSkip: false,
                        stepSize: subscriptionStepSize
                    },
                    title: { display: true, text: "날짜" }
                },
                y: {
                    beginAtZero: true,
                    title: { display: true, text: "구독 수" }
                }
            }
        }
    });
    
    // 도넛 차트 (플랜 유형별)
    const donutDataMap = activeSubscriptions.reduce((acc, cur) => {
        const type = cur.planType || '기타';
        acc[type] = (acc[type] || 0) + 1;
        return acc;
    }, {});
    const donutLabels = Object.keys(donutDataMap);
    const donutCounts = donutLabels.map(type => donutDataMap[type]);

    const activeDonutChart = new Chart(document.getElementById("activeDonutChart").getContext("2d"), {
        type: 'doughnut',
        data: {
            labels: donutLabels,
            datasets: [{
                label: "플랜 유형별 활성 구독",
                data: donutCounts,
                backgroundColor: [
                    'rgba(255, 99, 132, 0.6)',
                    'rgba(54, 162, 235, 0.6)',
                    'rgba(255, 206, 86, 0.6)',
                    'rgba(75, 192, 192, 0.6)'
                ]
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { position: 'right' } }
        }
    });
    
    /* ====================================================
       3. 업데이트 함수 및 버튼 이벤트
       ==================================================== */
    function updatePaymentChart(range) {
        const { fromDate, toDate, filtered } = filterPayments(paymentList, range);
        const aggregated = groupPaymentsByDay(filtered, fromDate, toDate);
        paymentChart.data.labels = aggregated.labels;
        paymentChart.data.datasets[0].data = aggregated.salesData;

        // range별 stepSize 결정
        let stepSize = 1;
        switch(range) {
            case '1week':  // 1일 간격
                stepSize = 1;
                break;
            case '1month':
            case '3months': // 7일 간격
                stepSize = 7;
                break;
            case '6months': // 30일 간격
                stepSize = 30;
                break;
        }
        paymentChart.options.scales.x.ticks.stepSize = stepSize;

        paymentChart.update();
    }
    
    function updateSubscriptionLineChart(range) {
        const { fromDate, toDate, filtered } = filterSubscriptions(activeSubscriptions, range);
        const aggregated = groupSubscriptionsByDay(filtered, fromDate, toDate);
        subscriptionLineChart.data.labels = aggregated.labels;
        subscriptionLineChart.data.datasets[0].data = aggregated.salesData;

        let stepSize = 1;
        switch(range) {
            case '1week':
                stepSize = 1;
                break;
            case '1month':
            case '3months':
                stepSize = 7;
                break;
            case '6months':
                stepSize = 30;
                break;
        }
        subscriptionLineChart.options.scales.x.ticks.stepSize = stepSize;

        subscriptionLineChart.update();
    }
    
    // 버튼 이벤트 등록 (두 차트를 동시에 업데이트)
    const btn1Week   = document.getElementById('btn1Week');
    const btn1Month  = document.getElementById('btn1Month');
    const btn3Months = document.getElementById('btn3Months');
    const btn6Months = document.getElementById('btn6Months');
    
    if (btn1Week) {
        btn1Week.addEventListener('click', () => {
            updatePaymentChart('1week');
            updateSubscriptionLineChart('1week');
        });
    }
    if (btn1Month) {
        btn1Month.addEventListener('click', () => {
            updatePaymentChart('1month');
            updateSubscriptionLineChart('1month');
        });
    }
    if (btn3Months) {
        btn3Months.addEventListener('click', () => {
            updatePaymentChart('3months');
            updateSubscriptionLineChart('3months');
        });
    }
    if (btn6Months) {
        btn6Months.addEventListener('click', () => {
            updatePaymentChart('6months');
            updateSubscriptionLineChart('6months');
        });
    }
    
    /* ====================================================
       4. 공통 유틸리티 함수
       ==================================================== */
    
    // 매출 데이터 필터링: paymentDate 기준
    function filterPayments(list, range) {
        const now = new Date();
        let fromDate = new Date();
        switch (range) {
            case '1week':   fromDate.setDate(now.getDate() - 7); break;
            case '1month':  fromDate.setMonth(now.getMonth() - 1); break;
            case '3months': fromDate.setMonth(now.getMonth() - 3); break;
            case '6months': fromDate.setMonth(now.getMonth() - 6); break;
            default:        fromDate = new Date(0);
        }
        const filtered = list.filter(item => {
            const itemDate = new Date(item.paymentDate);
            return itemDate >= fromDate && itemDate <= now;
        });
        return { fromDate, toDate: now, filtered };
    }
    
    // 구독 데이터 필터링: subscriptionStart 기준
    function filterSubscriptions(list, range) {
        const now = new Date();
        let fromDate = new Date();
        switch (range) {
            case '1week':   fromDate.setDate(now.getDate() - 7); break;
            case '1month':  fromDate.setMonth(now.getMonth() - 1); break;
            case '3months': fromDate.setMonth(now.getMonth() - 3); break;
            case '6months': fromDate.setMonth(now.getMonth() - 6); break;
            default:        fromDate = new Date(0);
        }
        const filtered = list.filter(item => {
            if (!item.subscriptionStart) return false;
            const itemDate = new Date(item.subscriptionStart);
            return itemDate >= fromDate && itemDate <= now;
        });
        return { fromDate, toDate: now, filtered };
    }
    
    // 날짜별 집계
    function groupPaymentsByDay(list, fromDate, toDate) {
        const grouped = {};
        list.forEach(item => {
            const dateStr = (item.paymentDate || item.subscriptionStart).substring(0, 10);
            if (!grouped[dateStr]) {
                grouped[dateStr] = 0;
            }
            grouped[dateStr] += (item.paymentAmount !== undefined ? item.paymentAmount : 1);
        });
        
        const labels = [];
        let current = new Date(fromDate);
        while (current <= toDate) {
            labels.push(formatDate(current));
            current.setDate(current.getDate() + 1);
        }
        const salesData = labels.map(dateStr => grouped[dateStr] || 0);
        return { labels, salesData };
    }
    
    function groupSubscriptionsByDay(list, fromDate, toDate) {
        const grouped = {};
        list.forEach(item => {
            const dateStr = item.subscriptionStart.substring(0, 10);
            if (!grouped[dateStr]) {
                grouped[dateStr] = 0;
            }
            grouped[dateStr] += 1;
        });
        const labels = [];
        let current = new Date(fromDate);
        while (current <= toDate) {
            labels.push(formatDate(current));
            current.setDate(current.getDate() + 1);
        }
        const salesData = labels.map(dateStr => grouped[dateStr] || 0);
        return { labels, salesData };
    }
    
    function formatDate(dateObj) {
        const yyyy = dateObj.getFullYear();
        let mm = dateObj.getMonth() + 1;
        let dd = dateObj.getDate();
        if (mm < 10) mm = '0' + mm;
        if (dd < 10) dd = '0' + dd;
        return `${yyyy}-${mm}-${dd}`;
    }
});
