/** 
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일              수정자           수정내용
 *  -----------      -------------    ---------------------------
 * 2025. 4. 11.      JSW            최초 생성
 * 2026. 1. 30.      (patch)         JSP inline script 통합(Sortable+저장) / 중복 AJAX 제거
 *
 * </pre>
 */

document.addEventListener('DOMContentLoaded', function () {
  const cfg = document.getElementById('indexGWConfig');
  if (!cfg) {
    console.error('[indexGW] indexGWConfig not found');
    return;
  }

  const contextPath = cfg.dataset.contextPath || '';
  const companyNo = cfg.dataset.companyNo || '';
  const empId = cfg.dataset.empId || '';

  const leftColumn = document.getElementById('leftColumn');
  const rightColumn = document.getElementById('rightColumn');

  if (!leftColumn || !rightColumn) {
    console.error('[indexGW] left/right column not found');
    return;
  }

  // Sortable 초기화
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

    fetch(`${contextPath}/${companyNo}/widget/save`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(widgetData)
    })
      .then(response => response.json())
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
        }
      })
      .catch(() => {
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
