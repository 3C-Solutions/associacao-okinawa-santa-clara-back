package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.conta.MinhaContaRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.conta.MinhaContaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.conta.MinhaContaSenhaRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.MinhaContaValidacaoException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.MinhaContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/minha-conta")
@Tag(name = "Minha conta", description = "Dados e senha do próprio usuário; exige apenas autenticação válida")
public class MinhaContaController {
    private final MinhaContaService servico;

    public MinhaContaController(MinhaContaService servico) {
        this.servico = servico;
    }

    @GetMapping
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Dados da própria conta"),
            @ApiResponse(responseCode = "401", description = "Sessão inválida ou conta sem acesso")})
    @Operation(summary = "Consultar minha conta", description = "Identifica a conta pelo JWT e retorna dados pessoais, função e família/endereço. Nunca retorna senha ou hash.")
    public ResponseEntity<MinhaContaResponse> consultar(JwtAuthenticationToken autenticacao) {
        return ResponseEntity.ok(servico.consultar(id(autenticacao)));
    }

    @PutMapping
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Dados atualizados"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou senha atual incorreta"),
            @ApiResponse(responseCode = "401", description = "Sessão inválida ou conta sem acesso"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")})
    @Operation(summary = "Atualizar minha conta", description = "Atualiza somente nome, e-mail e telefone. Alterar e-mail exige senha atual; não altera função, permissões, situação ou vínculos familiares.")
    public ResponseEntity<MinhaContaResponse> atualizar(JwtAuthenticationToken autenticacao, @Valid @RequestBody MinhaContaRequest dados) {
        return ResponseEntity.ok(servico.atualizar(id(autenticacao), dados));
    }

    @PutMapping("/senha")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Senha alterada; resposta sem corpo"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha inválida"),
            @ApiResponse(responseCode = "401", description = "Sessão inválida ou conta sem acesso")})
    @Operation(summary = "Alterar minha senha", description = "Confere a senha atual e grava a nova com BCrypt. Não revoga JWTs já emitidos; o front solicita novo login no navegador atual.")
    public ResponseEntity<Void> alterarSenha(JwtAuthenticationToken autenticacao, @Valid @RequestBody MinhaContaSenhaRequest dados) {
        servico.alterarSenha(id(autenticacao), dados);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MinhaContaValidacaoException.class)
    public ResponseEntity<Map<String, Object>> validacao(MinhaContaValidacaoException erro) {
        return ResponseEntity.badRequest().body(Map.of("status", 400, "mensagem", erro.getMessage(),
                "codigo", erro.getCodigo(), "timestamp", LocalDateTime.now()));
    }

    private Integer id(JwtAuthenticationToken autenticacao) {
        return Integer.valueOf(autenticacao.getToken().getSubject());
    }
}
