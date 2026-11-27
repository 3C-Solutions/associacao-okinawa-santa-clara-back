package com.clara_santa_okinawa_associacao.associacao_okinawa_api;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.SolicitacaoReembolsoCreateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Despesas;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Lancamento;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.DespesasRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.LancamentoRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.SolicitacaoReembolsoService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolicitacaoReembolsoServiceTests {
    private final LancamentoRepository lancamentos = mock(LancamentoRepository.class);
    private final DespesasRepository despesas = mock(DespesasRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final SolicitacaoReembolsoService service =
            new SolicitacaoReembolsoService(lancamentos, despesas, usuarios);

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(7);
        usuario.setNome("Ana");
        return usuario;
    }

    private Lancamento reembolso(String status) {
        var lancamento = new Lancamento();
        lancamento.setId(12);
        lancamento.setUsuario(usuario());
        lancamento.setDescricao("Compra de materiais");
        lancamento.setCategoria("Reembolsos");
        lancamento.setValor(new BigDecimal("45.50"));
        lancamento.setStatus(status);
        lancamento.setTipoLancamento(Lancamento.TIPO_REEMBOLSO);
        lancamento.setDataLancamento(LocalDate.of(2026, 11, 16));
        return lancamento;
    }

    @Test
    void criaSolicitacaoComoLancamentoUsandoUsuarioAutenticado() {
        when(usuarios.findById(7)).thenReturn(Optional.of(usuario()));
        when(lancamentos.save(any())).thenAnswer(chamada -> {
            Lancamento salvo = chamada.getArgument(0);
            salvo.setId(12);
            return salvo;
        });
        var pedido = new SolicitacaoReembolsoCreateRequest(
                " Compra de materiais ", new BigDecimal("45.50"));

        var resposta = service.criar(pedido, 7);

        assertEquals(12, resposta.id());
        assertEquals("Pendente", resposta.status());
        assertEquals("Ana", resposta.solicitante());
        assertEquals("Compra de materiais", resposta.motivo());
        var captor = org.mockito.ArgumentCaptor.forClass(Lancamento.class);
        verify(lancamentos).save(captor.capture());
        assertEquals(Lancamento.TIPO_REEMBOLSO, captor.getValue().getTipoLancamento());
        assertEquals(7, captor.getValue().getUsuario().getId());
        assertEquals("Reembolsos", captor.getValue().getCategoria());
        verifyNoInteractions(despesas);
    }

    @Test
    void aprovacaoCriaDespesaPendenteVinculadaAoLancamento() {
        when(lancamentos.buscarParaAtualizacao(12, Lancamento.TIPO_REEMBOLSO))
                .thenReturn(Optional.of(reembolso("Pendente")));
        when(lancamentos.save(any())).thenAnswer(chamada -> chamada.getArgument(0));
        when(despesas.save(any())).thenAnswer(chamada -> chamada.getArgument(0));

        var resposta = service.aprovar(12, 3);

        assertEquals("Aprovado", resposta.status());
        var captor = org.mockito.ArgumentCaptor.forClass(Despesas.class);
        verify(despesas).save(captor.capture());
        assertEquals("Pendente", captor.getValue().getStatus());
        assertEquals("Reembolsos", captor.getValue().getCategoria());
        assertEquals(new BigDecimal("45.50"), captor.getValue().getValor());
        assertEquals(12, captor.getValue().getFkReembolso());
        assertEquals(3, captor.getValue().getFkUsuario());
    }

    @Test
    void naoPermiteRevisarDuasVezes() {
        when(lancamentos.buscarParaAtualizacao(12, Lancamento.TIPO_REEMBOLSO))
                .thenReturn(Optional.of(reembolso("Aprovado")));

        var falha = assertThrows(ResponseStatusException.class, () -> service.aprovar(12, 3));

        assertEquals(HttpStatus.CONFLICT, falha.getStatusCode());
        verifyNoInteractions(despesas);
    }

    @Test
    void recusaSolicitacaoPendenteSemCapturarMotivoIndisponivelNoEsquema() {
        when(lancamentos.buscarParaAtualizacao(12, Lancamento.TIPO_REEMBOLSO))
                .thenReturn(Optional.of(reembolso("Pendente")));
        when(lancamentos.save(any())).thenAnswer(chamada -> chamada.getArgument(0));

        var resposta = service.recusar(12);

        assertEquals("Recusado", resposta.status());
        verify(lancamentos).save(any(Lancamento.class));
        verifyNoInteractions(despesas);
    }

    @Test
    void naoCriaLancamentoSemUsuarioAutenticado() {
        when(usuarios.findById(7)).thenReturn(Optional.empty());
        var pedido = new SolicitacaoReembolsoCreateRequest("Compra de materiais", BigDecimal.ONE);

        var falha = assertThrows(ResponseStatusException.class, () -> service.criar(pedido, 7));

        assertEquals(HttpStatus.UNAUTHORIZED, falha.getStatusCode());
        verifyNoInteractions(lancamentos, despesas);
    }
}
