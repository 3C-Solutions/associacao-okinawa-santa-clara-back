package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.associado.AssociadoCreateRequestDTO;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.associado.AssociadoResponseDTO;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.associado.AssociadoUpdateRequestDTO;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.error.ErrorResponseDTO;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.AssociadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/associados")
@Tag(
        name = "Associados",
        description = "Endpoints responsáveis pelo gerenciamento dos associados"
)
public class AssociadoController {
    private final AssociadoService associadoService;

    public AssociadoController(AssociadoService associadoService) {
        this.associadoService = associadoService;
    }

    @Operation(
            summary = "Listagem dos associados",
            description = "Lista todos os associados cadastrados no sistema"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Associados listados com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rota incorreta para realizar a listagem dos associados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                             {
                                             "status": 404,
                                             "message": "Erro ao buscar associados",
                                             "timestamp": "01/10/2026 10:00:00"\s
                                             }
                                            \s"""
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<AssociadoResponseDTO>> listar() {
        return ResponseEntity.status(HttpStatus.OK).body(associadoService.listar());
    }

    @Operation(
            summary = "Busca por um associado específico",
            description = "Busca um associado pelo identificador único"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Associado encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = AssociadoResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Associado não existe no sistema",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AssociadoResponseDTO> buscarPorId(
            @Parameter(
                    description = "Identificador único do associado",
                    example = "7"
            )
            @PathVariable Integer id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(associadoService.buscarPorId(id));
    }

    @Operation(
            summary = "Cadastro de associados",
            description = "Realiza o cadastro de um novo associado no sistema"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Associado cadastrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = AssociadoResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados para cadastro do associado enviados incorretamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                             {
                                             "status": 400,
                                             "message": "Dados para cadastro do associado enviados incorretamente",
                                             "timestamp": "01/10/2026 10:00:00"\s
                                             }
                                            \s"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Já existe um associado cadastrado com o e-mail informado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                             {
                                             "status": 409,
                                             "message": "Já existe um associado cadastrado com o e-mail informado",
                                             "timestamp": "01/10/2026 10:00:00"\s
                                             }
                                            \s"""
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<AssociadoResponseDTO> cadastrar(@Valid @RequestBody AssociadoCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(associadoService.cadastrar(request));
    }

    @Operation(
            summary = "Atualização de um associado",
            description = "Atualiza um associado específico, identificado pelo identificador único"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Associado atualizado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = AssociadoResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados para atualização do associado enviados incorretamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                             {
                                             "status": 400,
                                             "message": "Dados para atualização do associado enviados incorretamente",
                                             "timestamp": "01/10/2026 10:00:00"\s
                                             }
                                            \s"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Associado não existe no sistema",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                             {
                                             "status": 404,
                                             "message": "Associado não existe no sistema",
                                             "timestamp": "01/10/2026 10:00:00"\s
                                             }
                                            \s"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Já existe um associado cadastrado com o e-mail informado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                             {
                                             "status": 409,
                                             "message": "Já existe um associado cadastrado com o e-mail informado",
                                             "timestamp": "01/10/2026 10:00:00"\s
                                             }
                                            \s"""
                            )
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<AssociadoResponseDTO> editar(
            @Valid @RequestBody AssociadoUpdateRequestDTO dto,
            @Parameter(
                    description = "Identificador único do associado",
                    example = "7"
            )
            @PathVariable Integer id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(associadoService.atualizar(dto, id));
    }

    @Operation(
            summary = "Exclusão de um associado",
            description = "Exclui um associado do sistema pelo identificador único"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Associado excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Associado não existe no sistema")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        associadoService.excluir(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
