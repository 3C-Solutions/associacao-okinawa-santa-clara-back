package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.DashboardFinanceiroResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FinanceiroDashboardService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.utils.XlsxResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
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

    @GetMapping(value = "/export.xlsx", produces = XlsxResponse.CONTENT_TYPE)
    public ResponseEntity<byte[]> exportar(
            @RequestParam Integer ano,
            @RequestParam Integer mes,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String busca) {
        return XlsxResponse.attachment("financeiro.xlsx",
                service.exportarXlsx(ano, mes, dataInicio, dataFim, tipo, status, busca));
    }
}
