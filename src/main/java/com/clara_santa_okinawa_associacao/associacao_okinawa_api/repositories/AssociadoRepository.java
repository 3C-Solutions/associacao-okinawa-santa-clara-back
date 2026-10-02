package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Associado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssociadoRepository extends JpaRepository<Associado, Integer> {
    Boolean existsByEmail(String email);

    Boolean existsByEmailAndIdNot(String email, Integer id);
}
