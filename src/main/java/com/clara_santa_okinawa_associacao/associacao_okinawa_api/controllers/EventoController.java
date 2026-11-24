
package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.evento.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.EventoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/eventos")
@PreAuthorize("hasAuthority('TELA_AGENDA')")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    private Integer usuarioId(
            JwtAuthenticationToken authentication
    ) {
        return Integer.valueOf(
                authentication.getToken().getSubject()
        );
    }

    @GetMapping("/reservantes")
    public ResponseEntity<ReservantesAgendaResponse> reservantes(
            JwtAuthenticationToken authentication
    ) {
        return ResponseEntity.ok(
                service.listarReservantes(
                        usuarioId(authentication)
                )
        );
    }

    @GetMapping("/calendario")
    public ResponseEntity<List<EventoResumoResponse>> calendario(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fim,

            JwtAuthenticationToken authentication
    ) {
        return ResponseEntity.ok(
                service.calendario(
                        usuarioId(authentication),
                        inicio,
                        fim
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<EventoResponse>> listar(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fim,

            JwtAuthenticationToken authentication
    ) {
        return ResponseEntity.ok(
                service.listar(
                        usuarioId(authentication),
                        inicio,
                        fim
                )
        );
    }

    @GetMapping("/solicitacoes")
    public ResponseEntity<List<EventoResponse>> solicitacoes(
            JwtAuthenticationToken authentication
    ) {
        return ResponseEntity.ok(
                service.solicitacoes(
                        usuarioId(authentication)
                )
        );
    }

    @GetMapping("/minhas-solicitacoes")
    public ResponseEntity<List<EventoResponse>> minhasSolicitacoes(
            JwtAuthenticationToken authentication
    ) {
        return ResponseEntity.ok(
                service.minhasSolicitacoes(
                        usuarioId(authentication)
                )
        );
    }

    @GetMapping("/minhas-solicitacoes/{id}")
    public ResponseEntity<EventoResponse> minhaSolicitacao(
            @PathVariable Integer id,
            JwtAuthenticationToken authentication
    ) {
        return ResponseEntity.ok(
                service.minhaSolicitacao(
                        id,
                        usuarioId(authentication)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> buscar(
            @PathVariable Integer id,
            JwtAuthenticationToken authentication
    ) {
        return ResponseEntity.ok(
                service.buscar(
                        id,
                        usuarioId(authentication)
                )
        );
    }

    @PostMapping
    public ResponseEntity<EventoResponse> criar(
            @Valid @RequestBody EventoRequest request,
            JwtAuthenticationToken authentication
    ) {
        Integer usuarioId = usuarioId(authentication);

        EventoResponse novo = service.criar(
                request,
                usuarioId
        );

        return ResponseEntity.status(201).body(
                service.sincronizar(novo.id(), usuarioId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponse> editar(
            @PathVariable Integer id,
            @Valid @RequestBody EventoRequest request,
            JwtAuthenticationToken authentication
    ) {
        Integer usuarioId = usuarioId(authentication);

        service.editar(id, request, usuarioId);

        return ResponseEntity.ok(
                service.sincronizar(id, usuarioId)
        );
    }

    @PostMapping("/solicitacoes")
    public ResponseEntity<EventoResponse> solicitar(
            @Valid @RequestBody EventoRequest request,
            JwtAuthenticationToken authentication
    ) {
        return ResponseEntity.status(201).body(
                service.solicitar(
                        request,
                        usuarioId(authentication)
                )
        );
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<EventoResponse> aprovar(
            @PathVariable Integer id,
            JwtAuthenticationToken authentication
    ) {
        Integer usuarioId = usuarioId(authentication);

        service.aprovar(id, usuarioId);

        return ResponseEntity.ok(
                service.sincronizar(id, usuarioId)
        );
    }

    @PatchMapping("/{id}/recusar")
    public ResponseEntity<EventoResponse> recusar(
            @PathVariable Integer id,
            @Valid @RequestBody RecusaEventoRequest request,
            JwtAuthenticationToken authentication
    ) {
        return ResponseEntity.ok(
                service.recusar(
                        id,
                        request.motivoRecusa(),
                        usuarioId(authentication)
                )
        );
    }

    @PatchMapping("/{id}/cancelar-solicitacao")
    public ResponseEntity<EventoResponse> cancelarSolicitacao(
            @PathVariable Integer id,
            JwtAuthenticationToken authentication
    ) {
        return ResponseEntity.ok(
                service.cancelarSolicitacao(
                        id,
                        usuarioId(authentication)
                )
        );
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<EventoResponse> cancelar(
            @PathVariable Integer id,
            JwtAuthenticationToken authentication
    ) {
        Integer usuarioId = usuarioId(authentication);

        service.cancelar(id, usuarioId);

        return ResponseEntity.ok(
                service.sincronizar(id, usuarioId)
        );
    }

    @PatchMapping("/{id}/sincronizar")
    public ResponseEntity<EventoResponse> sincronizar(
            @PathVariable Integer id,
            JwtAuthenticationToken authentication
    ) {
        return ResponseEntity.ok(
                service.sincronizar(
                        id,
                        usuarioId(authentication)
                )
        );
    }
}
