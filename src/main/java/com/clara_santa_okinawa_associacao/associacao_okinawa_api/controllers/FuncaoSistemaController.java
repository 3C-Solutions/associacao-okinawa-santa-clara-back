package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.error.ErrorResponseDTO;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaCreateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FuncaoSistemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaDetalheResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaFuncionalidadesRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/funcoes-sistema")
@PreAuthorize("hasAuthority('TELA_FUNCOES_SISTEMA')")
@Tag(name = "Funções do sistema", description = "Cadastro e consulta dos perfis do sistema")
public class FuncaoSistemaController {

    private final FuncaoSistemaService funcaoSistemaService;

    public FuncaoSistemaController(FuncaoSistemaService funcaoSistemaService) {
        this.funcaoSistemaService = funcaoSistemaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('TELA_FUNCOES_SISTEMA', 'TELA_USUARIOS')")
    @Operation(summary = "Listagem das funções cadastradas",
            description = "Lista todas as funções em ordem de ID. Exige TELA_FUNCOES_SISTEMA ou TELA_USUARIOS.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Funções listadas; lista vazia quando não há registros",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FuncaoSistemaResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Autenticação ausente ou inválida", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sem permissão para consultar funções", content = @Content)
    })
    public ResponseEntity<List<FuncaoSistemaResponse>> listar() {
        return ResponseEntity.ok(funcaoSistemaService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta uma função com suas funcionalidades", description = "Exige TELA_FUNCOES_SISTEMA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Função encontrada"),
            @ApiResponse(responseCode = "401", description = "Autenticação ausente ou inválida", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content),
            @ApiResponse(responseCode = "404", description = "Função não encontrada", content = @Content)
    })
    public ResponseEntity<FuncaoSistemaDetalheResponse> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(funcaoSistemaService.buscar(id));
    }

    @PutMapping("/{id}/funcionalidades")
    @Operation(summary = "Substitui as funcionalidades da função", description = "Exige TELA_FUNCOES_SISTEMA. Aceita lista vazia para remover todos os acessos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Acessos atualizados"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação ausente ou inválida", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content),
            @ApiResponse(responseCode = "404", description = "Função ou funcionalidade não encontrada", content = @Content)
    })
    public ResponseEntity<FuncaoSistemaDetalheResponse> atualizarFuncionalidades(@PathVariable Integer id,
            @Valid @RequestBody FuncaoSistemaFuncionalidadesRequest request) {
        return ResponseEntity.ok(funcaoSistemaService.atualizarFuncionalidades(id, request));
    }

    @PostMapping
    @Operation(summary = "Cadastro de função do sistema",
            description = "Cria uma função vinculada a funcionalidades existentes. Exige TELA_FUNCOES_SISTEMA.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Função cadastrada com sucesso",
                    content = @Content(schema = @Schema(implementation = FuncaoSistemaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou JSON malformado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Autenticação ausente ou inválida", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sem permissão para cadastrar funções", content = @Content),
            @ApiResponse(responseCode = "404", description = "Uma ou mais funcionalidades não existem",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Nome de função já cadastrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<FuncaoSistemaResponse> cadastrar(@Valid @RequestBody FuncaoSistemaCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(funcaoSistemaService.cadastrar(request));
    }
}
