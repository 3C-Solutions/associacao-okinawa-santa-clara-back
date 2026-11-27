package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ComprovanteReembolsoRequest(
        @NotBlank @Size(max = 255) String nome,
        @NotBlank @Size(max = 50) String tipo,
        @NotBlank @Size(max = 1_398_200) String dados
) {}
