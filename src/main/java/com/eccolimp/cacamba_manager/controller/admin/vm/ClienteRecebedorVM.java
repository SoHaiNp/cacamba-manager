package com.eccolimp.cacamba_manager.controller.admin.vm;

public class ClienteRecebedorVM {
    private final Long id;
    private final String nome;
    private final String email;
    private final boolean recebeNotificacoes;

    public ClienteRecebedorVM(Long id, String nome, String email, boolean recebeNotificacoes) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.recebeNotificacoes = recebeNotificacoes;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public boolean isRecebeNotificacoes() {
        return recebeNotificacoes;
    }
}


