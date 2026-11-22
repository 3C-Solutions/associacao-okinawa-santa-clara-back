package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

public enum EspacoEvento {
    COZINHA("Cozinha"),
    ESPACO_EXTERNO("Espaço externo"),
    PALCO("Palco"),
    SALAO("Salão"),
    ESPACO_COMPLETO("Espaço completo");

    private final String descricao;
    EspacoEvento(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }

    public boolean conflitaCom(EspacoEvento outro) {
        return this == ESPACO_COMPLETO || outro == ESPACO_COMPLETO || this == outro;
    }
}