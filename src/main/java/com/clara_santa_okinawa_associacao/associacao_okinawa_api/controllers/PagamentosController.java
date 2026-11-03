package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.PagamentosService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@PreAuthorize("hasAuthority('TELA_FINANCEIRO')")
@RequestMapping("/api/financeiro")
public class PagamentosController {
    private final PagamentosService service;
    public PagamentosController(PagamentosService service) { this.service = service; }
    @GetMapping("/pagamentos")
    public ResponseEntity<List<PagamentoResponse>> listar() {
        return ResponseEntity.status(200).body(service.listarPagamentos());
    }
    @PostMapping("/pagamentos")
    public ResponseEntity<PagamentoResponse> solicitar(@Valid @RequestBody PagamentoCreateRequest dto) {
        return ResponseEntity.status(201).body(service.solicitar(dto));
    }
    @PatchMapping("/pagamentos/{id}/confirmar")
    public ResponseEntity<PagamentoResponse> confirmar(@PathVariable Integer id,
                                                       @Valid @RequestBody PagamentoConfirmarRequest dto) {
        return ResponseEntity.status(200).body(service.confirmar(id, dto));
    }
    @PatchMapping("/pagamentos/{id}/recusar")
    public ResponseEntity<PagamentoResponse> recusar(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(service.recusar(id));
    }
    @GetMapping("/pagamentos-departamento")
    public ResponseEntity<List<PagamentoDepartamentoResponse>> listarDepartamento() {
        return ResponseEntity.status(200).body(service.listarDepartamentos());
    }
    @PostMapping("/pagamentos-departamento")
    public ResponseEntity<PagamentoDepartamentoResponse> solicitarDepartamento(
            @Valid @RequestBody PagamentoDepartamentoCreateRequest dto) {
        return ResponseEntity.status(201).body(service.solicitarDepartamento(dto));
    }
    @PatchMapping("/pagamentos-departamento/{id}/confirmar")
    public ResponseEntity<PagamentoDepartamentoResponse> confirmarDepartamento(
            @PathVariable Integer id, @Valid @RequestBody PagamentoConfirmarRequest dto) {
        return ResponseEntity.status(200).body(service.confirmarDepartamento(id, dto));
    }
    @PatchMapping("/pagamentos-departamento/{id}/recusar")
    public ResponseEntity<PagamentoDepartamentoResponse> recusarDepartamento(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(service.recusarDepartamento(id));
    }
    @GetMapping("/taxas/anuidade")
    public ResponseEntity<AnuidadeSituacaoResponse> taxaFamilia(
            @RequestParam Integer fkFamilia, @RequestParam Integer ano) {
        return ResponseEntity.status(200).body(service.anuidade(fkFamilia, ano));
    }
    @GetMapping("/taxas/mensalidade")
    public ResponseEntity<MensalidadeSituacaoResponse> taxaDepartamento(
            @RequestParam Integer fkDepartamento, @RequestParam Integer ano, @RequestParam Integer mes) {
        return ResponseEntity.status(200).body(service.mensalidade(fkDepartamento, ano, mes));}
}

