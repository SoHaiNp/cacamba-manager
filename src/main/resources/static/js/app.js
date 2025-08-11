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

    // --- Cálculo de Data de Fim (form novo aluguel) ---
    bindContractEndDateCalculation();

    // --- Cálculo de Data de Fim (modal de renovação) ---
    bindRenewEndDateCalculation();

    // --- Máscara e validação para telefone (suporta fixo e celular) ---
    bindBrazilPhoneMask();

    // Inicialização segura de gráficos no dashboard caso estejam presentes
    try { initDashboardChartsFallback(); } catch (e) {}
});

function initDashboardChartsFallback() {
    var chartJsLoaded = typeof window.Chart !== 'undefined';
    var donut = document.getElementById('chartCacambas');
    var barras = document.getElementById('chartVencimentos');
    if (!donut && !barras) return; // não está no dashboard

    if (!chartJsLoaded) {
        // Renderiza placeholders amigáveis se Chart.js não carregou
        if (donut) {
            var p1 = document.createElement('div');
            p1.className = 'chart-placeholder';
            p1.textContent = 'Gráfico indisponível';
            donut.replaceWith(p1);
        }
        if (barras) {
            var p2 = document.createElement('div');
            p2.className = 'chart-placeholder';
            p2.textContent = 'Gráfico indisponível';
            barras.replaceWith(p2);
        }
    }
}
// -------- Máscara de Telefone BR (fixo/celular) ---------
function applyBrazilPhoneMaskToValue(raw) {
    var digits = (raw || '').replace(/\D/g, '');
    if (!digits) return '';
    // Limita ao máximo de 11 dígitos
    digits = digits.slice(0, 11);
    var ddd = digits.slice(0, 2);
    var rest = digits.slice(2);
    if (rest.length <= 4) {
        return ddd ? (ddd + ' ' + rest) : rest;
    }
    if (rest.length <= 9) {
        // Decide se é fixo (8 dígitos => 4+4) ou celular (9 dígitos => 5+4)
        var firstBlockLen = (rest.length === 9) ? 5 : 4;
        var first = rest.slice(0, firstBlockLen);
        var last = rest.slice(firstBlockLen);
        return ddd + ' ' + first + '-' + last;
    }
    // Fallback (não deve ocorrer com limite de 11 dígitos)
    return ddd + ' ' + rest;
}

