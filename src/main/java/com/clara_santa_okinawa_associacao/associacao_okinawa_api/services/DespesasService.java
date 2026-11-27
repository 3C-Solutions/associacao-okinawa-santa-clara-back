package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Despesas;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Lancamento;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.DespesasRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FamiliaRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.LancamentoRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.DespesasMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service
public class DespesasService {
    private final DespesasRepository repository;
    private final FamiliaRepository familias;
    private final LancamentoRepository lancamentos;
    public DespesasService(DespesasRepository repository, FamiliaRepository familias,
                           LancamentoRepository lancamentos) {
        this.repository = repository;
        this.familias = familias;
        this.lancamentos = lancamentos;
    }
    private void validar(String descricao, Integer familiaId) {
        if (descricao.trim().length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descreva a despesa com pelo menos 3 caracteres");
        }
        if (familiaId != null && !familias.existsById(familiaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Família não encontrada");
        }
    }
    private Despesas buscar(Integer id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Despesas nao encontrada"));
    }
    @Transactional(readOnly = true)
    public List<DespesasResponse> listar() { return repository.findAll().stream().map(DespesasMapper::toResponse).
            toList(); }
    @Transactional
    public DespesasResponse criar(DespesasCreateRequest r, Integer usuarioId) {
        validar(r.descricao(), r.fkFamilia());
        Despesas d = DespesasMapper.toEntity(r);
        d.setFkUsuario(usuarioId);
        return DespesasMapper.toResponse(repository.save(d));
    }
    @Transactional
    public DespesasResponse editar(Integer id, DespesasUpdateRequest r) {
        Despesas d = buscar(id);
        validarDespesaEditavel(d);
        validar(r.descricao(), r.fkFamilia());
        DespesasMapper.updateEntity(r, d);
        return DespesasMapper.toResponse(repository.save(d));
    }
    @Transactional
    public DespesasResponse confirmar(Integer id) {
        Despesas d = buscar(id);
        if (!"Pendente".equalsIgnoreCase(d.getStatus())) throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Somente despesa pendente pode ser confirmada");
        Lancamento reembolso = null;
        if (d.getFkReembolso() != null) {
            reembolso = lancamentos.buscarParaAtualizacao(d.getFkReembolso(), Lancamento.TIPO_REEMBOLSO).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.CONFLICT, "Solicitação de reembolso não encontrada"));
            if (!"Aprovado".equals(reembolso.getStatus())) throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Somente reembolso aprovado pode ser pago");
            reembolso.setStatus("Pago");
        }
        d.setStatus("Pago");
        DespesasResponse resposta = DespesasMapper.toResponse(repository.save(d));
        if (reembolso != null) lancamentos.save(reembolso);
        return resposta;
    }
    @Transactional
    public void excluir(Integer id) {
        Despesas d = buscar(id);
        validarDespesaEditavel(d);
        repository.delete(d);
    }

    private void validarDespesaEditavel(Despesas despesa) {
        if (despesa.getFkReembolso() != null) throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Despesa vinculada a reembolso não pode ser editada ou excluída");
    }
}
