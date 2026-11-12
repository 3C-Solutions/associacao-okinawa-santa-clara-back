package com.clara_santa_okinawa_associacao.associacao_okinawa_api.components;

import org.springframework.security.oauth2.jwt.Jwt;

public enum FinalidadeToken {
    PRIMEIRO_ACESSO,
    ACESSO;

    public static final String CLAIM = "finalidade";

    public static FinalidadeToken de(boolean primeiroAcesso) {
        return primeiroAcesso ? PRIMEIRO_ACESSO : ACESSO;
    }

    public boolean corresponde(Jwt jwt) {
        return name().equals(jwt.getClaims().get(CLAIM));
    }

    public String autoridade() {
        return "JWT_" + name();
    }

    public static boolean autoridadeReservada(String codigo) {
        return ACESSO.autoridade().equals(codigo) || PRIMEIRO_ACESSO.autoridade().equals(codigo);
    }
}
