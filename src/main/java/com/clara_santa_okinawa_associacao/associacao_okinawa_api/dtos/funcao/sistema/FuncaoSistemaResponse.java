package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({
        "id",
        "nome",
        "descricao"
})

@Schema(description = "Dados que representam a resposta de uma requisição")
public class FuncaoSistemaResponse {

    @Schema(description = "Identificador único de uma função do sistema", example = "2")
    private Integer id;

    @Schema(description = "Nome da função", example = "Diretoria")
    private String nome;

    @Schema(description = "Descrição da função", example = "Função responsável por toda a parte " +
            "administrativa do sistema")
    private String descricao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
