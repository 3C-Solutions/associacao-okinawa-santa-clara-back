package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcionalidade.sistema.FuncionalidadeSistemaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FuncionalidadeSistemaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionalidadeSistemaService {
    private final FuncionalidadeSistemaRepository repository;

    public FuncionalidadeSistemaService(FuncionalidadeSistemaRepository repository) {
        this.repository = repository;
    }

    public List<FuncionalidadeSistemaResponse> listar() {
        return repository.findAll(Sort.by("id")).stream()
                .map(funcionalidade -> new FuncionalidadeSistemaResponse(
                        funcionalidade.getId(), funcionalidade.getNome(),
                        funcionalidade.getDescricao(), funcionalidade.getCodigo()))
                .toList();
    }
}
