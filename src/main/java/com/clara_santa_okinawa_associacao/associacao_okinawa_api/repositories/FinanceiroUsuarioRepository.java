package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface FinanceiroUsuarioRepository extends JpaRepository<Usuario,Integer> {
    @Query("SELECT u FROM Usuario u WHERE u.ativo=true AND (lower(u.nome) LIKE lower(concat('%',:busca,'%')) " +
            "OR lower(u.email) LIKE lower(concat('%',:busca,'%'))) ORDER BY u.nome")
    Page<Usuario> pesquisar(@Param("busca") String busca, Pageable pageable);
    List<Usuario> findByAtivoTrue();
}