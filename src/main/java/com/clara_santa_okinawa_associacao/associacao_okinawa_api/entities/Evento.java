package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "evento")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "dt_inicio")
    private LocalDate dtInicio;

    @Column(name = "dt_fim")
    private LocalDate dtFim;

    @Column(name = "local")
    private String local;

    @Column(name = "status")
    private String status;

    @Column(name = "fk_usuario")
    private Integer fkUsuario;

    @Column(name = "fk_familia")
    private Integer fkFamilia;

    @Column(name = "dia_inteiro")
    private Boolean diaInteiro;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fim")
    private LocalTime horaFim;

    @Column(name = "origem_cadastro")
    private String origemCadastro;

    @Column(name = "tipo_reservante")
    private String tipoReservante;

    @Column(name = "fk_departamento")
    private Integer fkDepartamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "codigo_espaco")
    private EspacoEvento codigoEspaco;

    @Column(name = "motivo_recusa")
    private String motivoRecusa;

    @Column(name = "fk_usuario_decisao")
    private Integer fkUsuarioDecisao;

    @Column(name = "data_decisao")
    private OffsetDateTime dataDecisao;

    @Column(name = "fk_usuario_cancelamento")
    private Integer fkUsuarioCancelamento;

    @Column(name = "data_cancelamento")
    private OffsetDateTime dataCancelamento;

    @Column(name = "google_calendar_id")
    private String googleCalendarId;

    @Column(name = "google_event_id")
    private String googleEventId;

    @Column(name = "operacao_sincronizacao")
    private String operacaoSincronizacao;

    @Column(name = "status_sincronizacao")
    private String statusSincronizacao;

    @Column(name = "erro_sincronizacao")
    private String erroSincronizacao;

    @Column(name = "data_ultima_sincronizacao")
    private OffsetDateTime dataUltimaSincronizacao;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDtInicio() { return dtInicio; }
    public void setDtInicio(LocalDate dtInicio) { this.dtInicio = dtInicio; }

    public LocalDate getDtFim() { return dtFim; }
    public void setDtFim(LocalDate dtFim) { this.dtFim = dtFim; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getFkUsuario() { return fkUsuario; }
    public void setFkUsuario(Integer fkUsuario) { this.fkUsuario = fkUsuario; }

    public Integer getFkFamilia() { return fkFamilia; }
    public void setFkFamilia(Integer fkFamilia) { this.fkFamilia = fkFamilia; }

    public Boolean getDiaInteiro() { return diaInteiro; }
    public void setDiaInteiro(Boolean diaInteiro) { this.diaInteiro = diaInteiro; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }

    public String getOrigemCadastro() { return origemCadastro; }
    public void setOrigemCadastro(String origemCadastro) { this.origemCadastro = origemCadastro; }

    public String getTipoReservante() { return tipoReservante; }
    public void setTipoReservante(String tipoReservante) { this.tipoReservante = tipoReservante; }

    public Integer getFkDepartamento() { return fkDepartamento; }
    public void setFkDepartamento(Integer fkDepartamento) { this.fkDepartamento = fkDepartamento; }

    public EspacoEvento getCodigoEspaco() { return codigoEspaco; }
    public void setCodigoEspaco(EspacoEvento codigoEspaco) { this.codigoEspaco = codigoEspaco; }

    public String getMotivoRecusa() { return motivoRecusa; }
    public void setMotivoRecusa(String motivoRecusa) { this.motivoRecusa = motivoRecusa; }

    public Integer getFkUsuarioDecisao() { return fkUsuarioDecisao; }
    public void setFkUsuarioDecisao(Integer fkUsuarioDecisao) { this.fkUsuarioDecisao = fkUsuarioDecisao; }

    public OffsetDateTime getDataDecisao() { return dataDecisao; }
    public void setDataDecisao(OffsetDateTime dataDecisao) { this.dataDecisao = dataDecisao; }

    public Integer getFkUsuarioCancelamento() { return fkUsuarioCancelamento; }
    public void setFkUsuarioCancelamento(Integer fkUsuarioCancelamento) { this.fkUsuarioCancelamento = fkUsuarioCancelamento; }

    public OffsetDateTime getDataCancelamento() { return dataCancelamento; }
    public void setDataCancelamento(OffsetDateTime dataCancelamento) { this.dataCancelamento = dataCancelamento; }

    public String getGoogleCalendarId() { return googleCalendarId; }
    public void setGoogleCalendarId(String googleCalendarId) { this.googleCalendarId = googleCalendarId; }

    public String getGoogleEventId() { return googleEventId; }
    public void setGoogleEventId(String googleEventId) { this.googleEventId = googleEventId; }

    public String getOperacaoSincronizacao() { return operacaoSincronizacao; }
    public void setOperacaoSincronizacao(String operacaoSincronizacao) { this.operacaoSincronizacao = operacaoSincronizacao; }

    public String getStatusSincronizacao() { return statusSincronizacao; }
    public void setStatusSincronizacao(String statusSincronizacao) { this.statusSincronizacao = statusSincronizacao; }

    public String getErroSincronizacao() { return erroSincronizacao; }
    public void setErroSincronizacao(String erroSincronizacao) { this.erroSincronizacao = erroSincronizacao; }

    public OffsetDateTime getDataUltimaSincronizacao() { return dataUltimaSincronizacao; }
    public void setDataUltimaSincronizacao(OffsetDateTime dataUltimaSincronizacao) { this.dataUltimaSincronizacao = dataUltimaSincronizacao; }

}