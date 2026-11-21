package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.ImagemConteudo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ImagemConteudoRepository extends JpaRepository<ImagemConteudo, Integer> {

    @Query("""
            SELECT i.tipoConteudo AS tipoConteudo, c.conteudo AS conteudo
            FROM ImagemConteudo c
            JOIN c.imagem i
            WHERE c.id = :imagemId
            """)
    Optional<Arquivo> buscar(@Param("imagemId") Integer imagemId);

    interface Arquivo {
        String getTipoConteudo();
        byte[] getConteudo();
    }
}
