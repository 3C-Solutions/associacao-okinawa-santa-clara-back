package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FinanceiroService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.*;
@RestController
@RequestMapping("/api/financeiro")
public class CobrancasController {
    private final FinanceiroService service;
    public CobrancasController(FinanceiroService service){this.service=service;}
    @GetMapping("/cobrancas") @PreAuthorize("hasAuthority('TELA_FINANCEIRO')")
    public List<CobrancaResponse> cobrancas(){return service.listarCobrancas();}
    @PostMapping("/cobrancas") @PreAuthorize("hasAuthority('TELA_FINANCEIRO')")
    public ResponseEntity<CobrancaResponse> emitir(@Valid @RequestBody CobrancaCreateRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.emitir(req));
    }
    @GetMapping("/destinatarios") @PreAuthorize("hasAuthority('TELA_FINANCEIRO')")
    public Map<String,Object> destinatarios(@RequestParam(defaultValue="") String busca,
                                            @RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="20") int tamanho){
        return service.pesquisarUsuarios(busca,pagina,tamanho);
    }
    @GetMapping("/pagamentos-cobranca") @PreAuthorize("hasAuthority('TELA_FINANCEIRO')")
    public List<PagamentoCobrancaResponse> listarPagamentos(){return service.todosPagamentos();}
    @GetMapping("/minhas-cobrancas") @PreAuthorize("isAuthenticated()")
    public List<PagamentoCobrancaResponse> minhas(JwtAuthenticationToken auth){
        return service.meusPagamentos(Integer.parseInt(auth.getToken().getSubject()));
    }
    @PatchMapping("/pagamentos-cobranca/{id}/informar") @PreAuthorize("isAuthenticated()")
    public PagamentoCobrancaResponse informar(@PathVariable int id,@Valid @RequestBody PagamentoInformarRequest req,
                                              JwtAuthenticationToken auth){return service.informar(id,Integer.parseInt(auth.getToken().getSubject()),req);}
    @PatchMapping("/pagamentos-cobranca/{id}/confirmar") @PreAuthorize("hasAuthority('TELA_FINANCEIRO')")
    public PagamentoCobrancaResponse confirmar(@PathVariable int id){return service.revisar(id,true);}
    @PatchMapping("/pagamentos-cobranca/{id}/recusar") @PreAuthorize("hasAuthority('TELA_FINANCEIRO')")
    public PagamentoCobrancaResponse recusar(@PathVariable int id){return service.revisar(id,false);}
}
