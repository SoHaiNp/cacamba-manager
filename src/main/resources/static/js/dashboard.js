document.addEventListener('DOMContentLoaded', function() {
  const noDataPlugin = {
    id: 'noData',
    afterDraw(chart, args, opts) {
      if (!opts || !opts.show) return;
      const { ctx, chartArea } = chart; if (!chartArea) return;
      const x = (chartArea.left + chartArea.right) / 2;
      const y = (chartArea.top + chartArea.bottom) / 2;
      ctx.save();
      ctx.fillStyle = '#6c757d';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.font = 'bold 12px system-ui, -apple-system, Segoe UI, Roboto, Arial';
      ctx.fillText((opts && opts.text) || 'Sem dados', x, y);
      ctx.restore();
    }
  };

  // Donut: Status caçambas
  try {
    var chartEl = document.getElementById('chartCacambas');
    var disp = Number((chartEl && chartEl.getAttribute('data-disp')) || 0);
    var alug = Number((chartEl && chartEl.getAttribute('data-alug')) || 0);
    if (chartEl && window.Chart) {
      var sum = (disp || 0) + (alug || 0);
      var donut = new Chart(chartEl, {
        type: 'doughnut',
        plugins: [noDataPlugin],
        data: sum > 0 ? {
          labels: ['Disponível', 'Alugada'],
          datasets: [{ data: [disp, alug], backgroundColor: ['#0c7734', '#e1c233'], borderWidth: 0 }]
        } : {
          labels: ['Sem dados'],
          datasets: [{ data: [1], backgroundColor: ['#e9ecef'], borderWidth: 0 }]
        },
        options: {
          plugins: {
            legend: { position: 'bottom', labels: { boxWidth: 12, boxHeight: 12, padding: 10, font: { size: 11 } } },
            tooltip: { enabled: sum > 0 },
            noData: { show: sum === 0, text: 'Sem dados' }
          },
          cutout: '65%'
        }
      });
      document.addEventListener('cm-theme-changed', function() { try { donut.update(); } catch(_){} });
    }
  } catch (e) {}

  // Barras: Vencimentos
  try {
    var barrasEl = document.getElementById('chartVencimentos');
    if (barrasEl && window.Chart) {
      var hoje = Number((barrasEl.getAttribute('data-hoje')) || 0);
      var amanha = Number((barrasEl.getAttribute('data-amanha')) || 0);
      var proximos = Number((barrasEl.getAttribute('data-proximos')) || 0);
      var sumBars = (hoje || 0) + (amanha || 0) + (proximos || 0);
      var barras = new Chart(barrasEl, {
        type: 'bar',
        plugins: [noDataPlugin],
        data: {
          labels: ['Hoje', 'Amanhã', 'Próximos dias'],
          datasets: [{ label: 'Quantidade', data: [hoje, amanha, proximos], backgroundColor: ['#dc3545', '#e1c233', '#265988'], borderWidth: 0, borderRadius: 6 }]
        },
        options: {
          responsive: true,
          plugins: { legend: { display: false }, noData: { show: sumBars === 0, text: 'Sem dados' } },
          scales: {
            y: { beginAtZero: true, suggestedMax: sumBars === 0 ? 1 : undefined, ticks: { stepSize: 1, font: { size: 11 } } },
            x: { ticks: { font: { size: 11 } } }
          }
        }
      });
      document.addEventListener('cm-theme-changed', function() { try { barras.update(); } catch(_){} });
    }
  } catch (e) {}
});


