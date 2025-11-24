/**
 * Client-Side Form Validation
 * Provides real-time validation feedback matching backend validation rules
 */

(function () {
    'use strict';

    const Validator = {
        /**
         * Validation rules matching backend constraints
         */
        rules: {
            required: (value) => value && value.trim().length > 0,
            email: (value) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value),
            minLength: (value, min) => value.length >= min,
            maxLength: (value, max) => value.length <= max,
            min: (value, min) => Number(value) >= min,
            max: (value, max) => Number(value) <= max,
            pattern: (value, pattern) => new RegExp(pattern).test(value)
        },

        /**
         * Error messages in Vietnamese
         */
        messages: {
            required: 'Trường này không được để trống',
            email: 'Email không hợp lệ',
            minLength: 'Phải có ít nhất {min} ký tự',
            maxLength: 'Không được vượt quá {max} ký tự',
            min: 'Giá trị phải lớn hơn hoặc bằng {min}',
            max: 'Giá trị phải nhỏ hơn hoặc bằng {max}',
            pattern: 'Định dạng không hợp lệ',
            passwordMatch: 'Mật khẩu xác nhận không khớp'
        },

        /**
         * Validate a single field
         */
        validateField(input) {
            const value = input.value.trim();
            const rules = this.getFieldRules(input);
            let isValid = true;
            let errorMessage = '';

            // Check each rule
            for (const [ruleName, ruleValue] of Object.entries(rules)) {
                if (!this.rules[ruleName]) continue;

                if (ruleName === 'required' && ruleValue) {
                    if (!this.rules.required(value)) {
                        isValid = false;
                        errorMessage = input.dataset.requiredMessage || this.messages.required;
                        break;
                    }
                } else if (value && !this.rules[ruleName](value, ruleValue)) {
                    isValid = false;
                    errorMessage = this.formatMessage(ruleName, ruleValue, input);
                    break;
                }
            }

            // Special case: password confirmation
            if (input.dataset.passwordConfirm) {
                const passwordInput = document.getElementById(input.dataset.passwordConfirm);
                if (passwordInput && value !== passwordInput.value) {
                    isValid = false;
                    errorMessage = this.messages.passwordMatch;
                }
            }

            this.showFeedback(input, isValid, errorMessage);
            return isValid;
        },

        /**
         * Get validation rules from input attributes
         */
        getFieldRules(input) {
            const rules = {};

            if (input.required) rules.required = true;
            if (input.type === 'email') rules.email = true;
            if (input.minLength) rules.minLength = input.minLength;
            if (input.maxLength) rules.maxLength = input.maxLength;
            if (input.min) rules.min = input.min;
            if (input.max) rules.max = input.max;
            if (input.pattern) rules.pattern = input.pattern;

            return rules;
        },

        /**
         * Format error message with parameters
         */
        formatMessage(ruleName, ruleValue, input) {
            let message = input.dataset[`${ruleName}Message`] || this.messages[ruleName];
            return message.replace(`{${ruleName}}`, ruleValue);
        },

        /**
         * Show validation feedback
         */
        showFeedback(input, isValid, errorMessage) {
            const formGroup = input.closest('.mb-3, .form-group');
            if (!formGroup) return;

            // Remove existing feedback
            const existingFeedback = formGroup.querySelector('.invalid-feedback, .valid-feedback');
            if (existingFeedback) existingFeedback.remove();

            // Update input classes
            input.classList.remove('is-valid', 'is-invalid');

            if (!isValid) {
                input.classList.add('is-invalid');
                const feedback = document.createElement('div');
                feedback.className = 'invalid-feedback';
                feedback.textContent = errorMessage;
                input.parentNode.appendChild(feedback);
            } else if (input.value.trim()) {
                input.classList.add('is-valid');
                const feedback = document.createElement('div');
                feedback.className = 'valid-feedback';
                feedback.innerHTML = '<i class="fas fa-check"></i> Hợp lệ';
                input.parentNode.appendChild(feedback);
            }
        },

        /**
         * Validate entire form
         */
        validateForm(form) {
            const inputs = form.querySelectorAll('input[required], input[type="email"], input[data-validate]');
            let isValid = true;

            inputs.forEach(input => {
                if (!this.validateField(input)) {
                    isValid = false;
                }
            });

            return isValid;
        },

        /**
         * Initialize validation for a form
         */
        initForm(form) {
            const inputs = form.querySelectorAll('input, textarea, select');

            inputs.forEach(input => {
                // Validate on blur
                input.addEventListener('blur', () => {
                    if (input.value.trim() || input.classList.contains('is-invalid')) {
                        this.validateField(input);
                    }
                });

                // Clear validation on focus
                input.addEventListener('focus', () => {
                    input.classList.remove('is-invalid', 'is-valid');
                    const feedback = input.parentNode.querySelector('.invalid-feedback, .valid-feedback');
                    if (feedback) feedback.remove();
                });

                // Real-time validation for password confirmation
                if (input.dataset.passwordConfirm) {
                    input.addEventListener('input', () => {
                        if (input.value) {
                            this.validateField(input);
                        }
                    });
                }
            });

            // Validate on submit
            form.addEventListener('submit', (e) => {
                if (!this.validateForm(form)) {
                    e.preventDefault();
                    e.stopPropagation();

                    // Focus first invalid field
                    const firstInvalid = form.querySelector('.is-invalid');
                    if (firstInvalid) {
                        firstInvalid.focus();
                        firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    }

                    // Show error toast
                    if (window.KTXApp) {
                        window.KTXApp.utils.showToast('Vui lòng kiểm tra lại thông tin', 'error');
                    }
                }
            });
        },

        /**
         * Initialize all forms on page
         */
        init() {
            document.querySelectorAll('form[data-validate="true"]').forEach(form => {
                this.initForm(form);
            });
        }
    };

    // Initialize when DOM is ready
    document.addEventListener('DOMContentLoaded', () => {
        Validator.init();
    });

    // Export globally
    window.KTXValidator = Validator;

})();
