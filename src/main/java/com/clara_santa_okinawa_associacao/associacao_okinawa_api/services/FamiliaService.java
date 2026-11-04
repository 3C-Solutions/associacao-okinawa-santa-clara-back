package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaComEnderecoRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Endereco;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Familia;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.EnderecoNaoExisteException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.NotExistsByIdException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.EnderecoMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.FamiliaMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.EnderecoRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FamiliaRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FamiliaService {

    private final EnderecoRepository enderecoRepository;
    private final FamiliaRepository familiaRepository;

    public FamiliaService(EnderecoRepository enderecoRepository,
                          FamiliaRepository familiaRepository) {
        this.enderecoRepository = enderecoRepository;
        this.familiaRepository = familiaRepository;
    }

    public FamiliaResponse cadastrar(FamiliaRequest dto) {
        Optional<Endereco> enderecoExiste = enderecoRepository.findById(dto.getFkEndereco());

        if (enderecoExiste.isEmpty()) {
            throw new EnderecoNaoExisteException();
        }

        Endereco endereco = enderecoExiste.get();

        Familia entidade = FamiliaMapper.toEntity(dto);
        entidade.setEndereco(endereco);
        Familia entidadeSalva = familiaRepository.save(entidade);
        return FamiliaMapper.toResponse(entidadeSalva);
    }

    @Transactional(readOnly = true)
    public List<FamiliaResponse> listar() {
        return familiaRepository.findAll(Sort.by("nome").ascending().and(Sort.by("id")))
                .stream().map(FamiliaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public FamiliaResponse buscarPorId(Integer id) {
        return FamiliaMapper.toResponse(familiaRepository.findById(id)
                .orElseThrow(() -> new NotExistsByIdException("Família não encontrada: " + id)));
    }

    @Transactional
    public FamiliaResponse cadastrarComEndereco(FamiliaComEnderecoRequest dto) {
        Endereco endereco = EnderecoMapper.toEntity(dto.endereco());
        Endereco enderecoSalvo = enderecoRepository.save(endereco);
        Familia familia = new Familia();
        familia.setNome(dto.nome().trim());
        familia.setEndereco(enderecoSalvo);
        return FamiliaMapper.toResponse(familiaRepository.saveAndFlush(familia));
    }

}
