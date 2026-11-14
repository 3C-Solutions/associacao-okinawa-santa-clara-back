package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "pagamento_cobranca")
public class PagamentoCobranca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagamento_cobranca")
    private Integer idPagamentoCobranca;
    @Column(name = "fk_usuario")
    private Integer fkUsuario;
    @Column(name = "fk_familia")
    private Integer fkFamilia;
    @Column(name = "cobranca_id_cobranca")
    private Integer cobrancaIdCobranca;
    @Column(name = "status", length = 45)
    private String status;
    @Column(name = "forma_pagamento", length = 45)
    private String formaPagamento;
    public Integer getIdPagamentoCobranca() { return idPagamentoCobranca; }
    public void setIdPagamentoCobranca(Integer idPagamentoCobranca) {
        this.idPagamentoCobranca = idPagamentoCobranca;
    }
    public Integer getFkUsuario() { return fkUsuario; }
    public void setFkUsuario(Integer fkUsuario) { this.fkUsuario = fkUsuario; }
    public Integer getFkFamilia() { return fkFamilia; }
    public void setFkFamilia(Integer fkFamilia) { this.fkFamilia = fkFamilia; }
    public Integer getCobrancaIdCobranca() { return cobrancaIdCobranca; }
    public void setCobrancaIdCobranca(Integer cobrancaIdCobranca) {
        this.cobrancaIdCobranca = cobrancaIdCobranca;
    }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }
}
