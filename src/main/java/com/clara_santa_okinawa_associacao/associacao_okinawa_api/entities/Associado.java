package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "associados")
public class Associado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nomeCompleto;
    private LocalDate dataNascimento;
    @Column(nullable = false, unique = true)
    private String email;
    private String estadoCivil;
    private boolean isAssociado;
    private Integer filhosCadastrados;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataCriacao;

    public Associado() {
    }

    public Associado(Integer id, String nomeCompleto, LocalDate dataNascimento, String email,
                     String estadoCivil, boolean isAssociado, Integer filhosCadastrados,
                     LocalDateTime dataCriacao) {
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