package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.imagem.ImagemRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.imagem.ImagemResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Imagem;

import java.util.List;

public class ImagemMapper {

    private ImagemMapper() {
    }

    public static Imagem toEntity(ImagemRequest dto) {

        Imagem imagem = new Imagem();

        imagem.setUrl(dto.url().trim());

        return imagem;
    }

    public static ImagemResponse toResponse(Imagem imagem) {

        if (imagem == null) {
            return null;
        }

        return new ImagemResponse(
                imagem.getId(),
                imagem.getUrl()
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