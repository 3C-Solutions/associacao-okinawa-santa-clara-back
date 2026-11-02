package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth.AcessosResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth.LoginRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth.LoginResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.AcessoService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(
        name = "Autenticação",
        description = "Endpoints responsáveis pela autenticação dos usuários"
)
public class AuthController {

    private final AuthService authService;
    private final AcessoService acessoService;

    public AuthController(AuthService authService, AcessoService acessoService) {
        this.authService = authService;
        this.acessoService = acessoService;
    }

    @GetMapping("/acessos")
    @Operation(
            summary = "Consulta das telas permitidas ao usuário autenticado",
            description = "Retorna os códigos TELA_ concedidos à função do usuário autenticado, sem duplicidades e em ordem alfabética. Exige apenas autenticação válida."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Telas permitidas; lista vazia quando não há telas disponíveis",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AcessosResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado; usuário removido, inativo ou sem função",
                    content = @Content)
    })
    public ResponseEntity<AcessosResponse> listarAcessos(JwtAuthenticationToken authentication) {
        return ResponseEntity.ok(acessoService.listar(authentication.getAuthorities()));
    }

    @Operation(
            summary = "Autenticação de um usuário",
            description = "Autentica um usuário pelo e-mail e senha e retorna um token JWT para acesso ao sistema"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário autenticado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados para autenticação do usuário enviados incorretamente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "E-mail ou senha inválidos ou usuário desativado",
                    content = @Content
            )
    })
    @PostMapping("/login")
    @SecurityRequirements
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }
}
