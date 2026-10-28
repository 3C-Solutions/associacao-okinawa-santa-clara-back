package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario.UsuarioCreateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario.UsuarioResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario.UsuarioUpdateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.error.ErrorResponseDTO;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.UsuarioService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@PreAuthorize("hasAuthority('TELA_USUARIOS')")
@Tag(
        name = "Usuários",
        description = "Endpoints responsáveis pelo gerenciamento dos usuários"
)
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService associadoService) {
        this.usuarioService = associadoService;
    }

    @Operation(
            summary = "Listagem dos usuários",
            description = "Lista todos os usuários cadastrados no sistema"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuários listados com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rota incorreta para realizar a listagem dos usuários",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                             {
                                             "status": 404,
                                             "message": "Erro ao buscar usuários",
                                             "timestamp": "01/10/2026 10:00:00"\s
                                             }
                                            \s"""
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.listar());
    }

    @Operation(
            summary = "Busca por um usuário específico",
            description = "Busca um usuário pelo identificador único"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UsuarioResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não existe no sistema",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @Parameter(
                    description = "Identificador único do usuário",
                    example = "7"
            )
            @PathVariable("id") Integer id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.buscarPorId(id));
    }

    @Operation(
            summary = "Cadastro de usuários",
            description = "Realiza o cadastro de um novo usuário no sistema"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário cadastrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UsuarioResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados para cadastro do usuário enviados incorretamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                             {
                                             "status": 400,
                                             "message": "Dados para cadastro do usuário enviados incorretamente",
                                             "timestamp": "01/10/2026 10:00:00"\s
                                             }
                                            \s"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Já existe um usuário cadastrado com o e-mail informado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                             {
                                             "status": 409,
                                             "message": "Já existe um usuário cadastrado com o e-mail informado",
                                             "timestamp": "01/10/2026 10:00:00"\s
                                             }
                                            \s"""
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(request));
    }

    @Operation(
            summary = "Atualização de um usuário",
            description = "Atualiza um usuário específico, identificado pelo identificador único"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário atualizado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UsuarioResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados para atualização do usuário enviados incorretamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                             {
                                             "status": 400,
                                             "message": "Dados para atualização do usuário enviados incorretamente",
                                             "timestamp": "01/10/2026 10:00:00"\s
                                             }
                                            \s"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não existe no sistema",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                             {
                                             "status": 404,
                                             "message": "Usuário não existe no sistema",
                                             "timestamp": "01/10/2026 10:00:00"\s
                                             }
                                            \s"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Já existe um usuário cadastrado com o e-mail informado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                             {
                                             "status": 409,
                                             "message": "Já existe um usuário cadastrado com o e-mail informado",
                                             "timestamp": "01/10/2026 10:00:00"\s
                                             }
                                            \s"""
                            )
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> editar(
            @Valid @RequestBody UsuarioUpdateRequest dto,
            @Parameter(
                    description = "Identificador único do associado",
                    example = "7"
            )
            @PathVariable Integer id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.atualizar(id, dto));
    }

    @Operation(
            summary = "Exclusão de um usuário",
            description = "Exclui um usuário do sistema pelo identificador único"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não existe no sistema",
                    content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                             {
                                             "status": 404,
                                             "message": "Usuário não existe no sistema",
                                             "timestamp": "01/10/2026 10:00:00"\s
                                             }
                                            \s"""
                            )
                    ))
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        usuarioService.excluir(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
