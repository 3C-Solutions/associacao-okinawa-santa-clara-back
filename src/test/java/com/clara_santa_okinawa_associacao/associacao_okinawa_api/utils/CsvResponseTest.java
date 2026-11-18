package com.clara_santa_okinawa_associacao.associacao_okinawa_api.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvResponseTest {
    @Test
    void attachmentRetornaCsvUtf8ComNomeDeArquivo() {
        var response = CsvResponse.attachment("financeiro.csv", new byte[]{1, 2});

        assertEquals(200, response.getStatusCode().value());
        assertEquals(new MediaType("text", "csv", java.nio.charset.StandardCharsets.UTF_8),
                response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).contains("attachment"));
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).contains("financeiro.csv"));
        assertEquals(2, response.getBody().length);
    }
}
