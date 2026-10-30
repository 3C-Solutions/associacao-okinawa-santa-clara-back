package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class FuncaoSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private String descricao;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "funcionalidade_sistema_funcao_sistema",

            joinColumns = @JoinColumn(
                    name = "fk_funcao_sistema"
            ),

            inverseJoinColumns = @JoinColumn(
                    name = "fk_funcionalidade_sistema"
            )
    )
    private Set<FuncionalidadeSistema> funcionalidades = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Set<FuncionalidadeSistema> getFuncionalidades() {
        return funcionalidades;
    }

    public void setFuncionalidades(Set<FuncionalidadeSistema> funcionalidades) {
        this.funcionalidades = funcionalidades;
    }
}
