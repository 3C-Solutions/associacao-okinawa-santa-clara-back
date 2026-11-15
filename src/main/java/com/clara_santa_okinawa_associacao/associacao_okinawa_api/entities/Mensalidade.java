package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mensalidade")
public class Mensalidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idMensalidade;

    @Column(name = "data_vencimento")
    private LocalDate dtVencimento;

    @Column(name = "valor")
    private BigDecimal valor;

    public Integer getIdMensalidade() {
        return idMensalidade;
    }

    public void setIdMensalidade(Integer value) {
        this.idMensalidade = value;
    }

    public LocalDate getDtVencimento() {
        return dtVencimento;
    }

    public void setDtVencimento(LocalDate value) {
        this.dtVencimento = value;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal value) {
        this.valor = value;
    }
}