package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatriculaRepository extends JpaRepository<Matricula, Integer> {

    boolean existsByDepartamento_Id(Integer departamentoId);

    long countByDepartamento_IdAndAtivoTrue(Integer departamentoId);

    long countByDepartamento_IdAndAtivoTrueAndIdNot(
            Integer departamentoId,
            Integer id
    );
}