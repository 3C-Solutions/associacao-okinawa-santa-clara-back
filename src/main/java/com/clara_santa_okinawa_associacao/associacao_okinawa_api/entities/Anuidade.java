package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "anuidade")
public class Anuidade {
    @Id
    @Column(name = "idanuidade")
    private Integer idAnuidade;
    @Column(name = "ano")
    private Integer ano;
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "dtvencimento")
    private LocalDate dtVencimento;
    public Integer getIdAnuidade() { return idAnuidade; }
    public void setIdAnuidade(Integer value) { this.idAnuidade = value; }
    public Integer getAno() { return ano; }
    public void setAno(Integer value) { this.ano = value; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal value) { this.valor = value; }
    public LocalDate getDtVencimento() { return dtVencimento; }
    public void setDtVencimento(LocalDate value) { this.dtVencimento = value; }
}
