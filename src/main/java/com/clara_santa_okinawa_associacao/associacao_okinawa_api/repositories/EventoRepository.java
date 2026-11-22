package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    List<Evento> findByStatusAndDtInicioLessThanEqualAndDtFimGreaterThanEqual(
            String status, LocalDate fim, LocalDate inicio);

    List<Evento> findByFkUsuarioOrderByDtInicioDesc(Integer fkUsuario);

    @Query("SELECT e FROM Evento e WHERE e.dtInicio <= :fim AND e.dtFim >= :inicio ORDER BY e.dtInicio, e.horaInicio")
    List<Evento> buscarPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}