package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Despesas;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.DespesasRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FamiliaRepository;
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
    public DespesasService(DespesasRepository repository, FamiliaRepository familias) {
        this.repository = repository;
        this.familias = familias;
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
        validar(r.descricao(), r.fkFamilia());
        DespesasMapper.updateEntity(r, d);
        return DespesasMapper.toResponse(repository.save(d));
    }
    @Transactional
    public DespesasResponse confirmar(Integer id) {
        Despesas d = buscar(id);
        if (!"Pendente".equalsIgnoreCase(d.getStatus())) throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Somente despesa pendente pode ser confirmada");
        d.setStatus("Pago");
        return DespesasMapper.toResponse(repository.save(d));
    }
    @Transactional
    public void excluir(Integer id) { repository.delete(buscar(id)); }
}
