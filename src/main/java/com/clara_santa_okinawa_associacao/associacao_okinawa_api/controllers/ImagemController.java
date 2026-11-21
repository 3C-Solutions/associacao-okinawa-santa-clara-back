package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.imagem.ImagemResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Imagem;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.ImagemMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.ImagemService;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/imagens")
public class ImagemController {

    private final ImagemService imagemService;

    public ImagemController(ImagemService imagemService) {
        this.imagemService = imagemService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImagemResponse> cadastrar(
            @RequestPart("arquivo") MultipartFile arquivo
    ) {
        Imagem cadastrada = imagemService.cadastrar(arquivo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ImagemMapper.toResponse(cadastrada));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImagemResponse> editar(
            @PathVariable Integer id,
            @RequestPart("arquivo") MultipartFile arquivo
    ) {
        Imagem atualizada = imagemService.editar(id, arquivo);
        return ResponseEntity.ok(ImagemMapper.toResponse(atualizada));
    }

    @GetMapping
    public ResponseEntity<List<ImagemResponse>> listar() {
        return ResponseEntity.ok(ImagemMapper.toResponse(imagemService.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImagemResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ImagemMapper.toResponse(imagemService.buscarPorId(id)));
    }

    @GetMapping("/{id}/arquivo")
    public ResponseEntity<byte[]> arquivo(@PathVariable Integer id) {
        var arquivo = imagemService.carregar(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(arquivo.getTipoConteudo()))
                .cacheControl(CacheControl.noCache())
                .body(arquivo.getConteudo());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        imagemService.excluir(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
