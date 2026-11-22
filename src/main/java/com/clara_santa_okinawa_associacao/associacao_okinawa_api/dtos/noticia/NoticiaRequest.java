package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.noticia;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Noticia.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NoticiaRequest(
        @NotBlank @Size(max = 200) String titulo,
        @NotBlank @Size(max = 20) String classificacao,
        @NotBlank @Size(max = 500) String conteudo,
        @NotNull Status status,
        @NotBlank @Size(max = 100) String publico,
        Boolean removerImagem
) {
    public NoticiaRequest {
        removerImagem = Boolean.TRUE.equals(removerImagem);
    }

    public record AlterarStatus(@NotNull Status status) {}
}
