package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.AssociadoDependente.AssociadoDependenteRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.AssociadoDependente.AssociadoDependenteResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.AssociadoDependenteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/associado-dependente")
public class AssociadoController {

    private final AssociadoDependenteService associadoDependenteService;

    public AssociadoController(AssociadoDependenteService associadoDependenteService) {
        this.associadoDependenteService = associadoDependenteService;
    }

    @PostMapping
    public ResponseEntity<AssociadoDependenteResponse> cadastrar(@Valid @RequestBody AssociadoDependenteRequest dto){
        AssociadoDependenteResponse associado = associadoDependenteService.cadastrar(dto);
        return ResponseEntity.status(201).body(associado);
    }
}
