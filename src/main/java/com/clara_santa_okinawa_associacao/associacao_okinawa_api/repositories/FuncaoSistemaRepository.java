package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncaoSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FuncaoSistemaRepository extends JpaRepository<FuncaoSistema, Integer> {
    @Query("select trim(f.nome) from FuncaoSistema f where lower(trim(f.nome)) = lower(trim(:nome))")
    Optional<String> buscarNomeCanonico(@Param("nome") String nome);

    @Query("""
            select trim(f.nome) from FuncaoSistema f
            where length(trim(f.nome)) between 1 and 100 and lower(trim(f.nome)) <> 'todos'
            order by lower(trim(f.nome)), f.id
            """)
    List<String> listarNomesParaNoticias();

    @Query("select distinct f from FuncaoSistema f left join fetch f.funcionalidades where f.id = :id")
    java.util.Optional<FuncaoSistema> buscarComFuncionalidades(@Param("id") Integer id);

    @Query("""
            SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
            FROM FuncaoSistema f
            WHERE LOWER(TRIM(f.nome)) = LOWER(TRIM(:nome))
            """)
    boolean existsByNomeNormalizado(@Param("nome") String nome);
}
