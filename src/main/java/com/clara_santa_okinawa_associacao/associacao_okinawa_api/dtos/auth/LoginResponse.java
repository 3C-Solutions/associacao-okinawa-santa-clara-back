package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth;

public record LoginResponse(
        String accesToken,
        String tokenType,
        long expiresIn
) {
}
