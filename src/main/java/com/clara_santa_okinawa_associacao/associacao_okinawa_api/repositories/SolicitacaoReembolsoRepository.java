package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.SolicitacaoReembolso;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SolicitacaoReembolsoRepository extends JpaRepository<SolicitacaoReembolso, Integer> {
    @EntityGraph(attributePaths = {"solicitante", "solicitante.funcaoSistema"})
    List<SolicitacaoReembolso> findAllByOrderByDataSolicitacaoDesc();

    @EntityGraph(attributePaths = {"solicitante", "solicitante.funcaoSistema"})
    List<SolicitacaoReembolso> findAllBySolicitante_IdOrderByDataSolicitacaoDesc(Integer usuarioId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM SolicitacaoReembolso r WHERE r.id = :id")
    Optional<SolicitacaoReembolso> buscarParaAtualizacao(@Param("id") Integer id);
}
