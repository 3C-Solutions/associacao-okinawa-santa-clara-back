package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "matricula")
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "fk_familia")
    private Familia familia;

    @ManyToOne
    @JoinColumn(name = "fk_departamento")
    private Departamento departamento;

    @Column(name = "data_inicio_matricula")
    private LocalDate dataInicioMatricula;

    private Boolean ativo;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Familia getFamilia() { return familia; }
    public void setFamilia(Familia familia) { this.familia = familia; }
    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }
    public LocalDate getDataInicioMatricula() { return dataInicioMatricula; }
    public void setDataInicioMatricula(LocalDate dataInicioMatricula) { this.dataInicioMatricula = dataInicioMatricula; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
