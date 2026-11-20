package com.clara_santa_okinawa_associacao.associacao_okinawa_api.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XlsxResponseTest {
    @Test
    void attachmentRetornaXlsxComNomeDeArquivo() {
        var response = XlsxResponse.attachment("financeiro.xlsx", new byte[]{1, 2});

        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.parseMediaType(XlsxResponse.CONTENT_TYPE), response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).contains("attachment"));
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).contains("financeiro.xlsx"));
        assertEquals(2, response.getBody().length);
    }
}
