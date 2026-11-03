package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.
        DashboardFinanceiroResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FinanceiroDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@PreAuthorize("hasAuthority('TELA_FINANCEIRO')")
@RequestMapping("/api/financeiro/dashboard")
public class FinanceiroDashboardController {
    private final FinanceiroDashboardService service;
    public FinanceiroDashboardController(FinanceiroDashboardService service) { this.service = service; }
    @GetMapping
    public ResponseEntity<DashboardFinanceiroResponse> buscar(
            @RequestParam Integer ano, @RequestParam Integer mes) {
        return ResponseEntity.status(200).body(service.dashboard(ano, mes));
    }
}