package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.error.ErrorResponseDTO;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.EnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enderecos")
@PreAuthorize("hasAuthority('TELA_ENDERECOS')")
@Tag(name = "Endereços", description = "Cadastro de endereços de famílias")
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @PostMapping
    @Operation(summary = "Cadastro de endereço",
            description = "Cadastra um endereço para ser vinculado a uma família. Exige " +
                    "autenticação JWT e TELA_ENDERECOS.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Endereço cadastrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnderecoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Campos inválidos, corpo ausente ou JSON inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Autenticação ausente ou inválida", content = @Content),
            @ApiResponse(responseCode = "403", description = "Usuário sem a permissão " +
                    "TELA_ENDERECOS", content = @Content)
    })
    public ResponseEntity<EnderecoResponse> cadastrar(@Valid @RequestBody EnderecoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enderecoService.cadastrar(request));
    }
}