package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagamento_departamento")
public class PagamentoDepartamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idPagamentoDepartamento;

    private Integer fkDepartamento;

    private Integer fkMensalidade;

    @Column(name = "data_pagamento")
    private LocalDate dtPagamento;

    private String formaPagamento;

    private String status;

    public Integer getIdPagamentoDepartamento() {
        return idPagamentoDepartamento;
    }

    public void setIdPagamentoDepartamento(Integer value) {
        this.idPagamentoDepartamento = value;
    }

    public Integer getFkDepartamento() {
        return fkDepartamento;
    }

    public void setFkDepartamento(Integer value) {
        this.fkDepartamento = value;
    }

    public Integer getFkMensalidade() {
        return fkMensalidade;
    }

    public void setFkMensalidade(Integer value) {
        this.fkMensalidade = value;
    }

    public LocalDate getDtPagamento() {
        return dtPagamento;
    }

    public void setDtPagamento(LocalDate value) {
        this.dtPagamento = value;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String value) {
        this.formaPagamento = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }
}
