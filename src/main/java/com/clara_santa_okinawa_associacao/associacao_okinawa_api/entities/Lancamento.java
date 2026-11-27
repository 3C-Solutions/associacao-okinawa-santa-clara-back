package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lancamento")
public class Lancamento {
    public static final String TIPO_REEMBOLSO = "REEMBOLSO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "data_lancamento", nullable = false)
    private LocalDate dataLancamento;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_usuario", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_familia",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Familia familia;

    @Column(name = "tipo_lancamento")
    private String tipoLancamento;

    public Integer getId() { return id; }
    public void setId(Integer value) { id = value; }
    public LocalDate getDataLancamento() { return dataLancamento; }
    public void setDataLancamento(LocalDate value) { dataLancamento = value; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String value) { descricao = value; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String value) { categoria = value; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal value) { valor = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario value) { usuario = value; }
    public Familia getFamilia() { return familia; }
    public void setFamilia(Familia value) { familia = value; }
    public String getTipoLancamento() { return tipoLancamento; }
    public void setTipoLancamento(String value) { tipoLancamento = value; }
}
