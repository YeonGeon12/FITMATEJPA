document.addEventListener('DOMContentLoaded', function () {
    const tabItems = document.querySelectorAll('.tab-item');
    const tabPanes = document.querySelectorAll('.tab-pane');

    tabItems.forEach(tab => {
        tab.addEventListener('click', function (e) {
            e.preventDefault();

            // 1. 모든 탭에서 'active' 클래스 제거
            tabItems.forEach(item => item.classList.remove('active'));
            tabPanes.forEach(pane => pane.classList.remove('active'));

            // 2. 클릭된 탭에 'active' 클래스 추가
            tab.classList.add('active');

            // 3. 클릭된 탭과 연결된 콘텐츠 영역을 찾아 'active' 클래스 추가
            const targetPaneId = tab.getAttribute('href');
            const targetPane = document.querySelector(targetPaneId);
            if (targetPane) {
                targetPane.classList.add('active');
            }
        });
    });
});