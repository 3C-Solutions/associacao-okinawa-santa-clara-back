package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para cadastrar uma família vinculada a um endereço existente")
public class FamiliaRequest {

    @NotBlank
    @Schema(description = "Nome da família; não pode ser vazio nem conter apenas espaços", example = "Silva",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @NotNull
    @Schema(description = "Identificador de um endereço já cadastrado", example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
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
