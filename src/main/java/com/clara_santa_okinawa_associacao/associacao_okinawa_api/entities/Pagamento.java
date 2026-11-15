package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagamento")
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idPagamento;

    private Integer fkFamilia;

    private Integer fkAnuidade;

    @Column(name = "data_pagamento")
    private LocalDate dtPagamento;
    @Column(name = "forma_pagamento")
    private String formaPagamento;

    private String status;

    public Integer getIdPagamento() {
        return idPagamento;
    }

    public void setIdPagamento(Integer value) {
        this.idPagamento = value;
    }

    public Integer getFkFamilia() {
        return fkFamilia;
    }

    public void setFkFamilia(Integer value) {
        this.fkFamilia = value;
    }

    public Integer getFkAnuidade() {
        return fkAnuidade;
    }

    public void setFkAnuidade(Integer value) {
        this.fkAnuidade = value;
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

