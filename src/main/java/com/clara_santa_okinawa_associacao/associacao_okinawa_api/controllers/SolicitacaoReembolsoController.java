package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.ReembolsoRecusarRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.SolicitacaoReembolsoCreateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.SolicitacaoReembolsoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.SolicitacaoReembolsoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reembolsos")
public class SolicitacaoReembolsoController {
    private final SolicitacaoReembolsoService service;

    public SolicitacaoReembolsoController(SolicitacaoReembolsoService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("!hasAuthority('TELA_FINANCEIRO')")
    public ResponseEntity<SolicitacaoReembolsoResponse> criar(
            @Valid @RequestBody SolicitacaoReembolsoCreateRequest request,
            JwtAuthenticationToken authentication) {
        Integer usuarioId = Integer.valueOf(authentication.getToken().getSubject());
        return ResponseEntity.status(201).body(service.criar(request, usuarioId));
    }

    @GetMapping("/meus")
    public List<SolicitacaoReembolsoResponse> listarDoUsuario(JwtAuthenticationToken authentication) {
        Integer usuarioId = Integer.valueOf(authentication.getToken().getSubject());
        return service.listarDoUsuario(usuarioId);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TELA_FINANCEIRO')")
    public List<SolicitacaoReembolsoResponse> listar() {
        return service.listar();
    }

    @PatchMapping("/{id}/aprovar")
    @PreAuthorize("hasAuthority('TELA_FINANCEIRO')")
    public SolicitacaoReembolsoResponse aprovar(@PathVariable Integer id,
                                                JwtAuthenticationToken authentication) {
        Integer revisorId = Integer.valueOf(authentication.getToken().getSubject());
        return service.aprovar(id, revisorId);
    }

    @PatchMapping("/{id}/recusar")
    @PreAuthorize("hasAuthority('TELA_FINANCEIRO')")
    public SolicitacaoReembolsoResponse recusar(@PathVariable Integer id,
                                                @Valid @RequestBody ReembolsoRecusarRequest request,
                                                JwtAuthenticationToken authentication) {
        Integer revisorId = Integer.valueOf(authentication.getToken().getSubject());
        return service.recusar(id, revisorId, request.motivo());
    }
}
