package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonPropertyOrder({
        "id",
        "nome",
        "email",
        "telefone",
        "dataNascimento",
        "ativo",
        "familia",
        "funcaoSistema",
        "dataCriacao"
})

@Schema(description = "Dados que serão devolvidos como resposta de um requisição")
public class UsuarioResponse {
    @Schema(description = "Identificador único do associado", example = "1")
    private Integer id;

    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    private String nome;

    @Schema(description = "E-mail do usuário", example = "joao@email.com")
    private String email;

    @Schema(description = "Telefone do usuário", example = "(11) 99999-9999")
    private String telefone;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Schema(description = "Data de nascimento do usuário", example = "19/09/1999")
    private LocalDate dataNascimento;

    @Schema(description = "Status do usuário no sistema", example = "true")
    private boolean ativo;

    @Schema(description = "Função que o usuário exerce dentro do sistema")
    private FuncaoSistemaResponse funcaoSistema;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Data de criação do usuário no sistema", example = "30/09/2026")
    private LocalDateTime dataCriacao;

    @Schema(description = "Família a qual o usuário representa na associação")
    private FamiliaResponse familia;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public FuncaoSistemaResponse getFuncaoSistema() {
        return funcaoSistema;
    }

    public void setFuncaoSistema(FuncaoSistemaResponse funcaoSistema) {
        this.funcaoSistema = funcaoSistema;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public FamiliaResponse getFamilia() {
        return familia;
    }

    public void setFamilia(FamiliaResponse familia) {
        this.familia = familia;
    }
}
