package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.PagamentosMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Objects;
@Service
public class PagamentosService {
    private final PagamentoRepository pagamentos;
    private final PagamentoDepartamentoRepository departamentos;
    private final AnuidadeRepository anuidades;
    private final MensalidadeRepository mensalidades;
    public PagamentosService(PagamentoRepository pagamentos, PagamentoDepartamentoRepository
                                     departamentos,
                             AnuidadeRepository anuidades, MensalidadeRepository mensalidades) {
        this.pagamentos = pagamentos; this.departamentos = departamentos;
        this.anuidades = anuidades; this.mensalidades = mensalidades;
    }
    private Pagamento p(Integer id) { return pagamentos.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Pagamento nao encontrado")); }
    private PagamentoDepartamento pd(Integer id) { return departamentos.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "PagamentoDepartamento nao encontrado")); }
    @Transactional(readOnly = true)
    public List<PagamentoResponse> listarPagamentos() { return pagamentos.findAll().stream().
            map(PagamentosMapper::toResponse).toList(); }
    @Transactional(readOnly = true)
    public List<PagamentoDepartamentoResponse> listarDepartamentos() {
        return departamentos.findAll().stream().map(PagamentosMapper::toResponse).toList();
    }
    @Transactional
    public PagamentoResponse solicitar(PagamentoCreateRequest req) {
        if (!anuidades.existsById(req.fkAnuidade())) throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Anuidade nao encontrada");
        boolean repetido = pagamentos.findAll().stream().anyMatch(x ->
                Objects.equals(x.getFkFamilia(), req.fkFamilia()) && Objects.equals(x.getFkAnuidade(),
                        req.fkAnuidade())
                        && !"Recusado".equalsIgnoreCase(x.getStatus()));
        if (repetido) throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Ja existe pagamento da familia para essa anuidade");
        Pagamento p = new Pagamento();
        p.setFkFamilia(req.fkFamilia()); p.setFkAnuidade(req.fkAnuidade());
        p.setFormaPagamento(req.formaPagamento().trim()); p.setStatus("Pendente");
        return PagamentosMapper.toResponse(pagamentos.save(p));
    }
    @Transactional
    public PagamentoDepartamentoResponse solicitarDepartamento(PagamentoDepartamentoCreateRequest req) {
        if (!mensalidades.existsById(req.fkMensalidade())) throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Mensalidade nao encontrada");
        boolean repetido = departamentos.findAll().stream().anyMatch(x ->
                Objects.equals(x.getFkDepartamento(), req.fkDepartamento()) &&
                        Objects.equals(x.getFkMensalidade(), req.fkMensalidade()) &&
                        !"Recusado".equalsIgnoreCase(x.getStatus()));
        if (repetido) throw new ResponseStatusException(HttpStatus.CONFLICT,"Ja existe pagamento do departamento para essa mensalidade");
        PagamentoDepartamento p = new PagamentoDepartamento();
        p.setFkDepartamento(req.fkDepartamento()); p.setFkMensalidade(req.fkMensalidade());
        p.setFormaPagamento(req.formaPagamento().trim()); p.setStatus("Pendente");
        return PagamentosMapper.toResponse(departamentos.save(p));
    }
    @Transactional
    public PagamentoResponse confirmar(Integer id, PagamentoConfirmarRequest req) {
        Pagamento p = p(id);
        if (!"Pendente".equalsIgnoreCase(p.getStatus())) throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Somente pagamento pendente pode ser confirmado");
        p.setDtPagamento(req.dtPagamento()); p.setStatus("Pago");
        return PagamentosMapper.toResponse(pagamentos.save(p));
    }
    @Transactional
    public PagamentoDepartamentoResponse confirmarDepartamento(Integer id, PagamentoConfirmarRequest
            req) {
        PagamentoDepartamento p = pd(id);
        if (!"Pendente".equalsIgnoreCase(p.getStatus())) throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Somente pagamento pendente pode ser confirmado");
        p.setDtPagamento(req.dtPagamento()); p.setStatus("Pago");
        return PagamentosMapper.toResponse(departamentos.save(p));
    }
    @Transactional
    public PagamentoResponse recusar(Integer id) {
        Pagamento p = p(id);
        if (!"Pendente".equalsIgnoreCase(p.getStatus())) throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Pagamento nao esta pendente");
        p.setStatus("Recusado"); return PagamentosMapper.toResponse(pagamentos.save(p));
    }
    @Transactional
    public PagamentoDepartamentoResponse recusarDepartamento(Integer id) {
        PagamentoDepartamento p = pd(id);
        if (!"Pendente".equalsIgnoreCase(p.getStatus())) throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Pagamento nao esta pendente");
        p.setStatus("Recusado"); return PagamentosMapper.toResponse(departamentos.save(p));
    }
    @Transactional(readOnly = true)
    public AnuidadeSituacaoResponse anuidade(Integer fkFamilia, Integer ano) {
        var cobrancas = anuidades.findAll().stream().filter(a -> ano.equals(a.getAno())).toList();
        if (cobrancas.size() != 1) throw new ResponseStatusException(HttpStatus.CONFLICT,
                "O ano precisa ter uma unica Anuidade cadastrada");
        Anuidade a = cobrancas.get(0);
        String status = pagamentos.findAll().stream()
                .filter(p -> fkFamilia.equals(p.getFkFamilia()) && a.getIdAnuidade().equals(p.
                        getFkAnuidade()))
                .map(Pagamento::getStatus)
                .filter(s -> s != null && !s.equalsIgnoreCase("Recusado"))
                .findFirst().orElse("Em aberto");
        return new AnuidadeSituacaoResponse(a.getIdAnuidade(), a.getAno(), a.getValor(),
                a.getDtVencimento(), status);
    }
    @Transactional(readOnly = true)
    public MensalidadeSituacaoResponse mensalidade(Integer fkDepartamento, Integer ano, Integer mes) {
        if (mes < 1 || mes > 12) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mes invalido");
        var cobrancas = mensalidades.findAll().stream()
                .filter(m -> m.getDtVencimento() != null && m.getDtVencimento().getYear() == ano &&
                        m.getDtVencimento().getMonthValue() == mes).toList();
        if (cobrancas.size() != 1) throw new ResponseStatusException(HttpStatus.CONFLICT,
                "O mes precisa ter uma unica Mensalidade cadastrada");
        Mensalidade m = cobrancas.get(0);
        String status = departamentos.findAll().stream()
                .filter(p -> fkDepartamento.equals(p.getFkDepartamento()) &&
                        m.getIdMensalidade().equals(p.getFkMensalidade()))
                .map(PagamentoDepartamento::getStatus)
                .filter(s -> s != null && !s.equalsIgnoreCase("Recusado"))
                .findFirst().orElse("Em aberto");
        return new MensalidadeSituacaoResponse(m.getIdMensalidade(), m.getValor(),
                m.getDtVencimento(), fkDepartamento, status);
    } }