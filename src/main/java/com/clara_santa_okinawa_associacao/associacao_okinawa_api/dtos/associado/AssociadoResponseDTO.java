package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.associado;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Dados que serão devolvidos como resposta de um requisição")
public class AssociadoResponseDTO {
    @Schema(description = "Identificador único do associado", example = "1")
    private Integer id;

    @Schema(description = "Nome completo do associado", example = "João da Silva")
    private String nomeCompleto;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Schema(description = "Data de nascimento do associado", example = "19/09/1999")
    private LocalDate dataNascimento;

    @Schema(description = "E-mail do associado", example = "joao@email.com")
    private String email;

    @Schema(description = "Estado civil do associado", example = "Casado")
    private String estadoCivil;

    @Schema(description = "Status do associado", example = "true")
    private boolean isAssociado;

    @Schema(description = "Quantidade de filhos cadastrados pelo associado", example = "2")
    private Integer filhosCadastrados;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Data de criação do associado no sistema", example = "30/09/2026")
    private LocalDateTime dataCriacao;

    public AssociadoResponseDTO() {
    }

    public AssociadoResponseDTO(Integer id, String nomeCompleto, LocalDate dataNascimento,
                                String email, String estadoCivil, boolean isAssociado,
                                Integer filhosCadastrados, LocalDateTime dataCriacao) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.dataNascimento = dataNascimento;
        this.email = email;
        this.estadoCivil = estadoCivil;
        this.isAssociado = isAssociado;
        this.filhosCadastrados = filhosCadastrados;
        this.dataCriacao = dataCriacao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public boolean getAssociado() {
        return isAssociado;
    }

    public void setAssociado(boolean associado) {
        isAssociado = associado;
    }

    public Integer getFilhosCadastrados() {
        return filhosCadastrados;
    }

    public void setFilhosCadastrados(Integer filhosCadastrados) {
        this.filhosCadastrados = filhosCadastrados;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
