package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Despesas;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.SolicitacaoReembolso;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.DespesasRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.SolicitacaoReembolsoRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SolicitacaoReembolsoService {
    private static final int TAMANHO_MAXIMO_COMPROVANTE = 1024 * 1024;
    private static final Pattern DATA_URL = Pattern.compile(
            "^data:(image/png|image/jpeg|image/webp|application/pdf);base64,([A-Za-z0-9+/]*={0,2})$");

    private final SolicitacaoReembolsoRepository solicitacoes;
    private final DespesasRepository despesas;
    private final UsuarioRepository usuarios;

    public SolicitacaoReembolsoService(SolicitacaoReembolsoRepository solicitacoes,
                                       DespesasRepository despesas,
                                       UsuarioRepository usuarios) {
        this.solicitacoes = solicitacoes;
        this.despesas = despesas;
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoReembolsoResponse> listar() {
        return solicitacoes.findAllByOrderByDataSolicitacaoDesc().stream()
                .map(SolicitacaoReembolsoService::resposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoReembolsoResponse> listarDoUsuario(Integer usuarioId) {
        return solicitacoes.findAllBySolicitante_IdOrderByDataSolicitacaoDesc(usuarioId).stream()
                .map(SolicitacaoReembolsoService::resposta)
                .toList();
    }

    @Transactional
    public SolicitacaoReembolsoResponse criar(SolicitacaoReembolsoCreateRequest request, Integer usuarioId) {
        validarComprovante(request.comprovante());
        Usuario usuario = usuarios.findById(usuarioId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário autenticado não encontrado"));
        var comprovante = request.comprovante();
        var solicitacao = new SolicitacaoReembolso();
        solicitacao.setSolicitante(usuario);
        solicitacao.setMotivo(request.motivo().trim());
        solicitacao.setValor(request.valor());
        solicitacao.setComprovanteNome(comprovante.nome().trim());
        solicitacao.setComprovanteTipo(comprovante.tipo());
        solicitacao.setComprovanteDados(comprovante.dados());
        solicitacao.setStatus("Pendente");
        solicitacao.setDataSolicitacao(LocalDateTime.now());
        return resposta(solicitacoes.save(solicitacao));
    }

    @Transactional
    public SolicitacaoReembolsoResponse aprovar(Integer id, Integer revisorId) {
        SolicitacaoReembolso solicitacao = buscarPendente(id);
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
        solicitacao.setDataRevisao(LocalDateTime.now());
        solicitacao.setFkRevisor(revisorId);
        return resposta(solicitacoes.save(solicitacao));
    }

    @Transactional
    public SolicitacaoReembolsoResponse recusar(Integer id, Integer revisorId, String motivo) {
        SolicitacaoReembolso solicitacao = buscarPendente(id);
        solicitacao.setStatus("Recusado");
        solicitacao.setMotivoRecusa(motivo.trim());
        solicitacao.setDataRevisao(LocalDateTime.now());
        solicitacao.setFkRevisor(revisorId);
        return resposta(solicitacoes.save(solicitacao));
    }

    private SolicitacaoReembolso buscarPendente(Integer id) {
        SolicitacaoReembolso solicitacao = solicitacoes.buscarParaAtualizacao(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação de reembolso não encontrada"));
        if (!"Pendente".equals(solicitacao.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta solicitação já foi analisada");
        }
        return solicitacao;
    }

    private static void validarComprovante(ComprovanteReembolsoRequest comprovante) {
        Matcher matcher = DATA_URL.matcher(comprovante.dados());
        if (!matcher.matches() || !matcher.group(1).equals(comprovante.tipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comprovante inválido");
        }
        try {
            int tamanho = Base64.getDecoder().decode(matcher.group(2)).length;
            if (tamanho == 0 || tamanho > TAMANHO_MAXIMO_COMPROVANTE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O comprovante deve ter até 1 MB");
            }
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comprovante inválido");
        }
    }

    private static SolicitacaoReembolsoResponse resposta(SolicitacaoReembolso solicitacao) {
        Usuario usuario = solicitacao.getSolicitante();
        return new SolicitacaoReembolsoResponse(
                solicitacao.getId(),
                usuario.getNome(),
                usuario.getFuncaoSistema() == null ? "" : usuario.getFuncaoSistema().getNome(),
                solicitacao.getMotivo(),
                solicitacao.getValor(),
                solicitacao.getStatus(),
                solicitacao.getDataSolicitacao(),
                solicitacao.getDataRevisao(),
                solicitacao.getMotivoRecusa(),
                new ComprovanteReembolsoResponse(
                        solicitacao.getComprovanteNome(),
                        solicitacao.getComprovanteTipo(),
                        solicitacao.getComprovanteDados())
        );
    }
}
