package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Despesas;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Lancamento;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.DespesasRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.LancamentoRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@Service
public class SolicitacaoReembolsoService {
    private final LancamentoRepository lancamentos;
    private final DespesasRepository despesas;
    private final UsuarioRepository usuarios;

    public SolicitacaoReembolsoService(LancamentoRepository lancamentos,
                                       DespesasRepository despesas,
                                       UsuarioRepository usuarios) {
        this.lancamentos = lancamentos;
        this.despesas = despesas;
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoReembolsoResponse> listar() {
        return lancamentos.findAllByTipoLancamentoOrderByDataLancamentoDescIdDesc(Lancamento.TIPO_REEMBOLSO).stream()
                .map(SolicitacaoReembolsoService::resposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoReembolsoResponse> listarDoUsuario(Integer usuarioId) {
        return lancamentos.findAllByTipoLancamentoAndUsuario_IdOrderByDataLancamentoDescIdDesc(
                        Lancamento.TIPO_REEMBOLSO, usuarioId).stream()
                .map(SolicitacaoReembolsoService::resposta)
                .toList();
    }

    @Transactional
    public SolicitacaoReembolsoResponse criar(SolicitacaoReembolsoCreateRequest request, Integer usuarioId) {
        Usuario usuario = usuarios.findById(usuarioId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário autenticado não encontrado"));
        var lancamento = new Lancamento();
        lancamento.setDataLancamento(LocalDate.now());
        lancamento.setDescricao(request.motivo().trim());
        lancamento.setCategoria("Reembolsos");
        lancamento.setValor(request.valor());
        lancamento.setStatus("Pendente");
        lancamento.setUsuario(usuario);
        lancamento.setFamilia(usuario.getFamilia());
        lancamento.setTipoLancamento(Lancamento.TIPO_REEMBOLSO);
        return resposta(lancamentos.save(lancamento));
    }

    @Transactional
    public SolicitacaoReembolsoResponse aprovar(Integer id, Integer revisorId) {
        Lancamento solicitacao = buscarPendente(id);
        var despesa = new Despesas();
        despesa.setDtDepesa(LocalDate.now());
        despesa.setDescricao("Reembolso #" + solicitacao.getId());
        despesa.setCategoria("Reembolsos");
        despesa.setValor(solicitacao.getValor());
        despesa.setStatus("Pendente");
        despesa.setFkUsuario(revisorId);
        despesa.setFkReembolso(solicitacao.getId());
        despesas.save(despesa);

        solicitacao.setStatus("Aprovado");
        return resposta(lancamentos.save(solicitacao));
    }

    @Transactional
    public SolicitacaoReembolsoResponse recusar(Integer id) {
        Lancamento solicitacao = buscarPendente(id);
        solicitacao.setStatus("Recusado");
        return resposta(lancamentos.save(solicitacao));
    }

    private Lancamento buscarPendente(Integer id) {
        Lancamento solicitacao = lancamentos.buscarParaAtualizacao(id, Lancamento.TIPO_REEMBOLSO).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação de reembolso não encontrada"));
        if (!"Pendente".equals(solicitacao.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta solicitação já foi analisada");
        }
        return solicitacao;
    }

    private static SolicitacaoReembolsoResponse resposta(Lancamento solicitacao) {
        Usuario usuario = solicitacao.getUsuario();
        return new SolicitacaoReembolsoResponse(
                solicitacao.getId(),
                usuario.getNome(),
                usuario.getFuncaoSistema() == null ? "" : usuario.getFuncaoSistema().getNome(),
                solicitacao.getDescricao(),
                solicitacao.getValor(),
                solicitacao.getStatus(),
                solicitacao.getDataLancamento());
    }
}
