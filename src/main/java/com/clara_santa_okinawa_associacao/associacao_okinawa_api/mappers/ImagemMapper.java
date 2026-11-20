package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.imagem.ImagemResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Imagem;

import java.util.List;

public class ImagemMapper {

    private ImagemMapper() {
    }

    public static ImagemResponse toResponse(Imagem imagem) {

        if (imagem == null) {
            return null;
        }

        // ?v=<nome do arquivo> muda a cada troca de imagem e evita cache velho no navegador
        return new ImagemResponse(
                imagem.getId(),
                "/imagens/" + imagem.getId() + "/arquivo?v=" + imagem.getNomeArquivo()
        );
    }

    public static List<ImagemResponse> toResponse(List<Imagem> imagens) {

        if (imagens == null || imagens.isEmpty()) {
            return List.of();
        }

        return imagens.stream()
                .map(ImagemMapper::toResponse)
                .toList();
    }
}