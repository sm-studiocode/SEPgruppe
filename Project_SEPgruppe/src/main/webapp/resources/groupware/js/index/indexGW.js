/**
 * 2026. 2. 3. (patch)
 * - URL에서 companyNo 제거 (세션 기반)
 * - fetch에 CSRF 헤더 자동 포함
 * - (A) EMPLOYEE만 위젯 저장/드래그 가능 (COMPANY는 empId 없음)
 */
document.addEventListener('DOMContentLoaded', function () {
  const cfg = document.getElementById('indexGWConfig');
  if (!cfg) {
    console.error('[indexGW] indexGWConfig not found');
    return;
  }

  const contextPath = cfg.dataset.contextPath || '';
  const companyNo = cfg.dataset.companyNo || ''; // 화면/로그용
  const empId = cfg.dataset.empId || '';

  const leftColumn = document.getElementById('leftColumn');
  const rightColumn = document.getElementById('rightColumn');

  if (!leftColumn || !rightColumn) {
    console.error('[indexGW] left/right column not found');
    return;
  }

  // ✅ CSRF 메타 읽기 (Spring Security csrfMetaTags)
  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

  function withCsrf(headers = {}) {
    if (csrfToken && csrfHeader) {
      headers[csrfHeader] = csrfToken;
    }
    return headers;
  }

  // ✅ (A) EMPLOYEE가 아니면(empId 없음) 위젯 드래그/저장 기능 자체를 꺼버림
  if (!empId) {
    console.warn('[indexGW] empId is empty (probably COMPANY). Skip Sortable + widget save.');
    return;
  }

  new Sortable(leftColumn, {
    group: 'widgets',
    animation: 150,
    ghostClass: 'sortable-ghost',
    draggable: '.widget-box',
    onEnd: saveWidgetOrder
  });

  new Sortable(rightColumn, {
    group: 'widgets',
    animation: 150,
    ghostClass: 'sortable-ghost',
    draggable: '.widget-box',
    onEnd: saveWidgetOrder
  });

  function saveWidgetOrder() {
    const columns = ['leftColumn', 'rightColumn'];
    const widgetData = [];

    columns.forEach(columnId => {
      const column = document.getElementById(columnId);
      if (!column) return;

      const widgets = column.querySelectorAll('.widget-box');
      widgets.forEach((widget, index) => {
        const widgetId = widget.dataset.widgetId;
        if (!widgetId) return;

        widgetData.push({
          empId: empId,
          widgetId: widgetId,
          positionNo: index,
          columnName: columnId
        });
      });
    });

    // (안전) 보낼 데이터가 없으면 호출하지 않음
    if (widgetData.length === 0) return;

    // ✅ companyNo를 URL에서 제거 (세션 기반)
    fetch(`${contextPath}/widget/save`, {
      method: 'POST',
      headers: withCsrf({ 'Content-Type': 'application/json' }),
      body: JSON.stringify(widgetData)
    })
      .then(res => res.ok ? res.json() : Promise.reject(res))
      .then(result => {
        if (result && result.success) {
          Swal.fire({
            toast: true,
            position: 'top',
            icon: 'success',
            title: '위젯 위치 저장 완료!',
            showConfirmButton: false,
            timer: 1500,
          });
        } else {
          throw new Error('save failed');
        }
      })
      .catch(async (err) => {
        if (err && err.json) {
          try { console.error('[widget/save] error body', await err.json()); } catch (e) {}
        } else {
          console.error('[widget/save] error', err);
        }

        Swal.fire({
          toast: true,
          position: 'top',
          icon: 'error',
          title: '위젯 저장 실패',
          showConfirmButton: false,
          timer: 1500,
        });
      });
  }
});
