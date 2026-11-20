package com.clara_santa_okinawa_associacao.associacao_okinawa_api.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public final class XlsxUtils {
    private XlsxUtils() {
    }

    public static byte[] gerar(String nomePlanilha, List<String> cabecalho, List<? extends List<?>> linhas) {
        if (cabecalho.isEmpty()) {
            throw new IllegalArgumentException("A planilha precisa ter ao menos uma coluna.");
        }

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet planilha = workbook.createSheet(nomePlanilha);
            CellStyle estiloCabecalho = workbook.createCellStyle();
            Font fonteCabecalho = workbook.createFont();
            fonteCabecalho.setBold(true);
            estiloCabecalho.setFont(fonteCabecalho);

            CellStyle estiloData = workbook.createCellStyle();
            estiloData.setDataFormat(workbook.createDataFormat().getFormat("dd/mm/yyyy"));
            CellStyle estiloNumero = workbook.createCellStyle();
            estiloNumero.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

            Row primeiraLinha = planilha.createRow(0);
            for (int coluna = 0; coluna < cabecalho.size(); coluna++) {
                Cell celula = primeiraLinha.createCell(coluna);
                celula.setCellValue(cabecalho.get(coluna));
                celula.setCellStyle(estiloCabecalho);
            }

            for (int indiceLinha = 0; indiceLinha < linhas.size(); indiceLinha++) {
                Row linha = planilha.createRow(indiceLinha + 1);
                List<?> valores = linhas.get(indiceLinha);
                for (int coluna = 0; coluna < valores.size(); coluna++) {
                    preencherCelula(linha.createCell(coluna), valores.get(coluna), estiloData, estiloNumero);
                }
            }

            planilha.createFreezePane(0, 1);
            planilha.setAutoFilter(new CellRangeAddress(0, linhas.size(), 0, cabecalho.size() - 1));
            for (int coluna = 0; coluna < cabecalho.size(); coluna++) {
                planilha.autoSizeColumn(coluna);
                planilha.setColumnWidth(coluna, Math.min(planilha.getColumnWidth(coluna), 60 * 256));
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Não foi possível gerar o arquivo XLSX.", ex);
        }
    }

    private static void preencherCelula(Cell celula, Object valor, CellStyle estiloData, CellStyle estiloNumero) {
        if (valor == null) {
            celula.setBlank();
        } else if (valor instanceof LocalDate data) {
            celula.setCellValue(Date.valueOf(data));
            celula.setCellStyle(estiloData);
        } else if (valor instanceof BigDecimal numero) {
            celula.setCellValue(numero.doubleValue());
            celula.setCellStyle(estiloNumero);
        } else if (valor instanceof Number numero) {
            celula.setCellValue(numero.doubleValue());
            celula.setCellStyle(estiloNumero);
        } else if (valor instanceof Boolean booleano) {
            celula.setCellValue(booleano);
        } else {
            celula.setCellValue(Objects.toString(valor));
        }
    }
}
