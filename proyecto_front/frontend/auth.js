// Configuración de la API
const API_URL = "http://localhost:8080/auth";

// Clase para manejar la autenticación
class AuthManager {
    constructor() {
        this.initializeForms();
        this.initializeValidation();
    }

    // Inicializar validación Bootstrap
    initializeValidation() {
        const forms = document.querySelectorAll("form");
        Array.from(forms).forEach((form) => {
            form.addEventListener(
                "submit",
                function (event) {
                    if (!form.checkValidity()) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    form.classList.add("was-validated");
                },
                false
            );
        });
    }

    // Inicializar todos los formularios
    initializeForms() {
        this.setupLoginForm();
        this.setupRegisterForm();
        this.setupRegisterAdminForm();
    }

    // Configurar formulario de login
    setupLoginForm() {
        const loginForm = document.getElementById("loginForm");
        if (!loginForm) return;

        loginForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            if (!loginForm.checkValidity()) return;

            const email = document.getElementById("loginEmail").value;
            const password = document.getElementById("loginPassword").value;
            const submitBtn = loginForm.querySelector('button[type="submit"]');
            const msg = document.getElementById("loginMsg");

            // Mostrar estado de carga
            this.setLoadingState(submitBtn, true);
            msg.innerHTML = "";

            try {
                const response = await this.makeRequest(`${API_URL}/login`, {
                    method: "POST",
                    body: JSON.stringify({ email, password }),
                });

                if (response.ok) {
                    const data = await response.json();
                    this.saveUserData(data, email);
                    this.showSuccessMessage(msg, "¡Inicio de sesión exitoso! Redirigiendo...");
                    
                    // Redirigir después de un breve delay
                    setTimeout(() => {
                        window.location.href = "panel.html";
                    }, 1500);
                } else {
                    const error = await response.text();
                    this.showErrorMessage(msg, error);
                }
            } catch (err) {
                this.showErrorMessage(msg, "Error de conexión. Verifica tu conexión a internet.");
            } finally {
                this.setLoadingState(submitBtn, false);
            }
        });
    }

    // Configurar formulario de registro de usuario
    setupRegisterForm() {
        const registerForm = document.getElementById("registerForm");
        if (!registerForm) return;

        registerForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            if (!registerForm.checkValidity()) return;

            const email = document.getElementById("registerEmail").value;
            const password = document.getElementById("registerPassword").value;
            const submitBtn = registerForm.querySelector('button[type="submit"]');
            const msg = document.getElementById("registerMsg");

            this.setLoadingState(submitBtn, true);
            msg.innerHTML = "";

            try {
                const response = await this.makeRequest(`${API_URL}/register`, {
                    method: "POST",
                    body: JSON.stringify({ email, password }),
                });

                if (response.ok) {
                    this.showSuccessMessage(msg, "Usuario registrado correctamente. Ahora puedes iniciar sesión.");
                    this.resetForm(registerForm);
                } else {
                    const error = await response.text();
                    this.showErrorMessage(msg, error);
                }
            } catch (err) {
                this.showErrorMessage(msg, "Error de conexión. Verifica tu conexión a internet.");
            } finally {
                this.setLoadingState(submitBtn, false);
            }
        });
    }

    // Configurar formulario de registro de admin
    setupRegisterAdminForm() {
        const registerAdminForm = document.getElementById("registerAdminForm");
        if (!registerAdminForm) return;

        registerAdminForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            if (!registerAdminForm.checkValidity()) return;

            const email = document.getElementById("registerAdminEmail").value;
            const password = document.getElementById("registerAdminPassword").value;
            const submitBtn = registerAdminForm.querySelector('button[type="submit"]');
            const msg = document.getElementById("registerAdminMsg");

            this.setLoadingState(submitBtn, true);
            msg.innerHTML = "";

            try {
                const response = await this.makeRequest(`${API_URL}/register/admin`, {
                    method: "POST",
                    body: JSON.stringify({ email, password }),
                });

                if (response.ok) {
                    this.showSuccessMessage(msg, "Admin registrado correctamente. Ahora puedes iniciar sesión.");
                    this.resetForm(registerAdminForm);
                } else {
                    const error = await response.text();
                    this.showErrorMessage(msg, error);
                }
            } catch (err) {
                this.showErrorMessage(msg, "Error de conexión. Verifica tu conexión a internet.");
            } finally {
                this.setLoadingState(submitBtn, false);
            }
        });
    }

    // Realizar petición HTTP
    async makeRequest(url, options = {}) {
        const defaultOptions = {
            headers: { "Content-Type": "application/json" },
            ...options
        };

        return fetch(url, defaultOptions);
    }

    // Guardar datos del usuario en localStorage
    saveUserData(data, email) {
        localStorage.setItem("jwt", data.token);
        localStorage.setItem("role", data.role);
        localStorage.setItem("email", email);
    }

    // Mostrar mensaje de éxito
    showSuccessMessage(container, message) {
        container.innerHTML = `<div class='alert alert-success'>
            <i class="fas fa-check-circle me-2"></i>${message}
        </div>`;
    }

    // Mostrar mensaje de error
    showErrorMessage(container, message) {
        container.innerHTML = `<div class='alert alert-danger'>
            <i class="fas fa-exclamation-circle me-2"></i>${message}
        </div>`;
    }

    // Establecer estado de carga en botón
    setLoadingState(button, isLoading) {
        if (isLoading) {
            button.classList.add('loading');
            button.disabled = true;
            button.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';
        } else {
            button.classList.remove('loading');
            button.disabled = false;
            // Restaurar texto original del botón
            const originalText = button.getAttribute('data-original-text') || 'Enviar';
            button.innerHTML = originalText;
        }
    }

    // Resetear formulario
    resetForm(form) {
        form.reset();
        form.classList.remove("was-validated");
        
        // Limpiar mensajes de validación
        const invalidInputs = form.querySelectorAll('.is-invalid');
        invalidInputs.forEach(input => {
            input.classList.remove('is-invalid');
        });
    }

    // Validar email
    validateEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    // Validar contraseña
    validatePassword(password) {
        return password.length >= 4;
    }
}

