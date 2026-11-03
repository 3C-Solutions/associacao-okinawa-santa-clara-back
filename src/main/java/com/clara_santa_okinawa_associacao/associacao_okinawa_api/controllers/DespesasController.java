package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.DespesasService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@PreAuthorize("hasAuthority('TELA_FINANCEIRO')")
@RequestMapping("/api/financeiro/despesas")
public class DespesasController {
    private final DespesasService service;
    public DespesasController(DespesasService service) { this.service = service; }
    @GetMapping
    public ResponseEntity<List<DespesasResponse>> listar() {
        return ResponseEntity.status(200).body(service.listar());
    }
    @PostMapping
    public ResponseEntity<DespesasResponse> criar(@Valid @RequestBody DespesasCreateRequest dto) {
        return ResponseEntity.status(201).body(service.criar(dto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<DespesasResponse> editar(@PathVariable Integer id,
                                                   @Valid @RequestBody DespesasUpdateRequest dto) {
        return ResponseEntity.status(200).body(service.editar(id, dto));
    }
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<DespesasResponse> confirmar(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(service.confirmar(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}