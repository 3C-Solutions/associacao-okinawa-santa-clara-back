package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.AssociadoDependente.AssociadoDependenteRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.AssociadoDependente.AssociadoDependenteResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.AssociadoDependente;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Familia;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.FamiliaNaoExisteException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.AssociadoDependenteMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.AssociadoDependenteRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FamiliaRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AssociadoDependenteService {

    private final AssociadoDependenteMapper associadoDependenteMapper;
    private final FamiliaRepository familiaRepository;
    private final AssociadoDependenteRepository associadoDependenteRepository;

    public AssociadoDependenteService(AssociadoDependenteMapper associadoDependenteMapper, FamiliaRepository familiaRepository, AssociadoDependenteRepository associadoDependenteRepository) {
        this.associadoDependenteMapper = associadoDependenteMapper;
        this.familiaRepository = familiaRepository;
        this.associadoDependenteRepository = associadoDependenteRepository;
    }


    public AssociadoDependenteResponse cadastrar(AssociadoDependenteRequest dto){
        Optional<Familia> familiaExistente = familiaRepository.findById(dto.getFamiliaId().getId());

        if(familiaExistente.isEmpty()){
            throw new FamiliaNaoExisteException();
        }

        Familia familiaEncontrada = familiaExistente.get();

        AssociadoDependente associado = associadoDependenteMapper.toEntity(dto);
        associado.setFamilia(familiaEncontrada);
        AssociadoDependente associadoSalvo = associadoDependenteRepository.save(associado);

        AssociadoDependenteResponse associadoDto = associadoDependenteMapper.toResponse(associadoSalvo);

        return associadoDto;
}
}
