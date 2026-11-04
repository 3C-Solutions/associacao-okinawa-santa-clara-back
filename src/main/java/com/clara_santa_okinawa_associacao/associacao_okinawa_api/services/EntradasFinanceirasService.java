package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.EntradaFinanceiraRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.EntradaFinanceira;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class EntradasFinanceirasService {
    private final EntradaFinanceiraRepository entradas;
    private final FamiliaRepository familias;
    public EntradasFinanceirasService(EntradaFinanceiraRepository entradas, FamiliaRepository familias) {
        this.entradas = entradas;
        this.familias = familias;
    }
    private EntradaFinanceira buscar(Integer id) {
        return entradas.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrada não encontrada"));
    }
    private void preencher(EntradaFinanceira entrada, EntradaFinanceiraRequest r) {
        if (r.descricao().trim().length() < 3 || r.categoria().trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe descrição e categoria válidas");
        if (r.fkFamilia() != null && !familias.existsById(r.fkFamilia()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Família não encontrada");
        entrada.setData(r.data());
        entrada.setDescricao(r.descricao().trim());
        entrada.setCategoria(r.categoria().trim());
        entrada.setValor(r.valor());
        entrada.setStatus(r.status());
        entrada.setFkFamilia(r.fkFamilia());
    }
    @Transactional(readOnly = true)
    public List<EntradaFinanceira> listar() { return entradas.findAll(); }
    @Transactional
    public EntradaFinanceira criar(EntradaFinanceiraRequest r, Integer usuarioId) {
        EntradaFinanceira entrada = new EntradaFinanceira();
        preencher(entrada, r);
        entrada.setFkUsuario(usuarioId);
        return entradas.save(entrada);
    }
    @Transactional
    public EntradaFinanceira editar(Integer id, EntradaFinanceiraRequest r) {
        EntradaFinanceira entrada = buscar(id);
        preencher(entrada, r);
        return entradas.save(entrada);
    }
    @Transactional
    public EntradaFinanceira confirmar(Integer id) {
        EntradaFinanceira entrada = buscar(id);
        if (!"Pendente".equals(entrada.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Somente entrada pendente pode ser confirmada");
        entrada.setStatus("Recebido");
        return entradas.save(entrada);
    }
    @Transactional
    public void excluir(Integer id) { entradas.delete(buscar(id)); }
}
