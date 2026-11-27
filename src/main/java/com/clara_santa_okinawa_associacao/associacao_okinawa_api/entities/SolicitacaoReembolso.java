package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao_reembolso")
public class SolicitacaoReembolso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_usuario", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Usuario solicitante;

    @Column(name = "motivo", nullable = false, length = 500)
    private String motivo;

    @Column(name = "valor", nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    @Column(name = "comprovante_nome", nullable = false)
    private String comprovanteNome;

    @Column(name = "comprovante_tipo", nullable = false)
    private String comprovanteTipo;

    @Column(name = "comprovante_dados", nullable = false, columnDefinition = "TEXT")
    private String comprovanteDados;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_revisao")
    private LocalDateTime dataRevisao;

    @Column(name = "motivo_recusa", length = 500)
    private String motivoRecusa;

    @Column(name = "fk_revisor")
    private Integer fkRevisor;

    public Integer getId() { return id; }
    public void setId(Integer value) { id = value; }
    public Usuario getSolicitante() { return solicitante; }
    public void setSolicitante(Usuario value) { solicitante = value; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String value) { motivo = value; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal value) { valor = value; }
    public String getComprovanteNome() { return comprovanteNome; }
    public void setComprovanteNome(String value) { comprovanteNome = value; }
    public String getComprovanteTipo() { return comprovanteTipo; }
    public void setComprovanteTipo(String value) { comprovanteTipo = value; }
    public String getComprovanteDados() { return comprovanteDados; }
    public void setComprovanteDados(String value) { comprovanteDados = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime value) { dataSolicitacao = value; }
    public LocalDateTime getDataRevisao() { return dataRevisao; }
    public void setDataRevisao(LocalDateTime value) { dataRevisao = value; }
    public String getMotivoRecusa() { return motivoRecusa; }
    public void setMotivoRecusa(String value) { motivoRecusa = value; }
    public Integer getFkRevisor() { return fkRevisor; }
    public void setFkRevisor(Integer value) { fkRevisor = value; }
}
