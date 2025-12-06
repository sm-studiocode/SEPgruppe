document.addEventListener("DOMContentLoaded", function() {
    const sidebar1 = document.querySelector('.sidebar');
    const sidebar2 = document.querySelector('.sidebar2');
    const mainPanel = document.querySelector('.main-panel');
    const collapsedSidebarWidth = 190; // 사이드바가 접혔을 때의 너비
    let isCollapsed = false; // 사이드바의 상태를 추적하는 변수

    if (sidebar1 && sidebar2) {
        mainPanel.style.width = 'calc(100% - 530px)';

        // sidebar1이 클릭될 때 이벤트 리스너 추가
        sidebar1.addEventListener('click', function() {
            sidebar2.style.transition = 'transform 0.3s ease-in-out';
            mainPanel.style.transition = 'width 0.3s ease-in-out'; // mainPanel의 너비에 애니메이션 추가

            if (isCollapsed) {
                // 사이드바가 접혔을 때, 펼치는 동작
                sidebar2.style.transform = `translateX(0)`; // 사이드바2를 원래 위치로 이동
                mainPanel.style.width = 'calc(100% - 530px)'; // mainPanel의 너비를 원래대로 복원
            } else {
                // 사이드바가 펼쳐졌을 때, 접는 동작
                sidebar2.style.transform = `translateX(-${collapsedSidebarWidth}px)`; // 사이드바2를 왼쪽으로 이동
                mainPanel.style.width = `calc(100% - 340px)`; // mainPanel의 너비를 줄임 (530px - 190px)
            }

            // 사이드바 상태 토글
            isCollapsed = !isCollapsed;
            console.log(sidebar2.style.transform);
        });
    }
});
