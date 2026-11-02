package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcionalidade.sistema.FuncionalidadeSistemaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FuncionalidadeSistemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/funcionalidades-sistema")
@PreAuthorize("hasAuthority('TELA_FUNCOES_SISTEMA')")
@Tag(name = "Funcionalidades do sistema", description = "Catálogo de permissões disponíveis para as funções")
public class FuncionalidadeSistemaController {
    private final FuncionalidadeSistemaService service;

    public FuncionalidadeSistemaController(FuncionalidadeSistemaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listagem do catálogo de funcionalidades",
            description = "Lista todas as funcionalidades cadastradas em ordem de ID. Exige TELA_FUNCOES_SISTEMA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catálogo listado; lista vazia quando não há registros",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FuncionalidadeSistemaResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Autenticação ausente ou inválida", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sem permissão para consultar o catálogo", content = @Content)
    })
    public ResponseEntity<List<FuncionalidadeSistemaResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }
}
