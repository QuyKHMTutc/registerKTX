/**
 * Main Application Initialization
 * Handles CSRF token setup and global utilities
 */

(function () {
    'use strict';

    // CSRF Token Management
    const CSRFToken = {
        token: null,
        header: null,

        init() {
            this.token = document.querySelector("meta[name='_csrf']")?.getAttribute("content");
            this.header = document.querySelector("meta[name='_csrf_header']")?.getAttribute("content");

            if (this.token && this.header) {
                this.setupFetch();
                this.setupJQuery();
            }
        },

        setupFetch() {
            const originalFetch = window.fetch;
            const token = this.token;
            const header = this.header;

            window.fetch = function (url, options = {}) {
                if (options.method && ['POST', 'PUT', 'DELETE', 'PATCH'].includes(options.method.toUpperCase())) {
                    options.headers = options.headers || {};
                    if (options.headers instanceof Headers) {
                        options.headers.append(header, token);
                    } else {
                        options.headers[header] = token;
                    }
                }
                return originalFetch(url, options);
            };
        },

        setupJQuery() {
            if (window.jQuery) {
                const token = this.token;
                const header = this.header;

                jQuery(document).ajaxSend(function (e, xhr, options) {
                    if (!/^(GET|HEAD|OPTIONS|TRACE)$/.test(options.type)) {
                        xhr.setRequestHeader(header, token);
                    }
                });
            }
        }
    };

    // Utility Functions
    const Utils = {
        /**
         * Debounce function calls
         */
        debounce(func, wait) {
            let timeout;
            return function executedFunction(...args) {
                const later = () => {
                    clearTimeout(timeout);
                    func(...args);
                };
                clearTimeout(timeout);
                timeout = setTimeout(later, wait);
            };
        },

        /**
         * Show toast notification
         */
        showToast(message, type = 'info') {
            // Create toast element
            const toast = document.createElement('div');
            toast.className = `toast-notification toast-${type}`;
            toast.innerHTML = `
                <div class="toast-content">
                    <i class="fas fa-${this.getToastIcon(type)}"></i>
                    <span>${message}</span>
                </div>
            `;

            // Add to page
            document.body.appendChild(toast);

            // Animate in
            setTimeout(() => toast.classList.add('show'), 10);

            // Remove after 3 seconds
            setTimeout(() => {
                toast.classList.remove('show');
                setTimeout(() => toast.remove(), 300);
            }, 3000);
        },

        getToastIcon(type) {
            const icons = {
                success: 'check-circle',
                error: 'exclamation-circle',
                warning: 'exclamation-triangle',
                info: 'info-circle'
            };
            return icons[type] || icons.info;
        },

        /**
         * Format currency (VND)
         */
        formatCurrency(amount) {
            return new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND'
            }).format(amount);
        },

        /**
         * Format date
         */
        formatDate(date, format = 'DD/MM/YYYY') {
            const d = new Date(date);
            const day = String(d.getDate()).padStart(2, '0');
            const month = String(d.getMonth() + 1).padStart(2, '0');
            const year = d.getFullYear();

            return format
                .replace('DD', day)
                .replace('MM', month)
                .replace('YYYY', year);
        }
    };

    // Auto-hide alerts
    function initAlerts() {
        const alerts = document.querySelectorAll('.alert:not(.alert-dismissible.fade.show)');
        if (alerts.length > 0) {
            setTimeout(() => {
                alerts.forEach(alert => {
                    new bootstrap.Alert(alert).close();
                });
            }, 5000);
        }
    }

    // Keyboard shortcuts
    function initKeyboardShortcuts() {
        document.addEventListener('keydown', function (e) {
            // Ctrl/Cmd + K: Focus search
            if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
                const searchInput = document.getElementById('searchInput');
                if (searchInput) {
                    e.preventDefault();
                    searchInput.focus();
                    searchInput.select();
                }
            }

            // Escape: Close modals and clear search
            if (e.key === 'Escape') {
                const modals = document.querySelectorAll('.modal.show');
                modals.forEach(modal => {
                    const bsModal = bootstrap.Modal.getInstance(modal);
                    if (bsModal) bsModal.hide();
                });

                const searchInput = document.getElementById('searchInput');
                if (searchInput && document.activeElement === searchInput) {
                    searchInput.value = '';
                    searchInput.dispatchEvent(new Event('input'));
                    searchInput.blur();
                }
            }
        });
    }

    // Lazy load images
    function initLazyLoading() {
        if ('IntersectionObserver' in window) {
            const imageObserver = new IntersectionObserver((entries, observer) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        const img = entry.target;
                        if (img.dataset.src) {
                            img.src = img.dataset.src;
                            img.removeAttribute('data-src');
                            observer.unobserve(img);
                        }
                    }
                });
            });

            document.querySelectorAll('img[data-src]').forEach(img => imageObserver.observe(img));
        }
    }

    // Form loading states
    function initFormLoadingStates() {
        document.querySelectorAll('form').forEach(form => {
            form.addEventListener('submit', function () {
                const submitBtn = this.querySelector('button[type="submit"]');
                if (submitBtn && !submitBtn.disabled) {
                    const originalText = submitBtn.innerHTML;
                    submitBtn.disabled = true;
                    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang xử lý...';

                    // Re-enable after 10 seconds as fallback
                    setTimeout(() => {
                        if (submitBtn.disabled) {
                            submitBtn.disabled = false;
                            submitBtn.innerHTML = originalText;
                        }
                    }, 10000);
                }
            });
        });
    }

    // Smooth scroll to hash
    function initSmoothScroll() {
        if (window.location.hash) {
            setTimeout(() => {
                const element = document.querySelector(window.location.hash);
                if (element) {
                    element.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }
            }, 100);
        }
    }

    // Initialize everything when DOM is ready
    document.addEventListener('DOMContentLoaded', function () {
        CSRFToken.init();
        initAlerts();
        initKeyboardShortcuts();
        initLazyLoading();
        initFormLoadingStates();
        initSmoothScroll();
    });

    // Export utilities globally
    window.KTXApp = {
        utils: Utils,
        csrf: CSRFToken
    };

})();
