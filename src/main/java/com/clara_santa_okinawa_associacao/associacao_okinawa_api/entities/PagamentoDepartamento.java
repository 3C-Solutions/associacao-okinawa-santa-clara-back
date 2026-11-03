package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Table(name = "pagamentodepartamento")
public class PagamentoDepartamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpagamentodepartamento")
    private Integer idPagamentoDepartamento;
    @Column(name = "fkdepartamento")
    private Integer fkDepartamento;
    @Column(name = "fkmensalidade")
    private Integer fkMensalidade;
    @Column(name = "dtpagamento")
    private LocalDate dtPagamento;
    @Column(name = "formapagamento")
    private String formaPagamento;
    @Column(name = "status")
    private String status;
    public Integer getIdPagamentoDepartamento() { return idPagamentoDepartamento; }
    public void setIdPagamentoDepartamento(Integer value) { this.idPagamentoDepartamento = value; }
    public Integer getFkDepartamento() { return fkDepartamento; }
    public void setFkDepartamento(Integer value) { this.fkDepartamento = value; }
    public Integer getFkMensalidade() { return fkMensalidade; }
    public void setFkMensalidade(Integer value) { this.fkMensalidade = value; }
    public LocalDate getDtPagamento() { return dtPagamento; }
    public void setDtPagamento(LocalDate value) { this.dtPagamento = value; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String value) { this.formaPagamento = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
}
