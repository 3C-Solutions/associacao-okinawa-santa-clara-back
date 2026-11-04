package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.NotExistsByIdException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.FamiliaMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FamiliaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// Consultas portadas de origin/develop (dd04b15), preservando o contrato /familias.
@Service
public class FamiliaService {
    private final FamiliaRepository familias;
    public FamiliaService(FamiliaRepository familias) { this.familias = familias; }
    @Transactional(readOnly = true)
    public List<FamiliaResponse> listar() {
        return familias.findAll(Sort.by("nome").ascending().and(Sort.by("id")))
                .stream().map(FamiliaMapper::toResponse).toList();
    }
    @Transactional(readOnly = true)
    public FamiliaResponse buscarPorId(Integer id) {
        return FamiliaMapper.toResponse(familias.findById(id)
                .orElseThrow(() -> new NotExistsByIdException("Família não encontrada: " + id)));
    }
}
