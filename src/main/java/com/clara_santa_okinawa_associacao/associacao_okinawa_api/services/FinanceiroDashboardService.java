package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
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
}
