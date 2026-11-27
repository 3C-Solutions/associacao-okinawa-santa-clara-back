package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReembolsoRecusarRequest(
        @NotBlank @Size(min = 3, max = 500) String motivo
) {}
