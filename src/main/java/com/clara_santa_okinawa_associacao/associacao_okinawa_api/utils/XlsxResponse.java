package com.clara_santa_okinawa_associacao.associacao_okinawa_api.utils;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;

public final class XlsxResponse {
    public static final String CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private XlsxResponse() {
    }

    public static ResponseEntity<byte[]> attachment(String filename, byte[] content) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(CONTENT_TYPE))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build().toString())
                .body(content);
    }
}
