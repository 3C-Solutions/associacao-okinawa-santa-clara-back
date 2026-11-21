package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.imagem;

import jakarta.validation.constraints.NotBlank;

public record ImagemRequest(

        @NotBlank
        String url

) {
}