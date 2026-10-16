package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoResponse;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({
        "id",
        "nome",
        "endereco"
})

@Schema(description = "Dados que serão devolvidos como resposta de um requisição")
public class FamiliaResponse {

    @Schema(description = "Identificador único da família", example = "1")
    private Integer id;

    @Schema(description = "Nome da família", example = "Silva")
    private String nome;

    @Schema(description = "Dados do endereço da família")
    private EnderecoResponse endereco;

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

    public EnderecoResponse getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoResponse endereco) {
        this.endereco = endereco;
    }
}
