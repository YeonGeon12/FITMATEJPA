// DOMContentLoaded 이벤트는 HTML 문서가 완전히 로드되고 파싱되었을 때 발생합니다.
// 스크립트가 <head>에 있더라도 body 요소에 접근할 수 있게 해줍니다.
document.addEventListener('DOMContentLoaded', function () {

    // --- 탭 기능 구현 ---
    const tabItems = document.querySelectorAll('.tab-item'); // 모든 탭 버튼을 가져옴
    const tabPanes = document.querySelectorAll('.tab-pane'); // 모든 탭 콘텐츠 영역을 가져옴

    // 각 탭 버튼에 클릭 이벤트 리스너를 추가
    tabItems.forEach(tab => {
        tab.addEventListener('click', function (e) {
            e.preventDefault(); // a 태그의 기본 동작(페이지 이동)을 막음

            // 1. 모든 탭과 콘텐츠에서 'active' 클래스를 제거하여 비활성화
            tabItems.forEach(item => item.classList.remove('active'));
            tabPanes.forEach(pane => pane.classList.remove('active'));

            // 2. 클릭된 탭과 그에 해당하는 콘텐츠에 'active' 클래스를 추가하여 활성화
            tab.classList.add('active');
            const targetPaneId = tab.getAttribute('href'); // 클릭된 탭의 href 속성값 (예: '#exercise')을 가져옴
            document.querySelector(targetPaneId)?.classList.add('active'); // 해당 id를 가진 요소를 찾아 'active' 클래스를 추가
        });
    });

    // --- 삭제 확인 모달 기능 구현 ---
    const modal = document.getElementById('deleteModal'); // 모달창 전체를 감싸는 요소
    const cancelBtn = document.getElementById('cancelDelete'); // 모달의 '취소' 버튼
    const confirmDeleteForm = document.getElementById('confirmDeleteForm'); // 모달의 '삭제' 버튼을 감싸는 form
    const deleteTriggers = document.querySelectorAll('.js-delete-trigger'); // 페이지의 모든 '삭제' 버튼 (모달을 여는 역할)

    // 각 '삭제' 버튼(.js-delete-trigger)에 클릭 이벤트 리스너를 추가
    deleteTriggers.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault(); // 버튼이 속한 form의 기본 제출 동작을 막음

            // 1. 클릭된 버튼에서 가장 가까운 부모 <form> 요소를 찾음
            const form = button.closest('form');
            if (form) {
                // 2. 찾은 form의 'action' 속성값(삭제할 대상의 URL)을 가져옴
                const actionUrl = form.getAttribute('action');
                // 3. 모달창 내부의 확인용 form의 'action' 속성을 위에서 가져온 URL로 설정
                confirmDeleteForm.setAttribute('action', actionUrl);
                // 4. 모달창에 'show' 클래스를 추가하여 화면에 표시 (CSS와 연동)
                modal.classList.add('show');
            }
        });
    });

    // 모달의 '취소' 버튼을 클릭했을 때 모달을 숨기는 기능
    if (cancelBtn) {
        cancelBtn.addEventListener('click', function() {
            modal.classList.remove('show');
        });
    }

    // 모달 창 바깥의 어두운 배경(overlay)을 클릭했을 때 모달을 숨기는 기능
    if (modal) {
        modal.addEventListener('click', function(e) {
            // 클릭된 요소가 모달 배경 자체(e.target)일 때만 닫히도록 함
            if (e.target === modal) {
                modal.classList.remove('show');
            }
        });
    }

    // --- 토스트 메시지 기능 구현 ---
    const toast = document.getElementById('toast');
    if (toast) {
        // HTML의 'data-message' 속성에서 표시할 메시지를 가져옴
        const message = toast.getAttribute('data-message');
        if (message) {
            toast.textContent = message; // 토스트 요소에 메시지를 채움
            toast.classList.add('show'); // 'show' 클래스를 추가하여 화면에 표시
            // 3초 후에 'show' 클래스를 제거하여 사라지게 함
            setTimeout(() => {
                toast.classList.remove('show');
            }, 3000);
        }
    }
});
