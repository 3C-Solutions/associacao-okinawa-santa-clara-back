package com.clara_santa_okinawa_associacao.associacao_okinawa_api.utils;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XlsxUtilsTest {
    @Test
    void gerarCriaPlanilhaComCabecalhoDataEValorNumerico() throws Exception {
        byte[] arquivo = XlsxUtils.gerar(
                "Financeiro",
                List.of("Data", "Descrição", "Valor"),
                List.of(List.of(LocalDate.of(2026, 11, 18), "Materiais", new BigDecimal("45.50"))));

        try (var workbook = new XSSFWorkbook(new ByteArrayInputStream(arquivo))) {
            var planilha = workbook.getSheet("Financeiro");
            assertEquals("Data", planilha.getRow(0).getCell(0).getStringCellValue());
            assertTrue(planilha.getRow(0).getCell(0).getCellStyle().getFont().getBold());
            assertTrue(DateUtil.isCellDateFormatted(planilha.getRow(1).getCell(0)));
            assertEquals("Materiais", planilha.getRow(1).getCell(1).getStringCellValue());
            assertEquals(45.50, planilha.getRow(1).getCell(2).getNumericCellValue(), 0.001);
            assertEquals(1, planilha.getPaneInformation().getHorizontalSplitPosition());
        }
    }
}
