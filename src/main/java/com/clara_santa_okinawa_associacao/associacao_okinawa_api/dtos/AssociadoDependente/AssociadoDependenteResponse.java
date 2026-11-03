package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.AssociadoDependente;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Familia;

import java.time.LocalDate;

public class AssociadoDependenteResponse {

    private Integer id;
    private String nome;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private FamiliaResponse familia;

    public AssociadoDependenteResponse(Integer id, String nome, String email, String telefone, LocalDate dataNascimento, FamiliaResponse familia) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.familia = familia;
    }

    public AssociadoDependenteResponse() {
    }

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

    public FamiliaResponse getFamilia() {
        return familia;
    }

    public void setFamilia(FamiliaResponse familia) {
        this.familia = familia;
    }
}
