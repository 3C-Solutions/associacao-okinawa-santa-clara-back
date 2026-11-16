package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.departamento.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Departamento;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.DepartamentoMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.DepartamentoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @PostMapping
    public ResponseEntity<DepartamentoResponse> cadastrar(
            @Valid @RequestBody DepartamentoRequest dto
    ) {

        Departamento departamento = DepartamentoMapper.toEntity(dto);

        Departamento cadastrado = departamentoService.cadastrar(departamento);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DepartamentoMapper.toResponse(cadastrado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoResponse> editar(
            @PathVariable Integer id,
            @Valid @RequestBody DepartamentoRequest dto
    ) {

        Departamento departamento = DepartamentoMapper.toEntity(dto);

        Departamento atualizado = departamentoService.editar(id, departamento);

        return ResponseEntity.status(HttpStatus.OK)
                .body(DepartamentoMapper.toResponse(atualizado));
    }

    @GetMapping
    public ResponseEntity<List<DepartamentoResponse>> listar() {

        List<Departamento> departamentos = departamentoService.listar();

        return ResponseEntity.status(HttpStatus.OK)
                .body(DepartamentoMapper.toResponse(departamentos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoResponse> buscarPorId(
            @PathVariable Integer id
    ) {

        Departamento departamento = departamentoService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(DepartamentoMapper.toResponse(departamento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {

        departamentoService.excluir(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}