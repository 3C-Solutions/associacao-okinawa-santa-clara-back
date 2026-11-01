package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaCreateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncaoSistema;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncionalidadeSistema;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.AlreadyExistsByNomeException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.NotExistsByIdException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.FuncaoSistemaMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FuncaoSistemaRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FuncionalidadeSistemaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class FuncaoSistemaService {

    private final FuncaoSistemaRepository funcaoSistemaRepository;
    private final FuncionalidadeSistemaRepository funcionalidadeSistemaRepository;

    public FuncaoSistemaService(FuncaoSistemaRepository funcaoSistemaRepository,
                               FuncionalidadeSistemaRepository funcionalidadeSistemaRepository) {
        this.funcaoSistemaRepository = funcaoSistemaRepository;
        this.funcionalidadeSistemaRepository = funcionalidadeSistemaRepository;
    }

    @Transactional(readOnly = true)
    public List<FuncaoSistemaResponse> listar() {
        return funcaoSistemaRepository.findAll(Sort.by("id")).stream()
                .map(FuncaoSistemaMapper::toResponse)
                .toList();
    }

    @Transactional
    public FuncaoSistemaResponse cadastrar(FuncaoSistemaCreateRequest request) {
        FuncaoSistema funcao = FuncaoSistemaMapper.toEntity(request);
        if (funcaoSistemaRepository.existsByNomeNormalizado(funcao.getNome())) {
            throw nomeJaCadastrado();
        }

        List<FuncionalidadeSistema> funcionalidades =
                funcionalidadeSistemaRepository.findAllById(request.idsFuncionalidades());
        Set<Integer> idsNaoEncontrados = new TreeSet<>(request.idsFuncionalidades());
        funcionalidades.forEach(funcionalidade -> idsNaoEncontrados.remove(funcionalidade.getId()));
        if (!idsNaoEncontrados.isEmpty()) {
            throw new NotExistsByIdException("Funcionalidades não encontradas: " + idsNaoEncontrados);
        }

        funcao.setFuncionalidades(new HashSet<>(funcionalidades));
        return FuncaoSistemaMapper.toResponse(funcaoSistemaRepository.save(funcao));
    }

    private AlreadyExistsByNomeException nomeJaCadastrado() {
        return new AlreadyExistsByNomeException("Já existe uma função cadastrada com este nome");
    }
}
