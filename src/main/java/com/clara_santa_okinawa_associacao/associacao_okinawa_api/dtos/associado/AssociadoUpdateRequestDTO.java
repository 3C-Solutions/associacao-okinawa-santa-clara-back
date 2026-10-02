package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.associado;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Dados utilizados para atualização de um associado")
public class AssociadoUpdateRequestDTO {
    @NotBlank
    @Schema(description = "Nome completo do associado", example = "João Silva")
    private String nomeCompleto;

    @NotNull
    @Past
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Schema(description = "Data de nascimento do associado", example = "19/09/1999")
    private LocalDate dataNascimento;

    @NotBlank
    @Email
    @Schema(description = "E-mail do associado", example = "joao@email.com")
    private String email;

    @Nullable
    @Schema(description = "Estado civil do associado", example = "Casado")
    private String estadoCivil;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Quantidade de filhos cadastrados pelo associado", example =
            "2")
    private Integer filhosCadastrados;

    @Schema(description = "Status do associado", example = "true")
    private boolean isAssociado;

    public AssociadoUpdateRequestDTO() {
    }

    public AssociadoUpdateRequestDTO(String nomeCompleto, LocalDate dataNascimento, String email,
                                     @Nullable String estadoCivil, Integer filhosCadastrados,
                                     boolean isAssociado) {
        this.nomeCompleto = nomeCompleto;
        this.dataNascimento = dataNascimento;
        this.email = email;
        this.estadoCivil = estadoCivil;
        this.filhosCadastrados = filhosCadastrados;
        this.isAssociado = isAssociado;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Nullable
    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(@Nullable String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public Integer getFilhosCadastrados() {
        return filhosCadastrados;
    }

    public void setFilhosCadastrados(Integer filhosCadastrados) {
        this.filhosCadastrados = filhosCadastrados;
    }

    public boolean isAssociado() {
        return isAssociado;
    }

    public void setAssociado(boolean associado) {
        isAssociado = associado;
    }
}
