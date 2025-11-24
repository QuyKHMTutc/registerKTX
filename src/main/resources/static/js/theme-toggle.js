/**
 * Dark Mode Toggle Component
 * Handles theme switching with localStorage persistence
 */

(function () {
    'use strict';

    const ThemeManager = {
        STORAGE_KEY: 'ktx-theme',
        THEME_ATTR: 'data-theme',

        /**
         * Initialize theme on page load
         */
        init() {
            // Load saved theme or default to light
            const savedTheme = localStorage.getItem(this.STORAGE_KEY) || 'light';
            this.setTheme(savedTheme, false);

            // Add smooth transition for theme changes
            this.addTransitionClass();

            // Listen for toggle button clicks
            this.attachEventListeners();
        },

        /**
         * Set theme and optionally save to localStorage
         */
        setTheme(theme, save = true) {
            document.documentElement.setAttribute(this.THEME_ATTR, theme);

            if (save) {
                localStorage.setItem(this.STORAGE_KEY, theme);
            }

            // Update toggle button icon
            this.updateToggleIcon(theme);
        },

        /**
         * Toggle between light and dark theme
         */
        toggleTheme() {
            const currentTheme = document.documentElement.getAttribute(this.THEME_ATTR) || 'light';
            const newTheme = currentTheme === 'light' ? 'dark' : 'light';
            this.setTheme(newTheme);
        },

        /**
         * Add transition class for smooth theme switching
         */
        addTransitionClass() {
            document.documentElement.classList.add('theme-transition');

            // Add CSS for smooth transitions
            if (!document.getElementById('theme-transition-style')) {
                const style = document.createElement('style');
                style.id = 'theme-transition-style';
                style.textContent = `
                    .theme-transition,
                    .theme-transition *,
                    .theme-transition *:before,
                    .theme-transition *:after {
                        transition: background-color 0.3s ease,
                                    border-color 0.3s ease,
                                    color 0.3s ease,
                                    box-shadow 0.3s ease !important;
                        transition-delay: 0 !important;
                    }
                `;
                document.head.appendChild(style);
            }
        },

        /**
         * Update toggle button icon based on current theme
         */
        updateToggleIcon(theme) {
            const toggles = document.querySelectorAll('.theme-toggle');
            toggles.forEach(toggle => {
                const icon = toggle.querySelector('i');
                if (icon) {
                    if (theme === 'dark') {
                        icon.className = 'fas fa-sun';
                        toggle.setAttribute('title', 'Chuyển sang chế độ sáng');
                    } else {
                        icon.className = 'fas fa-moon';
                        toggle.setAttribute('title', 'Chuyển sang chế độ tối');
                    }
                }
            });
        },

        /**
         * Attach click listeners to all theme toggle buttons
         */
        attachEventListeners() {
            document.addEventListener('click', (e) => {
                if (e.target.closest('.theme-toggle')) {
                    e.preventDefault();
                    this.toggleTheme();
                }
            });
        }
    };

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => ThemeManager.init());
    } else {
        ThemeManager.init();
    }

    // Export globally
    window.ThemeManager = ThemeManager;

})();
