// DOM 콘텐츠가 모두 로드된 후 스크립트를 실행합니다.
document.addEventListener('DOMContentLoaded', function() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');

    // 각 탭 버튼에 클릭 이벤트를 추가합니다.
    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            // 모든 버튼과 콘텐츠에서 'active' 클래스를 먼저 제거합니다.
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));

            // 클릭된 버튼과 그에 해당하는 콘텐츠에만 'active' 클래스를 추가합니다.
            button.classList.add('active');
            const targetTab = document.getElementById(button.dataset.tab);
            if (targetTab) {
                targetTab.classList.add('active');
            }
        });
    });
});