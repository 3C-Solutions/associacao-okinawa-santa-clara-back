package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncaoSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FuncaoSistemaRepository extends JpaRepository<FuncaoSistema, Integer> {
    @Query(value = "SELECT EXISTS (SELECT 1 FROM funcao_sistema "
            + "WHERE lower(btrim(nome)) = lower(btrim(:nome)))", nativeQuery = true)
    boolean existsByNomeNormalizado(@Param("nome") String nome);
}
