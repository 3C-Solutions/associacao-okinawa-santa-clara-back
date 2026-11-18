package com.clara_santa_okinawa_associacao.associacao_okinawa_api.utils;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvUtilsTest {
    @Test
    void gerarEscapaCamposEUsaSeparadorCompativelComDecimalBrasileiro() {
        byte[] csv = CsvUtils.gerar(
                List.of("Nome", "Valor"),
                Arrays.asList(List.of("Café; \"A\"", "12,50"), Arrays.asList("Linha\n2", null)));

        assertEquals(
                "Nome;Valor\r\n\"Café; \"\"A\"\"\";12,50\r\n\"Linha\n2\";\r\n",
                new String(csv, StandardCharsets.UTF_8));
    }
}
