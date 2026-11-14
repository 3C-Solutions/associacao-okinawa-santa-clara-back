package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.DashboardFinanceiroConsolidadoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FinanceiroService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@PreAuthorize("hasAuthority('TELA_FINANCEIRO')")
@RequestMapping("/api/financeiro/dashboard")
public class FinanceiroDashboardController {
    private final FinanceiroService service;
    public FinanceiroDashboardController(FinanceiroService service){this.service=service;}
    @GetMapping public ResponseEntity<DashboardFinanceiroConsolidadoResponse> buscar(@RequestParam int ano,@RequestParam int mes){
        return ResponseEntity.ok(service.dashboard(ano,mes));}
}