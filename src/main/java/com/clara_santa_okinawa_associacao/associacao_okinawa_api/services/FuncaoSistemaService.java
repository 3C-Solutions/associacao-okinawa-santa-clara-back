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
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaDetalheResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaFuncionalidadesRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcionalidade.sistema.FuncionalidadeSistemaResponse;
import java.util.Comparator;
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

    @Transactional(readOnly = true)
    public FuncaoSistemaDetalheResponse buscar(Integer id) {
        return detalhar(buscarEntidade(id));
    }

    @Transactional
    public FuncaoSistemaDetalheResponse atualizarFuncionalidades(Integer id, FuncaoSistemaFuncionalidadesRequest request) {
        FuncaoSistema funcao = buscarEntidade(id);
        List<FuncionalidadeSistema> funcionalidades = funcionalidadeSistemaRepository.findAllById(request.idsFuncionalidades());
        Set<Integer> ausentes = new TreeSet<>(request.idsFuncionalidades());
        funcionalidades.forEach(item -> ausentes.remove(item.getId()));
        if (!ausentes.isEmpty()) throw new NotExistsByIdException("Funcionalidades não encontradas: " + ausentes);
        // Preserva a coleção gerenciada para que o Hibernate controle os vínculos no flush.
        if (!funcao.getFuncionalidades().equals(new HashSet<>(funcionalidades))) {
            funcao.getFuncionalidades().clear();
            funcao.getFuncionalidades().addAll(funcionalidades);
        }
        return detalhar(funcaoSistemaRepository.saveAndFlush(funcao));
    }

    private FuncaoSistema buscarEntidade(Integer id) {
        return funcaoSistemaRepository.buscarComFuncionalidades(id)
                .orElseThrow(() -> new NotExistsByIdException("Função do sistema não encontrada: " + id));
    }

    private FuncaoSistemaDetalheResponse detalhar(FuncaoSistema funcao) {
        return new FuncaoSistemaDetalheResponse(funcao.getId(), funcao.getNome(), funcao.getDescricao(),
                funcao.getFuncionalidades().stream().sorted(Comparator.comparing(FuncionalidadeSistema::getId))
                        .map(item -> new FuncionalidadeSistemaResponse(item.getId(), item.getNome(), item.getDescricao(), item.getCodigo()))
                        .toList());
    }

    private AlreadyExistsByNomeException nomeJaCadastrado() {
        return new AlreadyExistsByNomeException("Já existe uma função cadastrada com este nome");
    }
}
