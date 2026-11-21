package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "imagem_conteudo")
public class ImagemConteudo {

    @Id
    @Column(name = "imagem_id")
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "imagem_id", foreignKey = @ForeignKey(name = "fk_imagem_conteudo_imagem"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Imagem imagem;

    @Column(name = "conteudo", nullable = false, columnDefinition = "bytea")
    private byte[] conteudo;

    protected ImagemConteudo() {
    }

    public ImagemConteudo(Imagem imagem, byte[] conteudo) {
        this.imagem = imagem;
        this.conteudo = conteudo;
    }

    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }
}
