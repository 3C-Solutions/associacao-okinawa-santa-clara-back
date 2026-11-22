package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "noticia")
public class Noticia {
    public static final String TODOS = "TODOS";

    public enum Status { RASCUNHO, PUBLICADO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDate dataCriacao;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 20)
    private String classificacao;

    @Column(nullable = false, length = 500)
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column(nullable = false, length = 100)
    private String publico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_imagem")
    private Imagem imagem;

    protected Noticia() {}

    public Noticia(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public void atualizar(String titulo, String classificacao, String conteudo, Status status, String publico) {
        this.titulo = titulo;
        this.classificacao = classificacao;
        this.conteudo = conteudo;
        this.status = status;
        this.publico = publico;
    }

    public Integer getId() { return id; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public String getTitulo() { return titulo; }
    public String getClassificacao() { return classificacao; }
    public String getConteudo() { return conteudo; }
    public Status getStatus() { return status; }
    public String getPublico() { return publico; }
    public Imagem getImagem() { return imagem; }
    public void setImagem(Imagem imagem) { this.imagem = imagem; }
    public void setStatus(Status status) { this.status = status; }
}
