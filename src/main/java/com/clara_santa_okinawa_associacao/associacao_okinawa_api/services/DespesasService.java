package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Despesas;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.DespesasRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.DespesasMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service
public class DespesasService {
    private final DespesasRepository repository;
    public DespesasService(DespesasRepository repository) { this.repository = repository; }
    private Despesas buscar(Integer id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Despesas nao encontrada"));
    }
    @Transactional(readOnly = true)
    public List<DespesasResponse> listar() { return repository.findAll().stream().map(DespesasMapper::toResponse).
            toList(); }
    @Transactional
    public DespesasResponse criar(DespesasCreateRequest r) {
        Despesas d = DespesasMapper.toEntity(r);
        return DespesasMapper.toResponse(repository.save(d));
    }
    @Transactional
    public DespesasResponse editar(Integer id, DespesasUpdateRequest r) {
        Despesas d = buscar(id);
        DespesasMapper.updateEntity(r, d);
        return DespesasMapper.toResponse(repository.save(d));
    }
    @Transactional
    public DespesasResponse confirmar(Integer id) {
        Despesas d = buscar(id);
        if (FinanceiroRegras.pago(d.getStatus())) throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Despesa ja confirmada");
        d.setStatus("Pago");
        return DespesasMapper.toResponse(repository.save(d));
    }
    @Transactional
    public void excluir(Integer id) { repository.delete(buscar(id)); }
}