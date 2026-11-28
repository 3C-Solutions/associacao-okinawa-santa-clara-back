
package com.clara_santa_okinawa_associacao.associacao_okinawa_api;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.DashboardFinanceiroResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FinanceiroDashboardService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinanceiroDashboardServiceTest {

    private final DespesasRepository despesas = mock(DespesasRepository.class);
    private final PagamentoRepository pagamentos = mock(PagamentoRepository.class);
    private final PagamentoDepartamentoRepository pagamentosDepartamento = mock(PagamentoDepartamentoRepository.class);
    private final AnuidadeRepository anuidades = mock(AnuidadeRepository.class);
    private final MensalidadeRepository mensalidades = mock(MensalidadeRepository.class);
    private final EntradaFinanceiraRepository entradas = mock(EntradaFinanceiraRepository.class);

    private final FinanceiroDashboardService service = new FinanceiroDashboardService(
            despesas, pagamentos, pagamentosDepartamento, anuidades, mensalidades, entradas);

    @BeforeEach
    void configurarRepositorios() {
        when(despesas.findAll()).thenReturn(List.of());
        when(pagamentos.findAll()).thenReturn(List.of());
        when(pagamentosDepartamento.findAll()).thenReturn(List.of());
        when(anuidades.findAll()).thenReturn(List.of());
        when(mensalidades.findAll()).thenReturn(List.of());
        when(entradas.findAll()).thenReturn(List.of());
    }

    private Despesas criarDespesa(Integer id, LocalDate data, String descricao, String categoria,
                                  String valor, String status) {
        Despesas despesa = new Despesas();
        despesa.setIdDespesas(id);
        despesa.setDtDepesa(data);
        despesa.setDescricao(descricao);
        despesa.setCategoria(categoria);
        despesa.setValor(new BigDecimal(valor));
        despesa.setStatus(status);
        despesa.setFkUsuario(7);
        despesa.setFkFamilia(3);
        return despesa;
    }

    private EntradaFinanceira criarEntrada(Integer id, LocalDate data, String descricao,
                                           String categoria, String valor, String status) {
        EntradaFinanceira entrada = mock(EntradaFinanceira.class);

        when(entrada.getId()).thenReturn(id);
        when(entrada.getData()).thenReturn(data);
        when(entrada.getDescricao()).thenReturn(descricao);
        when(entrada.getCategoria()).thenReturn(categoria);
        when(entrada.getValor()).thenReturn(new BigDecimal(valor));
        when(entrada.getStatus()).thenReturn(status);
        when(entrada.getFkUsuario()).thenReturn(7);
        when(entrada.getFkFamilia()).thenReturn(3);

        return entrada;
    }

    private Anuidade criarAnuidade(Integer id, String valor) {
        Anuidade anuidade = new Anuidade();
        anuidade.setIdAnuidade(id);
        anuidade.setValor(new BigDecimal(valor));
        return anuidade;
    }

    private Mensalidade criarMensalidade(Integer id, String valor) {
        Mensalidade mensalidade = new Mensalidade();
        mensalidade.setIdMensalidade(id);
        mensalidade.setValor(new BigDecimal(valor));
        return mensalidade;
    }

    private Pagamento criarPagamento(Integer id, Integer idAnuidade, Integer idFamilia,
                                     LocalDate data, String status) {
        Pagamento pagamento = new Pagamento();
        pagamento.setIdPagamento(id);
        pagamento.setFkAnuidade(idAnuidade);
        pagamento.setFkFamilia(idFamilia);
        pagamento.setDtPagamento(data);
        pagamento.setStatus(status);
        return pagamento;
    }

    private PagamentoDepartamento criarPagamentoDepartamento(Integer id, Integer idMensalidade,
                                                             Integer idDepartamento, LocalDate data,
                                                             String status) {
        PagamentoDepartamento pagamento = new PagamentoDepartamento();
        pagamento.setIdPagamentoDepartamento(id);
        pagamento.setFkMensalidade(idMensalidade);
        pagamento.setFkDepartamento(idDepartamento);
        pagamento.setDtPagamento(data);
        pagamento.setStatus(status);
        return pagamento;
    }

    @Test
    @DisplayName("Deve montar o dashboard com os lançamentos financeiros e calcular os saldos")
    void deveMontarDashboardECalcularSaldosCorretamente() {

        Anuidade anuidade = criarAnuidade(1, "100.00");
        Mensalidade mensalidade = criarMensalidade(2, "50.00");

        EntradaFinanceira entrada1 = criarEntrada(
                1, LocalDate.of(2026, 11, 5), "Doação", "Doação", "30.00", "Recebido"
        );

        EntradaFinanceira entrada2 = criarEntrada(
                2, LocalDate.of(2026, 11, 6), "Evento", "Evento", "40.00", "Pendente"
        );

        Despesas despesa1 = criarDespesa(
                1, LocalDate.of(2026, 11, 7), "Material", "Compras", "20.00", "Pago"
        );

        Despesas despesa2 = criarDespesa(
                2, LocalDate.of(2026, 10, 7), "Transporte", "Transporte", "10.00", "Pago"
        );

        Despesas despesa3 = criarDespesa(
                3, LocalDate.of(2026, 11, 8), "Conta", "Contas", "500.00", "Pendente"
        );

        Pagamento pagamento1 = criarPagamento(
                1, 1, 10, LocalDate.of(2026, 11, 3), "Confirmado"
        );

        Pagamento pagamento2 = criarPagamento(
                2, 999, 11, null, "Pendente"
        );

        PagamentoDepartamento pagamentoDepartamento = criarPagamentoDepartamento(
                1, 2, 5, LocalDate.of(2026, 11, 4), "Pago"
        );

        when(anuidades.findAll()).thenReturn(List.of(anuidade));
        when(mensalidades.findAll()).thenReturn(List.of(mensalidade));
        when(entradas.findAll()).thenReturn(List.of(entrada1, entrada2));
        when(despesas.findAll()).thenReturn(List.of(despesa1, despesa2, despesa3));
        when(pagamentos.findAll()).thenReturn(List.of(pagamento1, pagamento2));
        when(pagamentosDepartamento.findAll()).thenReturn(List.of(pagamentoDepartamento));

        DashboardFinanceiroResponse resultado = service.dashboard(2026, 11);

        assertEquals(new BigDecimal("160.00"), resultado.resumo().saldoMensal());
        assertEquals(new BigDecimal("150.00"), resultado.resumo().saldoAnual());
        assertEquals(new BigDecimal("180.00"), resultado.resumo().entradasAnuais());
        assertEquals(new BigDecimal("30.00"), resultado.resumo().saidasAnuais());
        assertEquals(new BigDecimal("1700.00"), resultado.resumo().variacaoMensal());
        assertEquals(7, resultado.lancamentos().size());

        assertTrue(resultado.lancamentos().stream().anyMatch(item -> item.id().equals("ENT-1")
                && item.status().equals("Confirmado")));

        assertTrue(resultado.lancamentos().stream().anyMatch(item -> item.id().equals("ENT-2")
                && item.status().equals("Pendente")));

        assertTrue(resultado.lancamentos().stream().anyMatch(item -> item.id().equals("PAG-1")
                && item.bloqueado() && item.valor().equals(new BigDecimal("100.00"))));

        assertTrue(resultado.lancamentos().stream().anyMatch(item -> item.id().equals("PDE-1")
                && item.bloqueado() && item.valor().equals(new BigDecimal("50.00"))));
    }

    @Test
    @DisplayName("Deve ordenar os lançamentos por data decrescente e identificador crescente")
    void deveOrdenarLancamentosPorDataEDepoisPorIdentificador() {
        when(despesas.findAll()).thenReturn(List.of(
                criarDespesa(2, LocalDate.of(2026, 11, 10), "Despesa 2", "Outros", "10.00", "Pago"),
                criarDespesa(1, LocalDate.of(2026, 11, 10), "Despesa 1", "Outros", "10.00", "Pago"),
                criarDespesa(3, LocalDate.of(2026, 11, 9), "Despesa 3", "Outros", "10.00", "Pago")));

        var resultado = service.dashboard(2026, 11);

        assertEquals(List.of("DES-1", "DES-2", "DES-3"),
                resultado.lancamentos().stream().map(item -> item.id()).toList());
    }

    @Test
    @DisplayName("Deve ignorar despesas e pagamentos sem data")
    void deveIgnorarDespesasEPagamentosSemData() {
        Despesas despesaSemData = criarDespesa(1, null, "Despesa", "Outros", "10.00", "Pago");

        when(despesas.findAll()).thenReturn(List.of(despesaSemData));
        when(pagamentos.findAll()).thenReturn(List.of(
                criarPagamento(1, 999, 2, null, "Pendente")));
        when(pagamentosDepartamento.findAll()).thenReturn(List.of(
                criarPagamentoDepartamento(1, 999, 3, null, "Pendente")));

        var resultado = service.dashboard(2026, 11);

        assertTrue(resultado.lancamentos().isEmpty());
        assertEquals(BigDecimal.ZERO, resultado.resumo().saldoMensal());
        assertNull(resultado.resumo().variacaoMensal());
    }

    @Test
    @DisplayName("Deve calcular a variação mensal quando o saldo anterior é negativo")
    void deveCalcularVariacaoMensalQuandoSaldoAnteriorForNegativo() {
        when(despesas.findAll()).thenReturn(List.of(
                criarDespesa(1, LocalDate.of(2026, 10, 10), "Despesa anterior", "Outros", "20.00", "Pago"),
                criarDespesa(2, LocalDate.of(2026, 11, 10), "Despesa atual", "Outros", "10.00", "Pago")));

        var resumo = service.dashboard(2026, 11).resumo();

        assertEquals(new BigDecimal("-10.00"), resumo.saldoMensal());
        assertEquals(new BigDecimal("-30.00"), resumo.saldoAnual());
        assertEquals(new BigDecimal("50.00"), resumo.variacaoMensal());
    }

    @Test
    @DisplayName("Deve retornar variação nula quando não houver saldo no mês anterior")
    void deveRetornarVariacaoNulaQuandoSaldoAnteriorForZero() {
        when(despesas.findAll()).thenReturn(List.of(
                criarDespesa(1, LocalDate.of(2026, 11, 10), "Despesa", "Outros", "10.00", "Pago")));

        var resumo = service.dashboard(2026, 11).resumo();

        assertNull(resumo.variacaoMensal());
    }

    @Test
    @DisplayName("Deve considerar dezembro do ano anterior no cálculo de janeiro")
    void deveConsiderarDezembroDoAnoAnteriorNoCalculoDeJaneiro() {
        when(despesas.findAll()).thenReturn(List.of(
                criarDespesa(1, LocalDate.of(2025, 12, 10), "Despesa anterior", "Outros", "100.00", "Pago"),
                criarDespesa(2, LocalDate.of(2026, 1, 10), "Despesa atual", "Outros", "50.00", "Pago")));

        var resumo = service.dashboard(2026, 1).resumo();

        assertEquals(new BigDecimal("-50.00"), resumo.saldoMensal());
        assertEquals(new BigDecimal("-50.00"), resumo.saldoAnual());
        assertEquals(new BigDecimal("50.00"), resumo.variacaoMensal());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o pagamento não possuir anuidade válida")
    void deveLancarExcecaoQuandoPagamentoNaoPossuirAnuidadeValida() {
        when(pagamentos.findAll()).thenReturn(List.of(
                criarPagamento(8, 99, 2, LocalDate.of(2026, 11, 10), "Pago")));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.dashboard(2026, 11));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Pagamento sem Anuidade valida: 8", exception.getReason());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o pagamento de departamento não possuir mensalidade válida")
    void deveLancarExcecaoQuandoPagamentoDepartamentoNaoPossuirMensalidadeValida() {
        when(pagamentosDepartamento.findAll()).thenReturn(List.of(
                criarPagamentoDepartamento(9, 99, 3, LocalDate.of(2026, 11, 10), "Pago")));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.dashboard(2026, 11));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("PagamentoDepartamento sem Mensalidade valida: 9", exception.getReason());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o ano for menor que o permitido")
    void deveLancarExcecaoQuandoAnoForMenorQueOPermitido() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.dashboard(1999, 1));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Ano ou mes invalido", exception.getReason());
        verifyNoInteractions(despesas, pagamentos, pagamentosDepartamento, anuidades, mensalidades, entradas);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o ano for maior que o permitido")
    void deveLancarExcecaoQuandoAnoForMaiorQueOPermitido() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.dashboard(2101, 1));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(despesas, pagamentos, pagamentosDepartamento, anuidades, mensalidades, entradas);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o mês for menor que o permitido")
    void deveLancarExcecaoQuandoMesForMenorQueOPermitido() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.dashboard(2026, 0));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(despesas, pagamentos, pagamentosDepartamento, anuidades, mensalidades, entradas);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o mês for maior que o permitido")
    void deveLancarExcecaoQuandoMesForMaiorQueOPermitido() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.dashboard(2026, 13));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(despesas, pagamentos, pagamentosDepartamento, anuidades, mensalidades, entradas);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o valor de uma entrada financeira estiver ausente")
    void deveLancarExcecaoQuandoValorDaEntradaEstiverAusente() {
        EntradaFinanceira entrada = mock(EntradaFinanceira.class);

        when(entrada.getId()).thenReturn(1);
        when(entrada.getData()).thenReturn(LocalDate.of(2026, 11, 10));
        when(entrada.getValor()).thenReturn(null);

        when(entradas.findAll()).thenReturn(List.of(entrada));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.dashboard(2026, 11));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Valor financeiro ausente no banco", exception.getReason());
    }

    @Test
    @DisplayName("Deve exportar uma planilha com os lançamentos financeiros")
    void deveExportarPlanilhaComLancamentosFinanceiros() throws Exception {

        Despesas despesa1 = criarDespesa(
                1, LocalDate.of(2026, 11, 10), "Compra de material", "Compras", "12.345", "Pago"
        );

        Despesas despesa2 = criarDespesa(
                2, null, "Despesa sem data", "Outros", "20.00", "Pago"
        );

        EntradaFinanceira entrada1 = criarEntrada(
                1, LocalDate.of(2026, 11, 11), "Doação recebida", "Doação", "100.00", "Recebido"
        );

        EntradaFinanceira entrada2 = criarEntrada(
                2, LocalDate.of(2026, 11, 12), "Evento", "Evento", "50.00", "Pendente"
        );

        when(despesas.findAll()).thenReturn(List.of(despesa1, despesa2));
        when(entradas.findAll()).thenReturn(List.of(entrada1, entrada2));

        byte[] arquivo = service.exportarXlsx(2026, 11, null, null, null, null, null);

        assertNotNull(arquivo);
        assertTrue(arquivo.length > 0);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(arquivo))) {
            var planilha = workbook.getSheet("Financeiro");

            assertNotNull(planilha);
            assertEquals(4, planilha.getPhysicalNumberOfRows());
            assertEquals("Data", planilha.getRow(0).getCell(0).getStringCellValue());
            assertEquals("Descrição", planilha.getRow(0).getCell(1).getStringCellValue());
            assertEquals("Compra de material", planilha.getRow(1).getCell(1).getStringCellValue());
            assertEquals(12.35, planilha.getRow(1).getCell(4).getNumericCellValue(), 0.001);
            assertEquals("Pago", planilha.getRow(1).getCell(5).getStringCellValue());
            assertEquals("Doação recebida", planilha.getRow(2).getCell(1).getStringCellValue());
            assertEquals("Recebido", planilha.getRow(2).getCell(5).getStringCellValue());
            assertEquals("Pendente", planilha.getRow(3).getCell(5).getStringCellValue());
        }
    }

    @Test
    @DisplayName("Deve filtrar a exportação por período, tipo, status e busca")
    void deveFiltrarExportacaoPorPeriodoTipoStatusEBusca() throws Exception {

        Despesas despesa1 = criarDespesa(
                1, LocalDate.of(2026, 11, 10),
                "Conta de água", "Utilidades", "30.00", "Pago"
        );

        Despesas despesa2 = criarDespesa(
                2, LocalDate.of(2026, 11, 15),
                "Compra de papel", "Administrativo", "20.00", "Pendente"
        );

        EntradaFinanceira entrada1 = criarEntrada(
                1, LocalDate.of(2026, 11, 15),
                "Mensalidade familiar", "Anuidade", "100.00", "Recebido"
        );

        EntradaFinanceira entrada2 = criarEntrada(
                2, LocalDate.of(2026, 11, 16),
                "Doação", "Doação", "50.00", "Recebido"
        );

        when(despesas.findAll()).thenReturn(List.of(despesa1, despesa2));
        when(entradas.findAll()).thenReturn(List.of(entrada1, entrada2));

        byte[] arquivo = service.exportarXlsx(2026, 11,
                LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 15),
                " entrada ", " pago ", " ANUIDADE ");

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(arquivo))) {
            var planilha = workbook.getSheet("Financeiro");

            assertEquals(2, planilha.getPhysicalNumberOfRows());
            assertEquals("Mensalidade familiar", planilha.getRow(1).getCell(1).getStringCellValue());
            assertEquals("Entrada", planilha.getRow(1).getCell(3).getStringCellValue());
            assertEquals("Recebido", planilha.getRow(1).getCell(5).getStringCellValue());
        }
    }

    @Test
    @DisplayName("Deve filtrar a exportação por saída e status pendente")
    void deveFiltrarExportacaoPorSaidaEStatusPendente() throws Exception {

        Despesas despesa1 = criarDespesa(
                1, LocalDate.of(2026, 11, 10), "Conta de luz", "Utilidades", "80.00", "Pendente"
        );

        Despesas despesa2 = criarDespesa(
                2, LocalDate.of(2026, 11, 11), "Compra", "Outros", "20.00", "Pago"
        );

        EntradaFinanceira entrada = criarEntrada(
                1, LocalDate.of(2026, 11, 12), "Doação", "Doação", "100.00", "Pendente"
        );

        when(despesas.findAll()).thenReturn(List.of(despesa1, despesa2));
        when(entradas.findAll()).thenReturn(List.of(entrada));

        byte[] arquivo = service.exportarXlsx(
                2026, 11, null, null, "SAIDA", "PENDENTE", "lUz"
        );

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(arquivo))) {
            var planilha = workbook.getSheet("Financeiro");

            assertEquals(2, planilha.getPhysicalNumberOfRows());
            assertEquals("Conta de luz", planilha.getRow(1).getCell(1).getStringCellValue());
            assertEquals("Saída", planilha.getRow(1).getCell(3).getStringCellValue());
            assertEquals("Pendente", planilha.getRow(1).getCell(5).getStringCellValue());
        }
    }

    @Test
    @DisplayName("Deve exportar somente o cabeçalho quando não houver lançamentos correspondentes")
    void deveExportarSomenteCabecalhoQuandoNaoHouverLancamentosCorrespondentes() throws Exception {
        byte[] arquivo = service.exportarXlsx(2026, 11, null, null, "ENTRADA", null, null);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(arquivo))) {
            var planilha = workbook.getSheet("Financeiro");

            assertEquals(1, planilha.getPhysicalNumberOfRows());
            assertEquals(6, planilha.getRow(0).getPhysicalNumberOfCells());
        }
    }

    @Test
    @DisplayName("Deve lançar exceção quando o ano ou mês da exportação for inválido")
    void deveLancarExcecaoQuandoAnoOuMesDaExportacaoForInvalido() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.exportarXlsx(1999, 1, null, null, null, null, null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Ano ou mes invalido", exception.getReason());
        verifyNoInteractions(despesas, entradas);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a data inicial for posterior à data final")
    void deveLancarExcecaoQuandoDataInicialForPosteriorADataFinal() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.exportarXlsx(2026, 11,
                        LocalDate.of(2026, 11, 20), LocalDate.of(2026, 11, 10), null, null, null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Intervalo de datas invalido", exception.getReason());
        verifyNoInteractions(despesas, entradas);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o tipo de lançamento for inválido")
    void deveLancarExcecaoQuandoTipoDeLancamentoForInvalido() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.exportarXlsx(2026, 11, null, null, "TRANSFERENCIA", null, null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Tipo de lancamento invalido", exception.getReason());
        verifyNoInteractions(despesas, entradas);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o status do lançamento for inválido")
    void deveLancarExcecaoQuandoStatusDoLancamentoForInvalido() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.exportarXlsx(2026, 11, null, null, null, "CANCELADO", null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Status de lancamento invalido", exception.getReason());
        verifyNoInteractions(despesas, entradas);
    }
}
