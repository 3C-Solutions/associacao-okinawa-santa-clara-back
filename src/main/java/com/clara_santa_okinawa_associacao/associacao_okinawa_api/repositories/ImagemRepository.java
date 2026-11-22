package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Imagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ImagemRepository extends JpaRepository<Imagem, Integer> {
    String FORA_DE_NOTICIAS = "not exists (select 1 from Noticia n where n.imagem.id = i.id)";

    @Query("select i from Imagem i where " + FORA_DE_NOTICIAS)
    List<Imagem> listarSemCapasDeNoticias();

    @Query("select i from Imagem i where i.id = :id and " + FORA_DE_NOTICIAS)
    Optional<Imagem> buscarSemCapasDeNoticias(@Param("id") Integer id);
}
