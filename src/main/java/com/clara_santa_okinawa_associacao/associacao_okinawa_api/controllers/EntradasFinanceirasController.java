package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.EntradaFinanceiraRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.EntradaFinanceira;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.EntradasFinanceirasService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@PreAuthorize("hasAuthority('TELA_FINANCEIRO')")
@RequestMapping("/api/financeiro/entradas")
public class EntradasFinanceirasController {
    private final EntradasFinanceirasService service;
    public EntradasFinanceirasController(EntradasFinanceirasService service) { this.service = service; }
    @GetMapping
    public List<EntradaFinanceira> listar() { return service.listar(); }
    @PostMapping
    public ResponseEntity<EntradaFinanceira> criar(@Valid @RequestBody EntradaFinanceiraRequest r,
                                                  JwtAuthenticationToken authentication) {
        return ResponseEntity.status(201).body(service.criar(r,
                Integer.valueOf(authentication.getToken().getSubject())));
    }
    @PutMapping("/{id}")
    public EntradaFinanceira editar(@PathVariable Integer id, @Valid @RequestBody EntradaFinanceiraRequest r) {
        return service.editar(id, r);
    }
    @PatchMapping("/{id}/confirmar")
    public EntradaFinanceira confirmar(@PathVariable Integer id) { return service.confirmar(id); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
