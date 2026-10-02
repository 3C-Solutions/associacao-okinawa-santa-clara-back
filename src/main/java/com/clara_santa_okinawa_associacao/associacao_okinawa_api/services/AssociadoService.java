package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.associado.AssociadoCreateRequestDTO;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.associado.AssociadoResponseDTO;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.associado.AssociadoUpdateRequestDTO;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Associado;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.AlreadyExistsByEmailException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.NotExistsByIdException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.AssociadoMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.AssociadoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssociadoService {

    private final AssociadoRepository associadosRepository;
    private final AssociadoMapper mapper;

    public AssociadoService(AssociadoRepository associadosRepository, AssociadoMapper mapper) {
        this.associadosRepository = associadosRepository;
        this.mapper = mapper;
    }

    public List<AssociadoResponseDTO> listar() {
        List<Associado> associados = associadosRepository.findAll();
        return mapper.toResponseDto(associados);
    }

    public AssociadoResponseDTO buscarPorId(Integer id) {
        if (!associadosRepository.existsById(id)) throw new NotExistsByIdException("Associado não" +
                "encontrado! " + id);

        Associado associado =
                associadosRepository.findById(id).orElseThrow(() -> new NotExistsByIdException(
                        "Usuário não encontrado!"));

        return mapper.toResponseDto(associado);
    }

    public AssociadoResponseDTO cadastrar(AssociadoCreateRequestDTO requestDTO) {
        String emailNormalizado = normalizarEmail(requestDTO.getEmail());

        if (associadosRepository.existsByEmail(emailNormalizado)) {
            throw new AlreadyExistsByEmailException("Já existe um associado com este e-mail " +
                    "cadastrado no sistema! " + emailNormalizado);
        }

        Associado novoAssociado = mapper.toEntity(requestDTO);
        novoAssociado.setFilhosCadastrados(0);
        novoAssociado.setAssociado(true);
        novoAssociado.setDataCriacao(LocalDateTime.now());

        Associado associadoCadastrado = associadosRepository.save(novoAssociado);
        return mapper.toResponseDto(associadoCadastrado);
    }

    public AssociadoResponseDTO atualizar(AssociadoUpdateRequestDTO requestDTO, Integer id) {
        String emailNormalizado = normalizarEmail(requestDTO.getEmail());

        Associado associadoParaAtualizar =
                associadosRepository.findById(id).orElseThrow(() -> new NotExistsByIdException(
                        "Associado não encontrado!" + id));

        if (associadosRepository.existsByEmailAndIdNot(emailNormalizado, id)) {
            throw new AlreadyExistsByEmailException("Já existe um associado com este e-mail " +
                    "cadastrado no sistema! " + emailNormalizado);
        }

        associadoParaAtualizar.setId(id);
        mapper.updateEntity(requestDTO, associadoParaAtualizar);
        Associado associadoAtualizado = associadosRepository.save(associadoParaAtualizar);

        return mapper.toResponseDto(associadoAtualizado);
    }

    public void excluir(Integer id) {
        if (!associadosRepository.existsById(id)) {
            throw new NotExistsByIdException("Associado não encontrado " + id);
        }

        associadosRepository.deleteById(id);
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}
