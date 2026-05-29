'use strict';

document.addEventListener('DOMContentLoaded', () => {
    const modalOverlay = document.getElementById('modal-overlay');
    const focusableSelector = 'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])';
    let activeModal = null;
    let modalTrigger = null;

    function getFocusableElements(modal) {
        return Array.from(modal.querySelectorAll(focusableSelector))
            .filter(element => !element.disabled && element.getClientRects().length > 0);
    }

    function focusFirstDialogElement(modal) {
        const focusTarget = getFocusableElements(modal)[0] || modal;
        focusTarget.focus({preventScroll: true});
    }

    function modalOpen(modalId, trigger) {
        const modal = document.getElementById(modalId);
        if (!modal) {
            return;
        }
        activeModal = modal;
        modalTrigger = trigger;
        modal.classList.add('is-open');
        modal.classList.remove('hidden');
        modal.setAttribute('aria-hidden', 'false');
        modalOverlay?.classList.add('is-open');
        focusFirstDialogElement(modal);
    }

    function modalClose(modal) {
        modal.classList.remove('is-open');
        modal.classList.add('hidden');
        modal.setAttribute('aria-hidden', 'true');
        modalOverlay?.classList.remove('is-open');
        activeModal = null;
        if (modalTrigger && document.contains(modalTrigger)) {
            modalTrigger.focus({preventScroll: true});
        }
        modalTrigger = null;
    }

    function keepFocusInModal(event) {
        if (!activeModal) {
            return;
        }
        if (event.key === 'Escape') {
            event.preventDefault();
            modalClose(activeModal);
            return;
        }
        if (event.key !== 'Tab') {
            return;
        }

        const focusableElements = getFocusableElements(activeModal);
        if (focusableElements.length === 0) {
            event.preventDefault();
            activeModal.focus({preventScroll: true});
            return;
        }

        const firstElement = focusableElements[0];
        const lastElement = focusableElements[focusableElements.length - 1];
        if (!activeModal.contains(document.activeElement)) {
            event.preventDefault();
            firstElement.focus({preventScroll: true});
        } else if (event.shiftKey && document.activeElement === firstElement) {
            event.preventDefault();
            lastElement.focus({preventScroll: true});
        } else if (!event.shiftKey && document.activeElement === lastElement) {
            event.preventDefault();
            firstElement.focus({preventScroll: true});
        }
    }

    const modalTriggers = document.querySelectorAll('[data-modal-target]');
    const modalClosers = document.querySelectorAll('[data-modal-hide]');

    modalTriggers.forEach(trigger => {
        trigger.addEventListener('click', () => {
            const modalId = trigger.getAttribute('data-modal-target');
            modalOpen(modalId, trigger);
        });
    });

    modalClosers.forEach(closer => {
        closer.addEventListener('click', (event) => {
            event.stopPropagation();
            if (closer.classList.contains('modal-dialog') && event.target !== closer) {
                return;
            }

            const modalId = closer.getAttribute('data-modal-hide');
            const modal = modalId ? document.getElementById(modalId) : closer.closest('.modal');
            if (modal) {
                modalClose(modal);
            }
        });
    });

    const modalAria = document.getElementById('default-modal');

    if (modalAria) {
        modalAria.addEventListener('click', (event) => {
            const openedModal = document.querySelector('.modal.is-open');
            const modal = document.querySelector('[data-modal-hide=default-modal]');
            if (openedModal && modal && !modal.contains(event.target)) {
                modalClose(openedModal);
            }
        });
    }

    modalOverlay?.addEventListener('click', () => {
        if (activeModal) {
            modalClose(activeModal);
        }
    });

    document.addEventListener('keydown', keepFocusInModal);
});