function bindBrazilPhoneMask() {
    try {
        var inputs = document.querySelectorAll('input.tel-br');
        inputs.forEach(function(input) {
            // Formata valor inicial
            if (input.value) input.value = applyBrazilPhoneMaskToValue(input.value);
            input.addEventListener('input', function() {
                var pos = input.selectionStart;
                var before = input.value;
                input.value = applyBrazilPhoneMaskToValue(before);
            });
            input.addEventListener('blur', function() {
                // Revalida no blur
                validateField(input);
            });
        });
    } catch (e) {}
}

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
    const digits = (phone || '').replace(/\D/g, '');
    // Aceita 10 (fixo) ou 11 (celular) dígitos
    return digits.length === 10 || digits.length === 11;
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
        if (field.value) {
            var num = Number(field.value);
            var min = field.min !== '' ? Number(field.min) : -Infinity;
            var max = field.max !== '' ? Number(field.max) : Infinity;
            if (Number.isNaN(num) || num < min || num > max) {
                isValid = false;
                errorMessage = `Por favor, informe um valor entre ${field.min} e ${field.max}.`;
            }
        }
    } else if (field.type === 'tel' && field.value) {
        if (!validatePhone(field.value)) {
            isValid = false;
            errorMessage = 'Por favor, informe um telefone válido (10 ou 11 dígitos).';
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

// -------- Cálculo de Data de Fim (Novo Aluguel) ---------
function parseDateInputValue(el) {
    if (!el) return null;
    if (el.type === 'date') {
        if (el.value) {
            // yyyy-mm-dd (não usar valueAsDate para evitar problemas de fuso)
            var parts = el.value.split('-');
            if (parts.length === 3) return new Date(Number(parts[0]), Number(parts[1]) - 1, Number(parts[2]));
        }
        return null;
    }
    // fallback dd/mm/yyyy
    if (el.value && el.value.includes('/')) {
        var p = el.value.split('/');
        if (p.length === 3) return new Date(Number(p[2]), Number(p[1]) - 1, Number(p[0]));
    }
    return null;
}

function formatDateForInput(date) {
    var y = date.getFullYear();
    var m = String(date.getMonth() + 1).padStart(2, '0');
    var d = String(date.getDate()).padStart(2, '0');
    return y + '-' + m + '-' + d;
}

function computeEndDate() {
    var startEl = document.getElementById('dataInicio');
    var daysEl = document.getElementById('dias');
    var outEl = document.getElementById('dataFimPreview');
    if (!startEl || !daysEl || !outEl) return;

    var start = parseDateInputValue(startEl);
    var days = Number(daysEl.value);
    if (!(start instanceof Date) || isNaN(start) || !days || days <= 0) {
        outEl.value = '';
        return;
    }
    var end = new Date(start.getFullYear(), start.getMonth(), start.getDate() + (days - 1));
    outEl.value = formatDateForInput(end);
}

function bindContractEndDateCalculation() {
    var startEl = document.getElementById('dataInicio');
    var daysEl = document.getElementById('dias');
    var outEl = document.getElementById('dataFimPreview');
    if (!startEl || !daysEl || !outEl) return;

    var trigger = function() { requestAnimationFrame(computeEndDate); };
    startEl.addEventListener('input', trigger);
    startEl.addEventListener('change', trigger);
    daysEl.addEventListener('input', trigger);
    daysEl.addEventListener('change', trigger);
    // calcula no load
    trigger();

    // observa mudanças dinâmicas
    try {
        new MutationObserver(trigger).observe(startEl, { attributes: true, attributeFilter: ['value'] });
        new MutationObserver(trigger).observe(daysEl, { attributes: true, attributeFilter: ['value'] });
    } catch (e) {}
}

// -------- Cálculo de Data de Fim no Modal de Renovação ---------
function parseDateStringYMD(str) {
    var p = (str || '').split('-');
    if (p.length !== 3) return null;
    var y = Number(p[0]); var m = Number(p[1]); var d = Number(p[2]);
    var dt = new Date(y, m - 1, d);
    return isNaN(dt) ? null : dt;
}

function formatDateDDMMYYYY(date) {
    var dd = String(date.getDate()).padStart(2, '0');
    var mm = String(date.getMonth() + 1).padStart(2, '0');
    var yyyy = date.getFullYear();
    return dd + '/' + mm + '/' + yyyy;
}

function computeRenewEndDate() {
    var startEl = document.getElementById('novaDataInicio');
    var daysEl = document.getElementById('dias');
    var outEl = document.getElementById('dataFimPreviewRenovacao');
    if (!startEl || !daysEl || !outEl) return;

    var start = parseDateStringYMD(startEl.value);
    var days = Number(daysEl.value);
    if (!(start instanceof Date) || isNaN(start) || !days || days <= 0) {
        outEl.value = '';
        return;
    }
    var end = new Date(start.getFullYear(), start.getMonth(), start.getDate() + (days - 1));
    outEl.value = formatDateDDMMYYYY(end);
}

function bindRenewEndDateCalculation() {
    var modal = document.getElementById('renovarModal');
    if (!modal) return;
    var startEl = document.getElementById('novaDataInicio');
    var daysEl = document.getElementById('dias');
    var outEl = document.getElementById('dataFimPreviewRenovacao');
    if (!startEl || !daysEl || !outEl) return;

    var trigger = function() { requestAnimationFrame(computeRenewEndDate); };
    startEl.addEventListener('input', trigger);
    startEl.addEventListener('change', trigger);
    daysEl.addEventListener('input', trigger);
    daysEl.addEventListener('change', trigger);

    // calcular ao abrir o modal
    try { modal.addEventListener('shown.bs.modal', trigger); } catch (e) {}

    // calcula inicialmente
    trigger();
}