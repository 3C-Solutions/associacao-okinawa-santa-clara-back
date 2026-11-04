package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Table(name = "despesas")
public class Despesas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idDespesas;
    @Column(name = "data_despesa")
    private LocalDate dtDepesa;
    @Column(name = "descricao")
    private String descricao;
    @Column(name = "categoria")
    private String categoria;
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "status")
    private String status;
    @Column(name = "fk_usuario")
    private Integer fkUsuario;
    @Column(name = "fk_familia")
    private Integer fkFamilia;
    public Integer getIdDespesas() { return idDespesas; }
    public void setIdDespesas(Integer value) { this.idDespesas = value; }
    public LocalDate getDtDepesa() { return dtDepesa; }
    public void setDtDepesa(LocalDate value) { this.dtDepesa = value; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String value) { this.descricao = value; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String value) { this.categoria = value; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal value) { this.valor = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
    public Integer getFkUsuario() { return fkUsuario; }
    public void setFkUsuario(Integer value) { this.fkUsuario = value; }
    public Integer getFkFamilia() { return fkFamilia; }
    public void setFkFamilia(Integer value) { this.fkFamilia = value; }
}
