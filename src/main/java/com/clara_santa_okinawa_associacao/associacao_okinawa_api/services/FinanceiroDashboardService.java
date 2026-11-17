package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.utils.CsvUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Service
public class FinanceiroDashboardService {
    private final DespesasRepository despesas;
    private final PagamentoRepository pagamentos;
    private final PagamentoDepartamentoRepository pagamentosDepartamento;
    private final AnuidadeRepository anuidades;
    private final MensalidadeRepository mensalidades;
    private final EntradaFinanceiraRepository entradas;
    public FinanceiroDashboardService(DespesasRepository despesas, PagamentoRepository pagamentos,
                                      PagamentoDepartamentoRepository pagamentosDepartamento, AnuidadeRepository anuidades,
                                      MensalidadeRepository mensalidades, EntradaFinanceiraRepository entradas) {
        this.despesas = despesas;
        this.pagamentos = pagamentos;
        this.pagamentosDepartamento = pagamentosDepartamento;
        this.anuidades = anuidades;
        this.mensalidades = mensalidades;
        this.entradas = entradas;
    }
    private BigDecimal saldo(List<LancamentoFinanceiroResponse> itens, YearMonth periodo) {
        BigDecimal total = BigDecimal.ZERO;
        for (var t : itens) {
            if (!FinanceiroRegras.pago(t.status()) || !YearMonth.from(t.data()).equals(periodo))
                continue;
            total = total.add(t.tipo().equals("ENTRADA") ? t.valor() : t.valor().negate());
        }
        return total;
    }
    @Transactional(readOnly = true)
    public DashboardFinanceiroResponse dashboard(int ano, int mes) {
        if (ano < 2000 || ano > 2100 || mes < 1 || mes > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ano ou mes invalido");
        }
        Map<Integer, BigDecimal> valoresAnuidade = new HashMap<>();
        for (Anuidade a : anuidades.findAll()) valoresAnuidade.put(a.getIdAnuidade(), FinanceiroRegras.
                valor(a.getValor()));
        Map<Integer, BigDecimal> valoresMensalidade = new HashMap<>();
        for (Mensalidade m : mensalidades.findAll()) valoresMensalidade.put(m.getIdMensalidade(),
                FinanceiroRegras.valor(m.getValor()));
        List<LancamentoFinanceiroResponse> itens = new ArrayList<>();
        for (EntradaFinanceira e : entradas.findAll()) {
            itens.add(new LancamentoFinanceiroResponse("ENT-" + e.getId(), e.getData(),
                    e.getDescricao(), e.getCategoria(), "ENTRADA", FinanceiroRegras.valor(e.getValor()),
                    "Recebido".equals(e.getStatus()) ? "Confirmado" : "Pendente", false,
                    e.getFkUsuario(), e.getFkFamilia()));
        }
        for (Despesas d : despesas.findAll()) {
            if (d.getDtDepesa() == null) continue;
            itens.add(new LancamentoFinanceiroResponse("DES-" + d.getIdDespesas(), d.getDtDepesa(),
                    d.getDescricao(), d.getCategoria(), "SAIDA", FinanceiroRegras.valor(d.getValor()),
                    d.getStatus(), false, d.getFkUsuario(), d.getFkFamilia()));
        }
        for (Pagamento p : pagamentos.findAll()) {
            if (p.getDtPagamento() == null) continue; // pendentes aparecem na rota /pagamentos
            BigDecimal valor = valoresAnuidade.get(p.getFkAnuidade());
            if (valor == null) throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Pagamento sem Anuidade valida: " + p.getIdPagamento());
            itens.add(new LancamentoFinanceiroResponse("PAG-" + p.getIdPagamento(), p.getDtPagamento(),
                    "Anuidade - Familia " + p.getFkFamilia(), "Anuidades", "ENTRADA", valor,
                    p.getStatus(), true, null, p.getFkFamilia()));
        }
        for (PagamentoDepartamento p : pagamentosDepartamento.findAll()) {
            if (p.getDtPagamento() == null) continue;
            BigDecimal valor = valoresMensalidade.get(p.getFkMensalidade());
            if (valor == null) throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "PagamentoDepartamento sem Mensalidade valida: " + p.getIdPagamentoDepartamento());
            itens.add(new LancamentoFinanceiroResponse("PDE-" + p.getIdPagamentoDepartamento(), p.
                    getDtPagamento(),
                    "Mensalidade - Departamento " + p.getFkDepartamento(), "Mensalidades", "ENTRADA",
                    valor, p.getStatus(), true, null, null));
        }
        itens.sort(Comparator.comparing(LancamentoFinanceiroResponse::data).reversed()
                .thenComparing(LancamentoFinanceiroResponse::id));
        YearMonth atual = YearMonth.of(ano, mes);
        BigDecimal mensal = saldo(itens, atual);
        BigDecimal anterior = saldo(itens, atual.minusMonths(1));
        BigDecimal entradas = BigDecimal.ZERO, saidas = BigDecimal.ZERO;
        for (var t : itens) {
            if (!FinanceiroRegras.pago(t.status()) || t.data().getYear() != ano) continue;
            if (t.tipo().equals("ENTRADA")) entradas = entradas.add(t.valor());
            else saidas = saidas.add(t.valor());
        }
        BigDecimal variacao = anterior.signum() == 0 ? null :
                mensal.subtract(anterior).multiply(BigDecimal.valueOf(100))
                        .divide(anterior.abs(), 2, RoundingMode.HALF_UP);
        return new DashboardFinanceiroResponse(new ResumoFinanceiroResponse(
                mensal, entradas.subtract(saidas), entradas, saidas, variacao), itens);
    }

    @Transactional(readOnly = true)
    public byte[] exportarCsv(int ano, int mes, LocalDate dataInicio, LocalDate dataFim,
                              String tipo, String status, String busca) {
        if (ano < 2000 || ano > 2100 || mes < 1 || mes > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ano ou mes invalido");
        }
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Intervalo de datas invalido");
        }
        String tipoNormalizado = normalizarFiltro(tipo);
        if (tipoNormalizado != null && !Set.of("ENTRADA", "SAIDA").contains(tipoNormalizado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de lancamento invalido");
        }
        String statusNormalizado = normalizarFiltro(status);
        if (statusNormalizado != null && !Set.of("PAGO", "PENDENTE", "RECUSADO").contains(statusNormalizado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status de lancamento invalido");
        }
        String consulta = busca == null ? "" : busca.trim().toLowerCase(Locale.forLanguageTag("pt-BR"));
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<LancamentoFinanceiroResponse> lancamentos = new ArrayList<>();
        for (Despesas item : despesas.findAll()) {
            if (item.getDtDepesa() == null) continue;
            lancamentos.add(new LancamentoFinanceiroResponse(
                    "DES-" + item.getIdDespesas(),
                    item.getDtDepesa(),
                    item.getDescricao(),
                    item.getCategoria(),
                    "SAIDA",
                    FinanceiroRegras.valor(item.getValor()),
                    item.getStatus(),
                    false,
                    item.getFkUsuario(),
                    item.getFkFamilia()));
        }
        for (EntradaFinanceira item : entradas.findAll()) {
            lancamentos.add(new LancamentoFinanceiroResponse(
                    "ENT-" + item.getId(),
                    item.getData(),
                    item.getDescricao(),
                    item.getCategoria(),
                    "ENTRADA",
                    FinanceiroRegras.valor(item.getValor()),
                    "Recebido".equals(item.getStatus()) ? "Confirmado" : item.getStatus(),
                    false,
                    item.getFkUsuario(),
                    item.getFkFamilia()));
        }

        List<List<String>> linhas = lancamentos.stream()
                .filter(item -> dataInicio == null || !item.data().isBefore(dataInicio))
                .filter(item -> dataFim == null || !item.data().isAfter(dataFim))
                .filter(item -> tipoNormalizado == null || item.tipo().equals(tipoNormalizado))
                .filter(item -> statusNormalizado == null || correspondeStatus(item.status(), statusNormalizado))
                .filter(item -> consulta.isEmpty() || (Objects.toString(item.descricao(), "") + " "
                        + Objects.toString(item.categoria(), ""))
                        .toLowerCase(Locale.forLanguageTag("pt-BR")).contains(consulta))
                .map(item -> List.of(
                        item.data().format(formatoData),
                        Objects.toString(item.descricao(), ""),
                        Objects.toString(item.categoria(), ""),
                        item.tipo().equals("ENTRADA") ? "Entrada" : "Saída",
                        item.valor().setScale(2, RoundingMode.HALF_UP)
                                .toPlainString().replace('.', ','),
                        FinanceiroRegras.pago(item.status())
                                ? (item.tipo().equals("ENTRADA") ? "Recebido" : "Pago")
                                : Objects.toString(item.status(), "").trim()))
                .toList();

        return CsvUtils.gerar(
                List.of("Data", "Descrição", "Categoria", "Tipo", "Valor", "Status"),
                linhas);
    }

    private static String normalizarFiltro(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim().toUpperCase(Locale.ROOT);
    }

    private static boolean correspondeStatus(String status, String filtro) {
        return filtro.equals("PAGO") ? FinanceiroRegras.pago(status)
                : status != null && status.trim().equalsIgnoreCase(filtro);
    }
}
