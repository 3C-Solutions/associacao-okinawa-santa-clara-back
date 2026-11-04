package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.Locale;
public final class FinanceiroRegras {
    private FinanceiroRegras() {}
    public static boolean pago(String status) {
        return status != null && (status.trim().equalsIgnoreCase("Pago") ||
                status.trim().equalsIgnoreCase("Confirmado"));
    }
    public static String status(String valor) {
        if (valor == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status obrigatorio");
        return switch(valor.trim().toLowerCase(Locale.ROOT)) {
            case "pago", "confirmado" -> "Pago";
            case "pendente" -> "Pendente";
            case "recusado" -> "Recusado";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status invalido: " +
                    valor);
        };
    }
    public static BigDecimal valor(BigDecimal x) {
        if (x == null) throw new ResponseStatusException(HttpStatus.CONFLICT, "Valor financeiro ausente no banco");
        return x;
    }
}
