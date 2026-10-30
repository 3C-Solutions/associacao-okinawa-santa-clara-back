package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    @Query("""
                SELECT DISTINCT u
                FROM Usuario u
                LEFT JOIN FETCH u.funcaoSistema f
                LEFT JOIN FETCH f.funcionalidades
                WHERE u.id = :id
            """)
    Optional<Usuario> buscarComPermissoes(@Param("id") Integer id);
}
