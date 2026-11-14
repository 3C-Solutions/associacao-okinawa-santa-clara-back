package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FinanceiroService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
@RestController
@PreAuthorize("hasAuthority('TELA_FINANCEIRO')")
@RequestMapping("/api/financeiro/lancamentos")
public class LancamentoController {
    private final FinanceiroService service;
    public LancamentoController(FinanceiroService service){this.service=service;}
    @PostMapping public ResponseEntity<LancamentoResponse> criar(@Valid @RequestBody LancamentoCreateRequest req,
                                                                 JwtAuthenticationToken auth){return ResponseEntity.status(201).body(service.criarLancamento(req,
            Integer.parseInt(auth.getToken().getSubject())));}
    @PutMapping("/{id}") public LancamentoResponse editar(@PathVariable int id,@Valid @RequestBody LancamentoCreateRequest req){
        return service.editarLancamento(id,req);}
    @PatchMapping("/{id}/confirmar") public LancamentoResponse confirmar(@PathVariable int id){
        return service.confirmarLancamento(id);}
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable int id){
        service.excluirLancamento(id);return ResponseEntity.noContent().build();}
}