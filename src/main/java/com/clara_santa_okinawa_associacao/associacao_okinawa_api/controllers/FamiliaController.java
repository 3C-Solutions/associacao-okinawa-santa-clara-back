package com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FamiliaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/familias")
@PreAuthorize("hasAuthority('TELA_FAMILIAS')")
@Tag(name = "Famílias", description = "Consulta de famílias cadastradas")
public class FamiliaController {
    private final FamiliaService familias;
    public FamiliaController(FamiliaService familias) { this.familias = familias; }
    @GetMapping
    @Operation(summary = "Listar famílias", description = "Famílias e endereços ordenados por nome e ID. Exige TELA_FAMILIAS.")
    public List<FamiliaResponse> listar() { return familias.listar(); }
    @GetMapping("/{id}")
    public FamiliaResponse buscarPorId(@PathVariable Integer id) { return familias.buscarPorId(id); }
}
