package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Dados utilizados para realizar o cadastro de um associado")
public class UsuarioCreateRequest {
    @NotNull
    @Schema(description = "Identificador único da família no qual o usuário representa", example =
            "2")
    private Integer idFamilia;

    @NotBlank
    @Schema(description = "Nome completo do associado a ser cadastrado", example = "João da Silva")
    private String nome;

    @NotBlank
    @Email
    @Schema(description = "E-mail do associado a ser cadastrado", example = "joao@email.com")
    private String email;

    @Nullable
    @Size(max = 20)
    private String telefone;

    @NotNull
    @Past
    @Schema(description = "Data de nascimento do associado a ser cadastrado, sempre no passado",
            example = "19/09/1999")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    @NotNull
    @Schema(description = "Identificador único da função que o usuário tem no sistema", example =
            "1")
    private Integer idFuncaoSistema;

    public Integer getIdFamilia() {
        return idFamilia;
    }

    public void setIdFamilia(Integer idFamilia) {
        this.idFamilia = idFamilia;
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

    @Nullable
    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(@Nullable String telefone) {
        this.telefone = telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Integer getIdFuncaoSistema() {
        return idFuncaoSistema;
    }

    public void setIdFuncaoSistema(Integer idFuncaoSistema) {
        this.idFuncaoSistema = idFuncaoSistema;
    }
}
