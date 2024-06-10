'use strict';

document.addEventListener('DOMContentLoaded', () => {
    const modalOverlay = document.getElementById('modal-overlay');

    function modalOpen(modalId) {
        const modal = document.getElementById(modalId);
        modal.classList.add('is-open');
        modal.classList.remove('hidden');
        modalOverlay.classList.add('is-open');
    }

    function modalClose(modal) {
        modal.classList.remove('is-open');
        modal.classList.add('hidden');
        modalOverlay.classList.remove('is-open');
    }

    const modalTriggers = document.querySelectorAll('[data-modal-target]');
    const modalClosers = document.querySelectorAll('[data-modal-hide]');

    modalTriggers.forEach(trigger => {
        trigger.addEventListener('click', () => {
            const modalId = trigger.getAttribute('data-modal-target');
            modalOpen(modalId);
        });
    });

    modalClosers.forEach(closer => {
        closer.addEventListener('click', (event) => {
            event.stopPropagation();
            if (event.target === closer) {
                const modal = closer.closest('.modal');
                if (modal) {
                    modalClose(modal);
                }
            }
        });
    });

    const modalAria = document.getElementById('default-modal');

    modalAria.addEventListener('click', (event) => {
        const openedModal = document.querySelector('.modal.is-open');
        const modal = document.querySelector('[data-modal-hide=default-modal]');
        if (openedModal && !modal.contains(event.target)) {
            modalClose(openedModal);
        }
    });
});