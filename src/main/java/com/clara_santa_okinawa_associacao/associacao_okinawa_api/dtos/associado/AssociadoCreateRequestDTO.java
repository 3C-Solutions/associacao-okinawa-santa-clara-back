package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.associado;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Dados utilizados para realizar o cadastro de um associado")
public class AssociadoCreateRequestDTO {
    @NotBlank
    @Schema(description = "Nome completo do associado a ser cadastrado", example = "João da Silva")
    private String nomeCompleto;

    @NotNull
    @Past
    @Schema(description = "Data de nascimento do associado a ser cadastrado, sempre no passado",
            example = "19/09/1999")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    @NotBlank
    @Email
    @Schema(description = "E-mail do associado a ser cadastrado", example = "joao@email.com")
    private String email;

    @Nullable
    @Schema(description = "Estado civil do associado a ser cadastrado", example = "Casado")
    private String estadoCivil;

    public AssociadoCreateRequestDTO() {
    }

    public AssociadoCreateRequestDTO(String nomeCompleto, LocalDate dataNascimento, String email,
                                     @Nullable String estadoCivil) {
        this.nomeCompleto = nomeCompleto;
        this.dataNascimento = dataNascimento;
        this.email = email;
        this.estadoCivil = estadoCivil;
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
}
