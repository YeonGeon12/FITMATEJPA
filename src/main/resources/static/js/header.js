// DOM(Document Object Model) 콘텐츠가 모두 로드되면 스크립트를 실행합니다.
document.addEventListener('DOMContentLoaded', function() {
    // 헤더의 사용자 메뉴 드롭다운 기능
    const menuToggle = document.getElementById('menu-toggle');
    const dropdownMenu = document.getElementById('dropdown-menu');

    // 메뉴 토글 버튼과 드롭다운 메뉴 요소가 모두 존재할 때만 로직을 실행합니다.
    if (menuToggle && dropdownMenu) {

        // 메뉴 토글 버튼을 클릭했을 때의 이벤트 리스너
        menuToggle.addEventListener('click', (event) => {
            // 이벤트가 상위 요소로 전파(버블링)되는 것을 막습니다.
            // 이것이 없으면 body의 클릭 이벤트가 바로 실행되어 메뉴가 열리자마자 닫힙니다.
            event.stopPropagation();

            // 'show' 클래스를 추가하거나 제거하여 드롭다운 메뉴를 보이거나 숨깁니다.
            dropdownMenu.classList.toggle('show');
        });
    }

    // 페이지의 다른 곳(body)을 클릭하면 드롭다운 메뉴를 닫습니다.
    document.body.addEventListener('click', () => {
        // 드롭다운 메뉴가 존재하고, 'show' 클래스를 가지고 있을 때 (즉, 열려 있을 때)
        if (dropdownMenu && dropdownMenu.classList.contains('show')) {
            dropdownMenu.classList.remove('show'); // 'show' 클래스를 제거하여 메뉴를 숨깁니다.
        }
    });
});