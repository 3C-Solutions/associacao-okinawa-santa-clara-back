package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;

public interface DepartamentoRepository extends JpaRepository<Departamento, Integer> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Integer id);

    boolean existsByDiaSemanaIgnoreCaseAndHorarioAndAtivoTrue(
            String diaSemana,
            LocalTime horario
    );

    boolean existsByDiaSemanaIgnoreCaseAndHorarioAndAtivoTrueAndIdNot(
            String diaSemana,
            LocalTime horario,
            Integer id
    );

    boolean existsByImagem_Id(Integer imagemId);
}