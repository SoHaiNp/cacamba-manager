package com.eccolimp.cacamba_manager.domain;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.lang.reflect.Method;
import java.time.LocalDate;

/**
 * Aplica a regra D+1:
 *  - dataInicio = dataEntrega + 1
 *  - dataFim = dataInicio + (prazo - 1)
 * O prazo é resolvido por um dos getters comuns:
 *   getPrazoDias(), getPrazo() ou getDias()
 */
public class AluguelLifecycle {

  @PrePersist
  @PreUpdate
  public void applyDmais1(Aluguel a) {
    if (a == null) return;
    LocalDate entrega = a.getDataEntrega();
    if (entrega == null) return;

    Integer prazo = resolvePrazoDias(a);
    if (prazo == null || prazo <= 0) return;

    LocalDate inicio = entrega.plusDays(1);
    LocalDate fim = inicio.plusDays(Math.max(1, prazo) - 1);

    a.setDataInicio(inicio);
    a.setDataFim(fim);
  }

  private Integer resolvePrazoDias(Aluguel a) {
    Integer v = tryGetter(a, "getPrazoDias");
    if (v == null) v = tryGetter(a, "getPrazo");
    if (v == null) v = tryGetter(a, "getDias");
    return v;
  }

  private Integer tryGetter(Aluguel a, String methodName) {
    try {
      Method m = a.getClass().getMethod(methodName);
      Object r = m.invoke(a);
      if (r instanceof Integer) return (Integer) r;
      if (r instanceof Number) return ((Number) r).intValue();
    } catch (Exception ignored) {}
    return null;
  }
}
