package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.noticia.NoticiaRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.noticia.NoticiaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Noticia.Status;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.NoticiaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/noticias")
@Tag(name = "Notícias")
public class NoticiaController {
    private static final String GESTAO = "GERENCIAR_NOTICIAS";
    private static final String PODE_GERENCIAR = "hasAuthority('" + GESTAO + "')";
    private static final int MAX_TAMANHO = 100;
    private final NoticiaService servico;

    public NoticiaController(NoticiaService servico) {
        this.servico = servico;
    }

    @GetMapping
    public NoticiaResponse.Pagina listar(JwtAuthenticationToken auth,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "") @Size(max = 200) String busca,
            @RequestParam(defaultValue = "0") @Min(0) @Max(Integer.MAX_VALUE / MAX_TAMANHO) int pagina,
            @RequestParam(defaultValue = "20") @Min(1) @Max(MAX_TAMANHO) int tamanho) {
        return servico.listar(usuarioId(auth), gestor(auth), status, busca, PageRequest.of(pagina, tamanho));
    }

    @GetMapping("/publicos")
    @PreAuthorize(PODE_GERENCIAR)
    public List<String> listarPublicos() {
        return servico.listarPublicos();
    }

    @GetMapping("/{id}")
    public NoticiaResponse consultar(@PathVariable Integer id, JwtAuthenticationToken auth) {
        return servico.consultar(usuarioId(auth), gestor(auth), id);
    }

    @GetMapping("/{id}/imagem")
    public ResponseEntity<byte[]> capa(@PathVariable Integer id, JwtAuthenticationToken auth) {
        var arquivo = servico.carregarCapa(usuarioId(auth), gestor(auth), id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(arquivo.getTipoConteudo()))
                .cacheControl(CacheControl.noStore()).body(arquivo.getConteudo());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(PODE_GERENCIAR)
    public ResponseEntity<Void> cadastrar(@Valid @RequestPart("dados") NoticiaRequest dados,
            @RequestPart(value = "imagem", required = false) MultipartFile arquivo) {
        Integer id = servico.cadastrar(dados, arquivo);
        return ResponseEntity.created(URI.create("noticias/" + id)).build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(PODE_GERENCIAR)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editar(@PathVariable Integer id, JwtAuthenticationToken auth,
            @Valid @RequestPart("dados") NoticiaRequest dados,
            @RequestPart(value = "imagem", required = false) MultipartFile arquivo) {
        servico.editar(usuarioId(auth), id, dados, arquivo);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize(PODE_GERENCIAR)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarStatus(@PathVariable Integer id, JwtAuthenticationToken auth,
            @Valid @RequestBody NoticiaRequest.AlterarStatus dados) {
        servico.alterarStatus(usuarioId(auth), id, dados.status());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(PODE_GERENCIAR)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Integer id, JwtAuthenticationToken auth) {
        servico.excluir(usuarioId(auth), id);
    }

    private Integer usuarioId(JwtAuthenticationToken auth) {
        return Integer.valueOf(auth.getToken().getSubject());
    }

    private boolean gestor(JwtAuthenticationToken auth) {
        return auth.getAuthorities().stream().anyMatch(a -> GESTAO.equals(a.getAuthority()));
    }
}
