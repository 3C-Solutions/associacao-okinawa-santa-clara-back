package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.error.ErrorResponseDTO;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaComEnderecoRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FamiliaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@RequestMapping("/familias")
@PreAuthorize("hasAuthority('TELA_FAMILIAS')")
@Tag(name = "Famílias", description = "Cadastro de famílias da associação")
public class FamiliaController {

    private final FamiliaService familiaService;

    public FamiliaController(FamiliaService familiaService) {
        this.familiaService = familiaService;
    }


    @PostMapping
    @Operation(summary = "Cadastro de família",
            description = "Cadastra uma família vinculada a um endereço existente, informado em fkEndereco. "
                    + "Exige autenticação JWT e a permissão TELA_FAMILIAS.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Família cadastrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FamiliaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Campos inválidos, corpo ausente ou JSON inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Autenticação ausente ou inválida", content = @Content),
            @ApiResponse(responseCode = "403", description = "Usuário sem a permissão TELA_FAMILIAS", content = @Content),
            @ApiResponse(responseCode = "404", description = "Endereço informado não existe", content = @Content)
    })
    public ResponseEntity<FamiliaResponse> cadastrar(@Valid @RequestBody FamiliaRequest dto) {
        FamiliaResponse familia = familiaService.cadastrar(dto);

        return ResponseEntity.status(201).body(familia);
    }

    @GetMapping
    @Operation(summary = "Listar famílias", description = "Retorna as famílias e seus endereços, ordenadas por nome e ID. Lista vazia retorna []. Exige TELA_FAMILIAS.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Famílias consultadas"),
            @ApiResponse(responseCode = "401", description = "Autenticação ausente ou inválida", content = @Content),
            @ApiResponse(responseCode = "403", description = "Usuário sem TELA_FAMILIAS", content = @Content)
    })
    public ResponseEntity<List<FamiliaResponse>> listar() {
        return ResponseEntity.ok(familiaService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar família", description = "Retorna a família e seu endereço atual. Exige TELA_FAMILIAS.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Família encontrada"),
            @ApiResponse(responseCode = "401", description = "Autenticação ausente ou inválida", content = @Content),
            @ApiResponse(responseCode = "403", description = "Usuário sem TELA_FAMILIAS", content = @Content),
            @ApiResponse(responseCode = "404", description = "Família não encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<FamiliaResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(familiaService.buscarPorId(id));
    }

    @PostMapping("/com-endereco")
    @Operation(summary = "Cadastrar família com endereço", description = "Cria família e endereço na mesma transação. Exige apenas TELA_FAMILIAS, sem necessidade de TELA_ENDERECOS.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Família e endereço cadastrados"),
            @ApiResponse(responseCode = "400", description = "Campos inválidos ou JSON inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Autenticação ausente ou inválida", content = @Content),
            @ApiResponse(responseCode = "403", description = "Usuário sem TELA_FAMILIAS", content = @Content)
    })
    public ResponseEntity<FamiliaResponse> cadastrarComEndereco(@Valid @RequestBody FamiliaComEnderecoRequest dto) {
        return ResponseEntity.status(201).body(familiaService.cadastrarComEndereco(dto));
    }
}
