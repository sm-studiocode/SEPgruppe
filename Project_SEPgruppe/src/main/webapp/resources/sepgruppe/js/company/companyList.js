document.addEventListener("DOMContentLoaded", function () {

  // ctx
  const ctx = (window.ctx || document.body.dataset.ctx || "").trim();

  function getCsrf() {
    return {
      token: document.querySelector('meta[name="_csrf"]')?.content,
      header: document.querySelector('meta[name="_csrf_header"]')?.content
    };
  }

  /* =========================
   * DataTable + 필터
   * ========================= */
  const tableEl = document.getElementById("datatablesSimple");
  const dt = new simpleDatatables.DataTable(tableEl);

  const planFilter = document.getElementById("planFilter");
  if (planFilter) {
    planFilter.addEventListener("change", (e) => {
      dt.search(e.target.value || "");
    });
  }

  /* =========================
   * 클릭 이벤트 (테이블 전체에 위임)
   * simple-datatables가 tbody를 갈아끼우기 때문에
   * tbody에 걸면 깨질 수 있음 → tableEl에 걸기
   * ========================= */
  tableEl.addEventListener("click", async (e) => {

    // 1) 해지 버튼 클릭
    const cancelBtn = e.target.closest(".btn-cancel-sub");
    if (cancelBtn) {
      e.preventDefault();
      e.stopPropagation();

      const row = cancelBtn.closest("tr");
      if (!row) return;

      // ✅ dataset은 datatables가 날릴 수 있음 → 2번째 칸 텍스트로 가져오기
      const contactId = (row.cells?.[1]?.textContent || "").trim();
      if (!contactId) return alert("contactId를 찾을 수 없습니다.");

      if (!confirm(`[${contactId}] 구독을 해지하시겠습니까?\n자동결제도 중단됩니다.`)) return;

      const { token, header } = getCsrf();

      const res = await fetch(`${ctx}/provider/company/${contactId}/cancel`, {
        method: "POST",
        headers: { [header]: token },
        credentials: "same-origin"
      });

      // body 없는 응답일 수 있으니 안전 파싱
      let data = null;
      try { data = await res.json(); } catch (err) {}

      if (res.ok) {
        alert("구독 해지 완료");

        // ✅ 관리 칸(td) 즉시 UI 변경
        const td = cancelBtn.closest("td");
        if (td) td.innerHTML = `<span class="badge bg-secondary">해지됨</span>`;
        else {
          cancelBtn.style.display = "none";
          cancelBtn.disabled = true;
        }

        // ✅ 결제상태 컬럼(5번째= index 4) → 해지
        if (row.cells?.[4]) row.cells[4].textContent = "해지";

        return;
      }

      alert(data?.message || `해지 실패 (HTTP ${res.status})`);
      return;
    }

    // 2) 행 클릭 → 결제이력 모달
    const row = e.target.closest("tr");
    if (!row) return;

    // ✅ 헤더(tr) 클릭 방지: tbody 내부 row만 허용
    const isBodyRow = row.parentElement && row.parentElement.tagName === "TBODY";
    if (!isBodyRow) return;

    // ✅ dataset 대신 2번째 칸 텍스트 사용 (datatables가 다시 렌더해도 안전)
    const contactId = (row.cells?.[1]?.textContent || "").trim();
    if (!contactId) return;

    try {
      const res = await fetch(`${ctx}/provider/company/${contactId}/payments`, {
        method: "GET",
        credentials: "same-origin"
      });

      if (!res.ok) {
        alert(`결제이력 조회 실패 (HTTP ${res.status})`);
        return;
      }

      const list = await res.json();
      renderPaymentModal(contactId, list);

    } catch (err) {
      console.error(err);
      alert("결제이력 조회 중 오류 발생");
    }
  });

  function renderPaymentModal(contactId, list) {
    const tbody = document.getElementById("phTbody");
    tbody.innerHTML = "";

    if (!Array.isArray(list) || list.length === 0) {
      tbody.innerHTML = `<tr><td colspan="7" class="text-muted">결제 이력 없음</td></tr>`;
    } else {
      list.forEach(p => {
        tbody.innerHTML += `
          <tr>
            <td>${p.paymentNo ?? ""}</td>
            <td>${p.subscriptionNo ?? ""}</td>
            <td>${p.paymentDate ?? ""}</td>
            <td>${(p.paymentAmount ?? 0).toLocaleString()}원</td>
            <td>${p.paymentMethod ?? ""}</td>
            <td>${p.paymentStatus ?? ""}</td>
            <td>${p.autoPayment ?? ""}</td>
          </tr>
        `;
      });
    }

    document.getElementById("phContactId").textContent = contactId;

    // ✅ 안전한 모달 show
    const modalEl = document.getElementById("paymentHistoryModal");
    const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
    modal.show();
  }
});
