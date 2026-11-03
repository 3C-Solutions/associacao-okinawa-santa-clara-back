package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FamiliaRequest {

    @NotBlank
    private String nome;
    @NotNull
    private Integer fkEndereco;

    public FamiliaRequest(String nome, Integer fkEndereco) {
        this.nome = nome;
        this.fkEndereco = fkEndereco;
    }

    public FamiliaRequest() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getFkEndereco() {
        return fkEndereco;
    }

    public void setFkEndereco(Integer fkEndereco) {
        this.fkEndereco = fkEndereco;
    }
}
