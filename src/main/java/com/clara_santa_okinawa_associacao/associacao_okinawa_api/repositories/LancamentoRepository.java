package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Lancamento;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface LancamentoRepository extends JpaRepository<Lancamento, Integer> {
    @EntityGraph(attributePaths = {"usuario", "usuario.funcaoSistema"})
    List<Lancamento> findAllByTipoLancamentoOrderByDataLancamentoDescIdDesc(String tipoLancamento);

    @EntityGraph(attributePaths = {"usuario", "usuario.funcaoSistema"})
    List<Lancamento> findAllByTipoLancamentoAndUsuario_IdOrderByDataLancamentoDescIdDesc(
            String tipoLancamento, Integer usuarioId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Lancamento l WHERE l.id = :id AND l.tipoLancamento = :tipo")
    Optional<Lancamento> buscarParaAtualizacao(@Param("id") Integer id, @Param("tipo") String tipo);
}
