package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departamento")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String descricao;

    @Column(name = "dia_semana")
    private String diaSemana;

    private LocalTime horario;
    private String professor;
    private String escola;

    @Column(name = "valor_mensal", precision = 10, scale = 2)
    private BigDecimal valorMensal;

    @ManyToOne
    @JoinColumn(name = "fk_responsavel_departamento")
    private Usuario responsavelDepartamento;

    @ManyToOne
    @JoinColumn(name = "fk_familia")
    private Familia familia;

    @ManyToOne
    @JoinColumn(name = "fk_imagem")
    private Imagem imagem;

    @Column(name = "quantidade_vagas", nullable = false)
    private Integer quantidadeVagas;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Integer getQuantidadeVagas() {
        return quantidadeVagas;
    }

    public void setQuantidadeVagas(Integer quantidadeVagas) {
        this.quantidadeVagas = quantidadeVagas;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }
    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime horario) { this.horario = horario; }
    public String getProfessor() { return professor; }
    public void setProfessor(String professor) { this.professor = professor; }
    public String getEscola() { return escola; }
    public void setEscola(String escola) { this.escola = escola; }
    public BigDecimal getValorMensal() { return valorMensal; }
    public void setValorMensal(BigDecimal valorMensal) { this.valorMensal = valorMensal; }
    public Usuario getResponsavelDepartamento() { return responsavelDepartamento; }
    public void setResponsavelDepartamento(Usuario responsavelDepartamento) { this.responsavelDepartamento = responsavelDepartamento; }
    public Familia getFamilia() { return familia; }
    public void setFamilia(Familia familia) { this.familia = familia; }
    public Imagem getImagem() { return imagem; }
    public void setImagem(Imagem imagem) { this.imagem = imagem; }
}
