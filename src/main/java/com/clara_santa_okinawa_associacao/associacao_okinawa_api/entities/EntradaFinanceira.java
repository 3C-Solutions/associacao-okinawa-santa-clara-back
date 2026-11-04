package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "entradas_financeiras")
public class EntradaFinanceira {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "data_entrada", nullable = false)
    private LocalDate data;
    private String descricao;
    private String categoria;
    private BigDecimal valor;
    private String status;
    @Column(name = "fk_usuario", nullable = false)
    private Integer fkUsuario;
    @Column(name = "fk_familia")
    private Integer fkFamilia;

    public Integer getId() { return id; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getFkUsuario() { return fkUsuario; }
    public void setFkUsuario(Integer fkUsuario) { this.fkUsuario = fkUsuario; }
    public Integer getFkFamilia() { return fkFamilia; }
    public void setFkFamilia(Integer fkFamilia) { this.fkFamilia = fkFamilia; }
}
