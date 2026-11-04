package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Endereco;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.EnderecoMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.EnderecoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    @Transactional
    public EnderecoResponse cadastrar(EnderecoRequest request) {
        Endereco endereco = EnderecoMapper.toEntity(request);
        return EnderecoMapper.toResponse(enderecoRepository.save(endereco));
    }
}