/**
 * 상세 페이지에서 삭제 버튼을 눌러 이 페이지로 이동했을 때,
 * URL에 포함된 파라미터(deleteId, deleteType)를 확인하고
 * 자동으로 해당 항목의 삭제 모달을 띄우는 함수입니다.
 * 페이지가 완전히 로드된 후('load') 실행됩니다.
 */
window.addEventListener('load', function() {
    // 현재 페이지의 URL에서 파라미터 정보를 가져옵니다.
    const urlParams = new URLSearchParams(window.location.search);
    const deleteId = urlParams.get('deleteId'); // 'deleteId' 파라미터 값을 가져옵니다.

    // deleteId 파라미터가 존재할 경우에만 실행합니다.
    if (deleteId) {
        // 해당 ID를 가진 삭제 버튼을 찾습니다.
        // th:data-action="@{/history/diet/delete/{id}(id=${item.id})}" 와 같이 설정했으므로,
        // data-action 속성값 안에 deleteId가 포함된 버튼을 찾습니다.
        const deleteButton = document.querySelector(`.js-delete-trigger[data-action*='${deleteId}']`);

        // 해당 버튼이 존재하면
        if (deleteButton) {
            // 사용자가 직접 누른 것처럼 클릭 이벤트를 강제로 발생시킵니다.
            deleteButton.click();
        }
    }
});

// HTML 문서가 모두 로드된 후, 아래의 모든 코드를 한 번에 실행합니다.
document.addEventListener('DOMContentLoaded', function () {

    // --- 1. 탭 기능 구현 ---
    const tabItems = document.querySelectorAll('.tab-item');
    const tabPanes = document.querySelectorAll('.tab-pane');

    tabItems.forEach(tab => {
        tab.addEventListener('click', function (e) {
            e.preventDefault();
            tabItems.forEach(item => item.classList.remove('active'));
            tabPanes.forEach(pane => pane.classList.remove('active'));
            this.classList.add('active');
            const targetPane = document.querySelector(this.getAttribute('href'));
            if (targetPane) {
                targetPane.classList.add('active');
            }
        });
    });

    // --- 2. 삭제 확인 모달 기능 구현 (historyView.html 전용) ---
    // (이 코드는 상세 페이지가 아닌 목록 페이지의 삭제 버튼을 위한 것입니다.)
    const deleteModal = document.getElementById('deleteModal');
    if (deleteModal) {
        const cancelDeleteBtn = document.getElementById('cancelDelete');
        const confirmDeleteForm = document.getElementById('confirmDeleteForm');
        const deleteTriggers = document.querySelectorAll('.js-delete-trigger');

        deleteTriggers.forEach(button => {
            button.addEventListener('click', function () {
                const actionUrl = this.dataset.action;
                if (actionUrl) {
                    confirmDeleteForm.setAttribute('action', actionUrl);
                    deleteModal.classList.add('show');
                }
            });
        });

        if (cancelDeleteBtn) {
            cancelDeleteBtn.addEventListener('click', function () {
                deleteModal.classList.remove('show');
            });
        }

        deleteModal.addEventListener('click', function (event) {
            if (event.target === deleteModal) {
                deleteModal.classList.remove('show');
            }
        });
    }

    // --- 3. 토스트 메시지 기능 구현 (historyView.html 전용) ---
    const toast = document.getElementById('toast');
    if (toast) {
        // 0.1초 후에 'show' 클래스를 추가하여 부드럽게 나타나도록 합니다.
        setTimeout(() => {
            toast.classList.add('show');
        }, 100);

        // 3.5초(3500ms) 후에 'show' 클래스를 제거하여 사라지도록 합니다.
        setTimeout(() => {
            toast.classList.remove('show');
        }, 3500);
    }
});