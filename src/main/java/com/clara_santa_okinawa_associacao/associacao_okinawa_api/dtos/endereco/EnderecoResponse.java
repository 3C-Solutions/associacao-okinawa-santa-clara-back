package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({
        "id",
        "rua",
        "bairro",
        "cidade",
        "numero",
        "uf",
        "cep",
        "complemento"
})

@Schema(description = "Dados responsáveis por devolver uma resposta de uma requisição")
public class EnderecoResponse {

    @Schema(description = "Identificador único do endereco", example = "1")
    private Integer id;

    @Schema(description = "Nome da rua")
    private String rua;

    @Schema(description = "Nome do bairro")
    private String bairro;

    @Schema(description = "Nome da cidade")
    private String cidade;

    @Schema(description = "Número da rua")
    private String numero;

    @Schema(description = "Unidade Federativa", example = "SP")
    private String uf;

    @Schema(description = "CEP")
    private String cep;

    @Schema(description = "Complemento")
    private String complemento;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
}
