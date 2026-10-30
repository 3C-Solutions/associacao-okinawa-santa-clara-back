package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncionalidadeSistema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FuncionalidadeSistemaRepository extends JpaRepository<FuncionalidadeSistema,
        Integer> {
    Optional<FuncionalidadeSistema> findByCodigo(String codigo);
}
