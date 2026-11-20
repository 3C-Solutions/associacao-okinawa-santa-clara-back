package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.departamento.DepartamentoPublicoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.DepartamentoMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.DepartamentoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/publico/departamentos")
public class DepartamentoPublicoController {

    private final DepartamentoService departamentoService;

    public DepartamentoPublicoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public ResponseEntity<List<DepartamentoPublicoResponse>> listar() {
        return ResponseEntity.ok(
                DepartamentoMapper.toPublicoResponse(departamentoService.listarAtivos()));
    }
}