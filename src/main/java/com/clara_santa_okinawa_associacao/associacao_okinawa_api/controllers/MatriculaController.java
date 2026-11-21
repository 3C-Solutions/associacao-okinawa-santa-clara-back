package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.matricula.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Matricula;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.MatriculaMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.MatriculaService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PostMapping
    public ResponseEntity<MatriculaResponse> cadastrar(
            @Valid @RequestBody MatriculaRequest dto
    ) {

        Matricula matricula = MatriculaMapper.toEntity(dto);

        Matricula cadastrada = matriculaService.cadastrar(matricula);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MatriculaMapper.toResponse(cadastrada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatriculaResponse> editar(
            @PathVariable Integer id,
            @Valid @RequestBody MatriculaRequest dto
    ) {

        Matricula matricula = MatriculaMapper.toEntity(dto);

        Matricula atualizada = matriculaService.editar(id, matricula);

        return ResponseEntity.status(HttpStatus.OK)
                .body(MatriculaMapper.toResponse(atualizada));
    }

    @GetMapping
    public ResponseEntity<List<MatriculaResponse>> listar() {

        List<Matricula> matriculas = matriculaService.listar();

        return ResponseEntity.status(HttpStatus.OK)
                .body(MatriculaMapper.toResponse(matriculas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatriculaResponse> buscarPorId(
            @PathVariable Integer id
    ) {

        Matricula matricula = matriculaService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(MatriculaMapper.toResponse(matricula));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {

        matriculaService.excluir(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}