package com.clara_santa_okinawa_associacao.associacao_okinawa_api.utils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public final class CsvUtils {
    private static final char DELIMITADOR = ';';
    private static final String QUEBRA_DE_LINHA = "\r\n";

    private CsvUtils() {
    }

    public static byte[] gerar(List<String> cabecalho, List<List<String>> linhas) {
        StringBuilder csv = new StringBuilder();
        adicionarLinha(csv, cabecalho);
        for (List<String> linha : linhas) {
            adicionarLinha(csv, linha);
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static void adicionarLinha(StringBuilder csv, List<String> campos) {
        for (int i = 0; i < campos.size(); i++) {
            if (i > 0) {
                csv.append(DELIMITADOR);
            }
            adicionarCampo(csv, Objects.toString(campos.get(i), ""));
        }
        csv.append(QUEBRA_DE_LINHA);
    }

    private static void adicionarCampo(StringBuilder csv, String campo) {
        boolean precisaAspas = campo.indexOf(DELIMITADOR) >= 0
                || campo.indexOf('"') >= 0
                || campo.indexOf('\r') >= 0
                || campo.indexOf('\n') >= 0;
        if (precisaAspas) {
            csv.append('"').append(campo.replace("\"", "\"\"")).append('"');
        } else {
            csv.append(campo);
        }
    }
}
