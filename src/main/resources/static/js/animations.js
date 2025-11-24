/**
 * Enhanced Animations & Scroll Effects
 * Handles page transitions, scroll reveals, and loading animations
 */

(function () {
    'use strict';

    const AnimationManager = {
        /**
         * Initialize all animations
         */
        init() {
            this.initScrollReveal();
            this.initPageTransition();
            this.initCounterAnimations();
        },

        /**
         * Scroll reveal animation using Intersection Observer
         */
        initScrollReveal() {
            const revealElements = document.querySelectorAll('.scroll-reveal');

            const revealObserver = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        entry.target.classList.add('revealed');
                        revealObserver.unobserve(entry.target);
                    }
                });
            }, {
                threshold: 0.1,
                rootMargin: '0px 0px -50px 0px'
            });

            revealElements.forEach(el => revealObserver.observe(el));
        },

        /**
         * Add fade-in animation to page content
         */
        initPageTransition() {
            const mainContent = document.querySelector('.admin-main-content, .user-main-content, main');
            if (mainContent && !mainContent.classList.contains('page-transition')) {
                mainContent.classList.add('page-transition');
            }
        },

        /**
         * Animated counter for statistics
         */
        initCounterAnimations() {
            const counters = document.querySelectorAll('.stat-count');

            counters.forEach(counter => {
                const target = parseInt(counter.getAttribute('data-target') || counter.textContent);
                const duration = 2000; // 2 seconds
                const step = target / (duration / 16); // 60fps
                let current = 0;

                const updateCounter = () => {
                    current += step;
                    if (current < target) {
                        counter.textContent = Math.floor(current);
                        requestAnimationFrame(updateCounter);
                    } else {
                        counter.textContent = target;
                    }
                };

                // Start animation when visible
                const observer = new IntersectionObserver((entries) => {
                    entries.forEach(entry => {
                        if (entry.isIntersecting) {
                            updateCounter();
                            observer.unobserve(entry.target);
                        }
                    });
                });

                observer.observe(counter);
            });
        },

        /**
         * Add loading state to buttons
         */
        setButtonLoading(button, loading = true) {
            if (loading) {
                button.classList.add('is-loading');
                button.disabled = true;
            } else {
                button.classList.remove('is-loading');
                button.disabled = false;
            }
        },

        /**
         * Show skeleton loader
         */
        showSkeleton(container) {
            container.innerHTML = `
                <div class="skeleton skeleton-title"></div>
                <div class="skeleton skeleton-text"></div>
                <div class="skeleton skeleton-text"></div>
                <div class="skeleton skeleton-text" style="width: 80%;"></div>
            `;
        },

        /**
         * Parallax effect on scroll
         */
        initParallax() {
            const parallaxElements = document.querySelectorAll('.parallax-slow');

            window.addEventListener('scroll', () => {
                const scrolled = window.pageYOffset;

                parallaxElements.forEach((el, index) => {
                    const speed = 0.5;
                    const yPos = -(scrolled * speed * (index + 1) * 0.1);
                    el.style.transform = `translateY(${yPos}px)`;
                });
            });
        }
    };

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => AnimationManager.init());
    } else {
        AnimationManager.init();
    }

    // Export globally
    window.AnimationManager = AnimationManager;

})();
