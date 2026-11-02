package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth.UsuarioAutenticadoContextoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth.LoginRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth.LoginResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.AuthService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.UsuarioAutenticadoService;
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
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public AuthController(AuthService authService, UsuarioAutenticadoService usuarioAutenticadoService) {
        this.authService = authService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @GetMapping("/me")
    @Operation(
            summary = "Consulta do usuário autenticado e seus acessos",
            description = "Retorna identificação, função e permissões do usuário identificado pelo token. Cada permissão corresponde ao acesso a uma tela. Os códigos são únicos e ordenados alfabeticamente. Exige apenas autenticação válida."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário e acessos atuais; lista de permissões vazia quando não há acessos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioAutenticadoContextoResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado; usuário removido, inativo ou sem função",
                    content = @Content)
    })
    public ResponseEntity<UsuarioAutenticadoContextoResponse> consultarUsuarioAutenticado(JwtAuthenticationToken authentication) {
        Integer usuarioId = Integer.valueOf(authentication.getToken().getSubject());
        return ResponseEntity.ok(usuarioAutenticadoService.consultar(usuarioId));
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
