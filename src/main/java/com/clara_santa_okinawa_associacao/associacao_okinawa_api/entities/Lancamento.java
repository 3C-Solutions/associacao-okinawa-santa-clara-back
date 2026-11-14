package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Table(name = "lancamento")
public class Lancamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lancamento")
    private Integer idLancamento;
    @Column(name = "dt_lancamento")
    private LocalDate dtLancamento;
    @Column(name = "tipo_lancamento", length = 45)
    private String tipoLancamento;
    @Column(name = "descricao", length = 200)
    private String descricao;
    @Column(name = "categoria", length = 100)
    private String categoria;
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "status", length = 45)
    private String status;
    @Column(name = "fk_usuario")
    private Integer fkUsuario;
    @Column(name = "fk_familia")
    private Integer fkFamilia;
    public Integer getIdLancamento() { return idLancamento; }
    public void setIdLancamento(Integer idLancamento) { this.idLancamento = idLancamento; }
    public LocalDate getDtLancamento() { return dtLancamento; }
    public void setDtLancamento(LocalDate dtLancamento) { this.dtLancamento = dtLancamento; }
    public String getTipoLancamento() { return tipoLancamento; }
    public void setTipoLancamento(String tipoLancamento) { this.tipoLancamento = tipoLancamento; }
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
