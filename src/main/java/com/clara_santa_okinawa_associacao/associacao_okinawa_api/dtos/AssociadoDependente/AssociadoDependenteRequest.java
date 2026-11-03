package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.AssociadoDependente;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Familia;

import java.time.LocalDate;

public class AssociadoDependenteRequest {


    // depois é para fazer as validações!!!!
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private LocalDate dataNascimento;
    private Familia familiaId;

    public AssociadoDependenteRequest(String nome, String email, String senha, String telefone, LocalDate dataNascimento, Familia familiaId) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.familiaId = familiaId;
    }

    public AssociadoDependenteRequest() {
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
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

    public Familia getFamiliaId() {
        return familiaId;
    }

    public void setFamiliaId(Familia familiaId) {
        this.familiaId = familiaId;
    }
}
