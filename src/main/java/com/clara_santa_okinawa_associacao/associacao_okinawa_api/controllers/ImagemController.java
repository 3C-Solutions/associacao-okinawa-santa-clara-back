package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.imagem.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Imagem;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.ImagemMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.ImagemService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/imagens")
public class ImagemController {

    private final ImagemService imagemService;

    public ImagemController(ImagemService imagemService) {
        this.imagemService = imagemService;
    }

    @PostMapping
    public ResponseEntity<ImagemResponse> cadastrar(
            @Valid @RequestBody ImagemRequest dto
    ) {

        Imagem imagem = ImagemMapper.toEntity(dto);

        Imagem cadastrada = imagemService.cadastrar(imagem);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ImagemMapper.toResponse(cadastrada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImagemResponse> editar(
            @PathVariable Integer id,
            @Valid @RequestBody ImagemRequest dto
    ) {

        Imagem imagem = ImagemMapper.toEntity(dto);

        Imagem atualizada = imagemService.editar(id, imagem);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ImagemMapper.toResponse(atualizada));
    }

    @GetMapping
    public ResponseEntity<List<ImagemResponse>> listar() {

        List<Imagem> imagens = imagemService.listar();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ImagemMapper.toResponse(imagens));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImagemResponse> buscarPorId(
            @PathVariable Integer id
    ) {

        Imagem imagem = imagemService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ImagemMapper.toResponse(imagem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {

        imagemService.excluir(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}