// Utilidades adicionales
class Utils {
    // Mostrar notificación toast
    static showToast(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `toast align-items-center text-white bg-${type} border-0`;
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'assertive');
        toast.setAttribute('aria-atomic', 'true');
        
        toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        `;
        
        const container = document.getElementById('toast-container') || document.body;
        container.appendChild(toast);
        
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();
        
        // Remover del DOM después de que se oculte
        toast.addEventListener('hidden.bs.toast', () => {
            container.removeChild(toast);
        });
    }

    // Verificar si el usuario está autenticado
    static isAuthenticated() {
        return localStorage.getItem('jwt') !== null;
    }

    // Obtener token JWT
    static getToken() {
        return localStorage.getItem('jwt');
    }

    // Obtener rol del usuario
    static getUserRole() {
        return localStorage.getItem('role');
    }

    // Cerrar sesión
    static logout() {
        localStorage.removeItem('jwt');
        localStorage.removeItem('role');
        localStorage.removeItem('email');
        window.location.href = 'index.html';
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    // Inicializar el gestor de autenticación
    const authManager = new AuthManager();
    
    // Guardar referencias globales para uso en otros archivos
    window.AuthManager = AuthManager;
    window.Utils = Utils;
    
    // Agregar efectos visuales adicionales
    addVisualEffects();
});

// Efectos visuales adicionales
function addVisualEffects() {
    // Efecto de hover en los campos de formulario
    const formControls = document.querySelectorAll('.form-control');
    formControls.forEach(control => {
        control.addEventListener('focus', function() {
            this.parentElement.style.transform = 'scale(1.02)';
        });
        
        control.addEventListener('blur', function() {
            this.parentElement.style.transform = 'scale(1)';
        });
    });

    // Efecto de ripple en botones
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(button => {
        button.addEventListener('click', function(e) {
            const ripple = document.createElement('span');
            const rect = this.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            const x = e.clientX - rect.left - size / 2;
            const y = e.clientY - rect.top - size / 2;
            
            ripple.style.width = ripple.style.height = size + 'px';
            ripple.style.left = x + 'px';
            ripple.style.top = y + 'px';
            ripple.classList.add('ripple');
            
            this.appendChild(ripple);
            
            setTimeout(() => {
                ripple.remove();
            }, 600);
        });
    });
} 