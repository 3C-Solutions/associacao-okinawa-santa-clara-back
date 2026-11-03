package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Endereco;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Familia;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.EnderecoNaoExisteException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.EnderecoMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.FamiliaMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.EnderecoRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FamiliaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FamiliaService {

    private final FamiliaMapper familiaMapper;
    private final EnderecoRepository enderecoRepository;
    private final FamiliaRepository familiaRepository;
    private final EnderecoMapper enderecoMapper;

    public FamiliaService(FamiliaMapper familiaMapper, EnderecoRepository enderecoRepository, FamiliaRepository familiaRepository, EnderecoMapper enderecoMapper) {
        this.familiaMapper = familiaMapper;
        this.enderecoRepository = enderecoRepository;
        this.familiaRepository = familiaRepository;
        this.enderecoMapper = enderecoMapper;
    }

    public FamiliaResponse cadastrar(FamiliaRequest dto){
        Optional<Endereco> enderecoExiste = enderecoRepository.findById(dto.getFkEndereco());

        if(enderecoExiste.isEmpty()){
            throw new EnderecoNaoExisteException();
        }

        Endereco endereco = enderecoExiste.get();

        Familia entidade = familiaMapper.toEntity(dto);
        entidade.setEndereco(endereco);
        Familia entidadeSalva = familiaRepository.save(entidade);
        FamiliaResponse familiaDto = FamiliaMapper.toResponse(entidadeSalva);

        return familiaDto;
    }
}
