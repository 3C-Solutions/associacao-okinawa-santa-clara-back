package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Noticia;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Noticia.Status;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface NoticiaRepository extends JpaRepository<Noticia, Integer> {
    String NO_PUBLICO = "(lower(trim(n.publico)) = 'todos' or lower(trim(n.publico)) = lower(trim(:funcao)))";
    String VISIVEL = NO_PUBLICO + " and (:gestor = true or n.status = PUBLICADO)";
    String FILTROS = " where " + VISIVEL + """
              and (:filtrarStatus = false or n.status = :status)
              and (:busca = '' or locate(lower(:busca), lower(n.titulo)) > 0
                   or locate(lower(:busca), lower(n.classificacao)) > 0
                   or locate(lower(:busca), lower(n.conteudo)) > 0)
            """;

    @Query(value = "select n from Noticia n" + FILTROS + " order by n.dataCriacao desc, n.id desc",
            countQuery = "select count(n) from Noticia n" + FILTROS)
    Page<Noticia> listar(@Param("funcao") String funcao, @Param("gestor") boolean gestor,
                        @Param("filtrarStatus") boolean filtrarStatus, @Param("status") Status status,
                        @Param("busca") String busca, Pageable pagina);

    @Query("select n from Noticia n where n.id = :id and " + VISIVEL)
    Optional<Noticia> buscarVisivel(@Param("id") Integer id, @Param("funcao") String funcao,
                                  @Param("gestor") boolean gestor);

    @Query("""
            select c.imagem.tipoConteudo as tipoConteudo, c.conteudo as conteudo
            from Noticia n join ImagemConteudo c on c.id = n.imagem.id
            where n.id = :id and
            """ + VISIVEL)
    Optional<ImagemConteudoRepository.Arquivo> buscarCapa(@Param("id") Integer id,
            @Param("funcao") String funcao, @Param("gestor") boolean gestor);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from Noticia n where n.id = :id and " + NO_PUBLICO)
    Optional<Noticia> buscarParaAlterar(@Param("id") Integer id, @Param("funcao") String funcao);

    boolean existsByImagem_Id(Integer imagemId);
}
