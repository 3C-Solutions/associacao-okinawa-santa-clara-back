package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

@Schema(description = "Dados utilizados para atualização de um usuário")
public class UsuarioUpdateRequest {
    @NotBlank
    @Schema(description = "Nome completo do usuário", example = "João Silva")
    private String nome;

    @Nullable
    @Schema(description = "Telefone do usuário", example = "11999999999")
    private String telefone;

    @Schema(description = "Status do usuário no sistema", example = "true")
    private boolean ativo;

    @NotNull
    @Schema(description = "Identificador único da função que o usuário exerce no sistema",
            example = "1")
    private Integer idFuncaoSistema;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Nullable
    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(@Nullable String telefone) {
        this.telefone = telefone;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getIdFuncaoSistema() {
        return idFuncaoSistema;
    }

    public void setIdFuncaoSistema(Integer idFuncaoSistema) {
        this.idFuncaoSistema = idFuncaoSistema;
    }
}
