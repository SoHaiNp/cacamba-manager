console.log('Caçamba Manager frontend carregado');

// Inicializar tooltips do Bootstrap
document.addEventListener('DOMContentLoaded', function() {
    // Inicializar tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Inicializar popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // Auto-hide alerts após 5 segundos
    var alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Melhorar confirmações de exclusão
    var deleteButtons = document.querySelectorAll('button[onclick*="confirm"]');
    deleteButtons.forEach(function(button) {
        button.addEventListener('click', function(e) {
            if (!confirm('Tem certeza que deseja realizar esta ação?')) {
                e.preventDefault();
                return false;
            }
        });
    });

    // Adicionar loading state e validação em formulários
    var forms = document.querySelectorAll('form');
    forms.forEach(function(form) {
        form.addEventListener('submit', function(e) {
            // Validação client-side
            if (!form.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
                
                // Mostrar mensagens de erro
                form.classList.add('was-validated');
                
                // Focar no primeiro campo inválido
                var firstInvalid = form.querySelector(':invalid');
                if (firstInvalid) {
                    firstInvalid.focus();
                    firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
                
                // Mostrar notificação
                showNotification('Por favor, preencha todos os campos obrigatórios corretamente.', 'warning');
                return false;
            }
            
            // Se passou na validação, adicionar loading state
            var submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.classList.add('btn-loading');
                submitBtn.disabled = true;
                
                // Remover loading state após 10 segundos (timeout de segurança)
                setTimeout(function() {
                    if (submitBtn.classList.contains('btn-loading')) {
                        submitBtn.classList.remove('btn-loading');
                        submitBtn.disabled = false;
                    }
                }, 10000);
            }
        });
        
        // Validação em tempo real
        var inputs = form.querySelectorAll('input, select, textarea');
        inputs.forEach(function(input) {
            input.addEventListener('blur', function() {
                validateField(input);
            });
            
            input.addEventListener('input', function() {
                if (input.classList.contains('is-invalid')) {
                    validateField(input);
                }
            });
        });
    });
    
    // Remover loading state de botões ao carregar a página
    var loadingButtons = document.querySelectorAll('.btn-loading');
    loadingButtons.forEach(function(button) {
        button.classList.remove('btn-loading');
        button.disabled = false;
    });

    // (Dark mode removido)
    bindActiveNavLink();
});

// Função para mostrar notificações
function showNotification(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(alertDiv);
    
    // Auto-remove após 5 segundos
    setTimeout(function() {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}

// Função para formatar números
function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
}

// Função para formatar datas
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
}

// Função para validar telefone
function validatePhone(phone) {
    const phoneRegex = /^\(?([0-9]{2})\)?[-. ]?([0-9]{4,5})[-. ]?([0-9]{4})$/;
    return phoneRegex.test(phone);
}

// Função para validar CPF
function validateCPF(cpf) {
    cpf = cpf.replace(/[^\d]/g, '');
    
    if (cpf.length !== 11) return false;
    
    // Verifica se todos os dígitos são iguais
    if (/^(\d)\1{10}$/.test(cpf)) return false;
    
    // Validação do primeiro dígito verificador
    let sum = 0;
    for (let i = 0; i < 9; i++) {
        sum += parseInt(cpf.charAt(i)) * (10 - i);
    }
    let remainder = sum % 11;
    let digit1 = remainder < 2 ? 0 : 11 - remainder;
    
    // Validação do segundo dígito verificador
    sum = 0;
    for (let i = 0; i < 10; i++) {
        sum += parseInt(cpf.charAt(i)) * (11 - i);
    }
    remainder = sum % 11;
    let digit2 = remainder < 2 ? 0 : 11 - remainder;
    
    return parseInt(cpf.charAt(9)) === digit1 && parseInt(cpf.charAt(10)) === digit2;
}

// Função para validar campos individuais
function validateField(field) {
    var isValid = true;
    var errorMessage = '';
    
    // Remover classes de validação anteriores
    field.classList.remove('is-valid', 'is-invalid');
    
    // Validações específicas por tipo de campo
    if (field.hasAttribute('required') && !field.value.trim()) {
        isValid = false;
        errorMessage = 'Este campo é obrigatório.';
    } else if (field.type === 'email' && field.value) {
        var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(field.value)) {
            isValid = false;
            errorMessage = 'Por favor, informe um e-mail válido.';
        }
    } else if (field.type === 'number') {
        if (field.value && (isNaN(field.value) || field.value < field.min || field.value > field.max)) {
            isValid = false;
            errorMessage = `Por favor, informe um valor entre ${field.min} e ${field.max}.`;
        }
    } else if (field.type === 'tel' && field.value) {
        if (!validatePhone(field.value)) {
            isValid = false;
            errorMessage = 'Por favor, informe um telefone válido.';
        }
    }
    
    // Aplicar classes de validação
    if (isValid && field.value.trim()) {
        field.classList.add('is-valid');
    } else if (!isValid) {
        field.classList.add('is-invalid');
    }
    
    return isValid;
}

// (Funções de tema removidas)

// -------- Ativar link do menu atual ---------
function bindActiveNavLink() {
    var navLinks = document.querySelectorAll('.navbar-nav .nav-link');
    var current = window.location.pathname;
    navLinks.forEach(function(link) {
        var href = link.getAttribute('href') || link.getAttribute('th:href');
        if (!href) return;
        // Considera caminhos base
        if (current === '/' && href.endsWith('/ui')) {
            link.classList.add('active');
        } else if (current.startsWith(href.replace(/\{.*\}/, ''))) {
            link.classList.add('active');
        }
    });
}