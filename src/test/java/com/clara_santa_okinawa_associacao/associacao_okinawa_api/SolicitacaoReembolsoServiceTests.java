package com.clara_santa_okinawa_associacao.associacao_okinawa_api;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.ComprovanteReembolsoRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.SolicitacaoReembolsoCreateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Despesas;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.SolicitacaoReembolso;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.DespesasRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.SolicitacaoReembolsoRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.SolicitacaoReembolsoService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolicitacaoReembolsoServiceTests {
    private final SolicitacaoReembolsoRepository solicitacoes = mock(SolicitacaoReembolsoRepository.class);
    private final DespesasRepository despesas = mock(DespesasRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final SolicitacaoReembolsoService service =
            new SolicitacaoReembolsoService(solicitacoes, despesas, usuarios);

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(7);
        usuario.setNome("Ana");
        return usuario;
    }

    private SolicitacaoReembolso solicitacao(String status) {
        SolicitacaoReembolso solicitacao = new SolicitacaoReembolso();
        solicitacao.setId(12);
        solicitacao.setSolicitante(usuario());
        solicitacao.setMotivo("Compra de materiais");
        solicitacao.setValor(new BigDecimal("45.50"));
        solicitacao.setComprovanteNome("nota.png");
        solicitacao.setComprovanteTipo("image/png");
        solicitacao.setComprovanteDados("data:image/png;base64,aGVsbG8=");
        solicitacao.setStatus(status);
        solicitacao.setDataSolicitacao(LocalDateTime.now());
        return solicitacao;
    }

    @Test
    void criaSolicitacaoComUsuarioAutenticadoEComprovanteValidado() {
        when(usuarios.findById(7)).thenReturn(Optional.of(usuario()));
        when(solicitacoes.save(any())).thenAnswer(chamada -> chamada.getArgument(0));
        var pedido = new SolicitacaoReembolsoCreateRequest(
                " Compra de materiais ", new BigDecimal("45.50"),
                new ComprovanteReembolsoRequest(" nota.png ", "image/png", "data:image/png;base64,aGVsbG8="));

        var resposta = service.criar(pedido, 7);

        assertEquals("Pendente", resposta.status());
        assertEquals("Ana", resposta.solicitante());
        assertEquals("Compra de materiais", resposta.motivo());
        assertEquals("nota.png", resposta.comprovante().nome());
        verify(solicitacoes).save(any(SolicitacaoReembolso.class));
    }

    @Test
    void aprovacaoCriaDespesaPendenteVinculadaUmaUnicaVez() {
        when(solicitacoes.buscarParaAtualizacao(12)).thenReturn(Optional.of(solicitacao("Pendente")));
        when(solicitacoes.save(any())).thenAnswer(chamada -> chamada.getArgument(0));
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
        when(solicitacoes.buscarParaAtualizacao(12)).thenReturn(Optional.of(solicitacao("Aprovado")));

        var falha = assertThrows(ResponseStatusException.class, () -> service.aprovar(12, 3));

        assertEquals(HttpStatus.CONFLICT, falha.getStatusCode());
        verifyNoInteractions(despesas);
    }

    @Test
    void recusaExigePedidoPendenteERegistraMotivo() {
        when(solicitacoes.buscarParaAtualizacao(12)).thenReturn(Optional.of(solicitacao("Pendente")));
        when(solicitacoes.save(any())).thenAnswer(chamada -> chamada.getArgument(0));

        var resposta = service.recusar(12, 3, "  Sem comprovante válido  ");

        assertEquals("Recusado", resposta.status());
        assertEquals("Sem comprovante válido", resposta.motivoRecusa());
        verifyNoInteractions(despesas);
    }

    @Test
    void rejeitaComprovanteInválidoAntesDePersistir() {
        var pedido = new SolicitacaoReembolsoCreateRequest(
                "Compra de materiais", BigDecimal.ONE,
                new ComprovanteReembolsoRequest("nota.png", "image/png", "data:image/jpeg;base64,aGVsbG8="));

        var falha = assertThrows(ResponseStatusException.class, () -> service.criar(pedido, 7));

        assertEquals(HttpStatus.BAD_REQUEST, falha.getStatusCode());
        verifyNoInteractions(usuarios, solicitacoes);
    }
}
