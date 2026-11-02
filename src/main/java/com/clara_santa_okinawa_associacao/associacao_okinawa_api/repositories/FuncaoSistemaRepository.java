package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncaoSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FuncaoSistemaRepository extends JpaRepository<FuncaoSistema, Integer> {
    @Query("""
            SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
            FROM FuncaoSistema f
            WHERE LOWER(TRIM(f.nome)) = LOWER(TRIM(:nome))
            """)
    boolean existsByNomeNormalizado(@Param("nome") String nome);
}
