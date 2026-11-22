package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.*;

// Mapeamento mínimo de tabela já presente no DER; não criar nova tabela.
@Entity
@Table(name = "departamento")
public class Departamento {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "nome_departamento")
    private String nomeDepartamento;
    @Column(name = "fk_responsavel_departamento")
    private Integer fkResponsavelDepartamento;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNomeDepartamento() { return nomeDepartamento; }
    public void setNomeDepartamento(String nomeDepartamento) { this.nomeDepartamento = nomeDepartamento; }
    public Integer getFkResponsavelDepartamento() { return fkResponsavelDepartamento; }
    public void setFkResponsavelDepartamento(Integer fkResponsavelDepartamento) { this.fkResponsavelDepartamento = fkResponsavelDepartamento; }
}