package com.eccolimp.cacamba_manager.domain.repository.spec;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;
import com.eccolimp.cacamba_manager.domain.model.StatusAluguel;

public final class AluguelSpecifications {

    private AluguelSpecifications() {}

    public static Specification<Aluguel> statusEquals(StatusAluguel status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Aluguel> clienteNomeLike(String nome) {
        return (root, query, cb) -> {
            if (nome == null || nome.isBlank()) return null;
            return cb.like(cb.lower(root.get("cliente").get("nome")), "%" + nome.toLowerCase() + "%");
        };
    }

    public static Specification<Aluguel> cacambaCodigoLike(String codigo) {
        return (root, query, cb) -> {
            if (codigo == null || codigo.isBlank()) return null;
            return cb.like(cb.lower(root.get("cacamba").get("codigo")), "%" + codigo.toLowerCase() + "%");
        };
    }

    public static Specification<Aluguel> dataInicioBetween(LocalDate de, LocalDate ate) {
        return (root, query, cb) -> {
            if (de == null && ate == null) return null;
            if (de != null && ate != null) return cb.between(root.get("dataInicio"), de, ate);
            if (de != null) return cb.greaterThanOrEqualTo(root.get("dataInicio"), de);
            return cb.lessThanOrEqualTo(root.get("dataInicio"), ate);
        };
    }

    public static Specification<Aluguel> dataFimBetween(LocalDate de, LocalDate ate) {
        return (root, query, cb) -> {
            if (de == null && ate == null) return null;
            if (de != null && ate != null) return cb.between(root.get("dataFim"), de, ate);
            if (de != null) return cb.greaterThanOrEqualTo(root.get("dataFim"), de);
            return cb.lessThanOrEqualTo(root.get("dataFim"), ate);
        };
    }

    public static Specification<Aluguel> venceHoje() {
        return (root, query, cb) -> cb.and(
            cb.equal(root.get("status"), StatusAluguel.ATIVO),
            cb.equal(root.get("dataFim"), LocalDate.now())
        );
    }

    public static Specification<Aluguel> vencidos() {
        return (root, query, cb) -> cb.and(
            cb.equal(root.get("status"), StatusAluguel.ATIVO),
            cb.lessThan(root.get("dataFim"), LocalDate.now())
        );
    }

    public static Specification<Aluguel> proximos7Dias() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(7);
        return (root, query, cb) -> cb.and(
            cb.equal(root.get("status"), StatusAluguel.ATIVO),
            cb.between(root.get("dataFim"), hoje.plusDays(1), limite)
        );
    }

    public static Specification<Aluguel> comAtraso() {
        return (root, query, cb) -> cb.and(
            cb.equal(root.get("status"), StatusAluguel.ATIVO),
            cb.lessThan(root.get("dataFim"), LocalDate.now())
        );
    }

    public static Specification<Aluguel> textoLivre(String texto) {
        return (root, query, cb) -> {
            if (texto == null || texto.isBlank()) return null;
            String like = "%" + texto.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("endereco")), like),
                cb.like(cb.lower(root.get("cliente").get("nome")), like),
                cb.like(cb.lower(root.get("cacamba").get("codigo")), like)
            );
        };
    }
}


