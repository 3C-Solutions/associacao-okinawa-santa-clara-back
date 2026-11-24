package com.clara_santa_okinawa_associacao.associacao_okinawa_api;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.*;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FinanceiroTests {
    private final DespesasRepository despesas = mock(DespesasRepository.class);
    private final PagamentoRepository pagamentos = mock(PagamentoRepository.class);
    private final PagamentoDepartamentoRepository departamentos = mock(PagamentoDepartamentoRepository.class);
    private final AnuidadeRepository anuidades = mock(AnuidadeRepository.class);
    private final MensalidadeRepository mensalidades = mock(MensalidadeRepository.class);
    private final FamiliaRepository familias = mock(FamiliaRepository.class);
    private final EntradaFinanceiraRepository entradas = mock(EntradaFinanceiraRepository.class);
    private final FinanceiroDashboardService dashboard = new FinanceiroDashboardService(
            despesas, pagamentos, departamentos, anuidades, mensalidades, entradas);

    private Despesas despesa(int id, String data, String valor, String status) {
        Despesas item = new Despesas();
        item.setIdDespesas(id);
        item.setDtDepesa(LocalDate.parse(data));
        item.setValor(new BigDecimal(valor));
        item.setStatus(status);
        item.setFkUsuario(7);
        item.setDescricao("Materiais");
        item.setCategoria("Outros");
        return item;
    }

    @Test
    void saldosConsideramSomenteConfirmadosEDatasDoPeriodo() {
        Anuidade anuidade = new Anuidade();
        anuidade.setIdAnuidade(1);
        anuidade.setValor(new BigDecimal("100.10"));
        Mensalidade mensalidade = new Mensalidade();
        mensalidade.setIdMensalidade(2);
        mensalidade.setValor(new BigDecimal("50.20"));
        Pagamento entrada = new Pagamento();
        entrada.setIdPagamento(1);
        entrada.setFkAnuidade(1);
        entrada.setDtPagamento(LocalDate.of(2026, 11, 1));
        entrada.setStatus(" Confirmado ");
        PagamentoDepartamento departamento = new PagamentoDepartamento();
        departamento.setIdPagamentoDepartamento(2);
        departamento.setFkMensalidade(2);
        departamento.setDtPagamento(LocalDate.of(2026, 11, 2));
        departamento.setStatus("Pago");
        when(anuidades.findAll()).thenReturn(List.of(anuidade));
        when(mensalidades.findAll()).thenReturn(List.of(mensalidade));
        when(pagamentos.findAll()).thenReturn(List.of(entrada));
        when(departamentos.findAll()).thenReturn(List.of(departamento));
        when(despesas.findAll()).thenReturn(List.of(
                despesa(1, "2026-11-03", "20.05", "Pago"),
                despesa(2, "2026-10-03", "10.00", "Pago"),
                despesa(3, "2026-11-04", "999.00", "Pendente"),
                despesa(4, "2025-11-04", "999.00", "Pago"),
                despesa(5, "2026-11-05", "999.00", "Recusado")));
        var resultado = dashboard.dashboard(2026, 11);
        var resumo = resultado.resumo();
        assertEquals(0, new BigDecimal("130.25").compareTo(resumo.saldoMensal()));
        assertEquals(0, new BigDecimal("120.25").compareTo(resumo.saldoAnual()));
        assertEquals(0, new BigDecimal("150.30").compareTo(resumo.entradasAnuais()));
        assertEquals(0, new BigDecimal("30.05").compareTo(resumo.saidasAnuais()));
        assertEquals(0, new BigDecimal("1402.50").compareTo(resumo.variacaoMensal()));
        assertTrue(resultado.lancamentos().stream().filter(t -> t.tipo().equals("ENTRADA")).allMatch(LancamentoFinanceiroResponse::bloqueado));
    }

    @Test
    void janeiroComparaDezembroDoAnoAnteriorSemMisturarSaldoAnual() {
        when(despesas.findAll()).thenReturn(List.of(despesa(1, "2025-12-10", "100", "Pago"), despesa(2, "2026-01-10", "50", "Pago")));
        var resumo = dashboard.dashboard(2026, 1).resumo();
        assertEquals(0, new BigDecimal("-50").compareTo(resumo.saldoAnual()));
        assertEquals(0, new BigDecimal("50").compareTo(resumo.variacaoMensal()));
    }

    @Test
    void semMovimentacaoNaoInventaVariacao() {
        var resumo = dashboard.dashboard(2026, 11).resumo();
        assertEquals(0, BigDecimal.ZERO.compareTo(resumo.saldoMensal()));
        assertNull(resumo.variacaoMensal());
    }

    @Test
    void rejeitaPeriodoInvalidoAntesDeConsultarBanco() {
        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class, () -> dashboard.dashboard(2026, 13)).getStatusCode());
        verifyNoInteractions(despesas, pagamentos, departamentos, anuidades, mensalidades);
    }

    @Test
    void criacaoUsaResponsavelAutenticadoEEdicaoPreservaResponsavel() {
        var service = new DespesasService(despesas, familias);
        when(despesas.save(any())).thenAnswer(invocacao -> invocacao.getArgument(0));
        var criado = service.criar(new DespesasCreateRequest(LocalDate.of(2026, 11, 3), " Materiais ", " Outros ", new BigDecimal("12.50"), "pago", 999, null), 7);
        assertEquals(7, criado.fkUsuario());
        assertEquals("Materiais", criado.descricao());
        var original = despesa(1, "2026-11-03", "12.50", "Pago");
        when(despesas.findById(1)).thenReturn(Optional.of(original));
        var editado = service.editar(1, new DespesasUpdateRequest(LocalDate.of(2026, 11, 4), "Materiais novos", "Outros", new BigDecimal("20"), "Pago", 999, null));
        assertEquals(7, editado.fkUsuario());
    }

    @Test
    void naoGravaFamiliaInexistenteNemConfirmaDespesaRecusada() {
        var service = new DespesasService(despesas, familias);
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ResponseStatusException.class, () -> service.criar(new DespesasCreateRequest(LocalDate.now(), "Materiais", "Outros", BigDecimal.ONE, "Pago", null, 999), 7)).getStatusCode());
        when(despesas.findById(1)).thenReturn(Optional.of(despesa(1, "2026-11-03", "10", "Recusado")));
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class, () -> service.confirmar(1)).getStatusCode());
        verify(despesas, never()).save(any());
    }

    @Test
    void validacaoRejeitaValorNegativoFracionamentoInvalidoEVinculoInvalido() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertFalse(validator.validate(new DespesasCreateRequest(LocalDate.now(), "Materiais", "Outros", new BigDecimal("-1"), "Pago", null, null)).isEmpty());
            assertFalse(validator.validate(new DespesasCreateRequest(LocalDate.now(), "Materiais", "Outros", new BigDecimal("1.001"), "Pago", null, null)).isEmpty());
            assertFalse(validator.validate(new DespesasCreateRequest(LocalDate.now(), "Materiais", "Outros", BigDecimal.ONE, "Pago", null, 0)).isEmpty());
            assertTrue(validator.validate(new DespesasCreateRequest(LocalDate.now(), "Materiais", "Outros", new BigDecimal("0.01"), "Pago", null, null)).isEmpty());
        }
    }
}
