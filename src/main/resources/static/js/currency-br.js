(function (global) {
  // Formata e parseia valores em BRL enquanto o usuário digita.
  // Mantém a mesma API: CurrencyBR.bind(selector), .parse(str), .format(num)

  const formatter = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

  function parseBRL(value) {
    if (typeof value !== 'string') return 0;
    let raw = value.trim();
    if (!raw) return 0;
    // Remove símbolo, espaços (inclui NBSP) e tudo que não for dígito/ponto/vírgula
    raw = raw.replace(/[R$\s\u00A0]/g, '');
    raw = raw.replace(/[^0-9,\.]/g, '');

    const hasComma = raw.indexOf(',') >= 0;
    const hasDot = raw.indexOf('.') >= 0;

    let number = 0;
    if (hasComma && hasDot) {
      number = parseFloat(raw.replace(/\./g, '').replace(',', '.')) || 0;
    } else if (hasComma) {
      number = parseFloat(raw.replace(/\./g, '').replace(',', '.')) || 0;
    } else if (hasDot) {
      const lastDot = raw.lastIndexOf('.');
      const fracLen = raw.length - lastDot - 1;
      if (lastDot >= 0 && fracLen > 0 && fracLen <= 2) {
        number = parseFloat(raw) || 0;
      } else {
        number = parseFloat(raw.replace(/\./g, '')) || 0;
      }
    } else {
      // Apenas dígitos: trate como unidades inteiras (1000 => 1000.00)
      const onlyDigits = raw.replace(/[^0-9]/g, '');
      number = parseInt(onlyDigits || '0', 10);
    }
    return Math.round(number * 100) / 100;
  }

  function formatBRLNumber(num) {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(isNaN(num) ? 0 : num);
  }

  function cleanStr(str) {
    return (str || '').replace(/[^\d,.\sR$\u00A0]/g, '');
  }

  function digitsOnly(str) {
    return (str || '').replace(/\D/g, '');
  }

  function formatFromDigits(digits) {
    if (!digits) return '';
    // Interpreta dígitos como centavos. Ex.: '1' => 0.01; '150' => 1.50
    const cents = parseInt(digits, 10);
    const value = isNaN(cents) ? 0 : (cents / 100);
    return formatBRLNumber(value);
  }

  function setCaretToEnd(el) {
    try {
      const len = el.value.length;
      el.setSelectionRange(len, len);
    } catch (_) {}
  }

  function onKeyDown(e) {
    const input = e.target;
    const key = e.key;
    const ctrl = e.ctrlKey || e.metaKey;

    // Permitir atalhos e navegação
    if (ctrl || key === 'Tab' || key === 'Escape' || key === 'ArrowLeft' || key === 'ArrowRight' || key === 'Home' || key === 'End') {
      return;
    }

    // Dígitos
    if (/^[0-9]$/.test(key)) {
      e.preventDefault();
      const digits = digitsOnly(input.dataset.cbrDigits || '');
      const next = digits + key;
      input.dataset.cbrDigits = next;
      input.value = formatFromDigits(next);
      setCaretToEnd(input);
      return;
    }

    // Backspace / Delete
    if (key === 'Backspace' || key === 'Delete') {
      e.preventDefault();
      const digits = digitsOnly(input.dataset.cbrDigits || '');
      const next = digits.slice(0, -1);
      input.dataset.cbrDigits = next;
      input.value = formatFromDigits(next);
      setCaretToEnd(input);
      return;
    }

    // Bloquear demais caracteres (vírgula, ponto, letras, etc.)
    e.preventDefault();
  }

  function onBlur(e) {
    const input = e.target;
    const digits = digitsOnly(input.dataset.cbrDigits || '');
    input.value = formatFromDigits(digits);
  }

  function onPaste(e) {
    // Converte conteúdo colado para dígitos e formata como centavos
    e.preventDefault();
    const text = (e.clipboardData || window.clipboardData).getData('text') || '';
    const onlyDigits = digitsOnly(text);
    const input = e.target;
    input.dataset.cbrDigits = onlyDigits;
    input.value = formatFromDigits(onlyDigits);
    setCaretToEnd(input);
  }

  function bind(selector) {
    document.querySelectorAll(selector).forEach(function (input) {
      if (input.dataset.currencyBrBound === 'true') return;
      // Atributos recomendados para UX mobile
      if (!input.hasAttribute('inputmode')) input.setAttribute('inputmode', 'decimal');
      if (!input.hasAttribute('autocomplete')) input.setAttribute('autocomplete', 'off');

      // Formata o valor inicial (se houver)
      const trimmed = (input.value || '').trim();
      if (trimmed) {
        const initial = parseBRL(trimmed);
        input.value = formatBRLNumber(initial);
      } else {
        input.value = '';
      }

      // Eventos
      input.addEventListener('keydown', onKeyDown);
      input.addEventListener('blur', onBlur);
      input.addEventListener('paste', onPaste);

      // No submit: envia um hidden com o mesmo name em decimal (ex.: 1234.56)
      const form = input.form;
      if (form && input.dataset.currencyBrFormBound !== 'true') {
        form.addEventListener('submit', function () {
          // Converte os dígitos (centavos) para decimal com 2 casas
          const digits = digitsOnly(input.dataset.cbrDigits || '');
          const amount = digits ? (parseInt(digits, 10) / 100) : NaN;
          let hidden = form.querySelector('input[type="hidden"][name="' + input.name + '"]');
          if (!hidden) {
            hidden = document.createElement('input');
            hidden.type = 'hidden';
            hidden.name = input.name;
            form.appendChild(hidden);
          }
          hidden.value = isNaN(amount) ? '' : (amount.toFixed(2));
          // Renomeia o campo visível para evitar conflito de binding
          input.name = input.name + '_display';
        });
        input.dataset.currencyBrFormBound = 'true';
      }
      input.dataset.currencyBrBound = 'true';
    });
  }

  global.CurrencyBR = { bind, parse: parseBRL, format: formatBRLNumber };
  // Auto-bind ao carregar DOM e para nós adicionados dinamicamente
  function autoBind() {
    try { bind('input.currency-br:not([data-currency-br-bound])'); } catch (e) {}
  }
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function(){
      bind('input.currency-br');
      new MutationObserver(function(){
        bind('input.currency-br:not([data-currency-br-bound])');
      }).observe(document.body, { childList: true, subtree: true });
    });
  } else {
    bind('input.currency-br');
    new MutationObserver(function(){
      bind('input.currency-br:not([data-currency-br-bound])');
    }).observe(document.body, { childList: true, subtree: true });
  }
})(window);