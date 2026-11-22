package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.evento.EventoRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.evento.EventoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.evento.EventoResumoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.EspacoEvento;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Evento;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Familia;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.EventoConflitoException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.EventoDadosInvalidosException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.EventoNaoEncontradoException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.DepartamentoRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.EventoRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FamiliaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
@DisplayName("Regras de negócio da Agenda - EventoService")
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private FamiliaRepository familiaRepository;

    @Mock
    private DepartamentoRepository departamentoRepository;

    @Mock
    private PermissaoAgendaService permissaoAgendaService;

    @Mock
    private GoogleCalendarService googleCalendarService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private EventoService eventoService;

    private Usuario diretoria;

    @BeforeEach
    void prepararUsuario() {
        diretoria = new Usuario();
        diretoria.setId(7);
        diretoria.setAtivo(true);
    }

    private EventoRequest request(EspacoEvento espaco, String inicio, String fim) {
        return new EventoRequest("Ensaio cultural", "Descrição do ensaio",
                LocalDate.of(2026, 12, 12), LocalDate.of(2026, 12, 12),
                false, LocalTime.parse(inicio), LocalTime.parse(fim),
                espaco, null, null, null);
    }

    private Evento ocupado(EspacoEvento espaco, String inicio, String fim) {
        Evento evento = new Evento();
        evento.setId(5);
        evento.setTitulo("Reunião interna");
        evento.setDescricao("Informações reservadas");
        evento.setDtInicio(LocalDate.of(2026, 12, 12));
        evento.setDtFim(LocalDate.of(2026, 12, 12));
        evento.setDiaInteiro(false);
        evento.setHoraInicio(LocalTime.parse(inicio));
        evento.setHoraFim(LocalTime.parse(fim));
        evento.setCodigoEspaco(espaco);
        evento.setStatus("APROVADO");
        evento.setTipoReservante("DIRETORIA");
        return evento;
    }

    private void configurarAutorizacaoDiretoria() {
        Mockito.when(permissaoAgendaService.usuario(7)).thenReturn(diretoria);
    }

    private void configurarBloqueioTransacional() {
        Mockito.when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        Mockito.when(query.getSingleResult()).thenReturn(1);
    }

    private void configurarSalvamento() {
        Mockito.when(eventoRepository.saveAndFlush(any(Evento.class)))
                .thenAnswer(invocacao -> {
                    Evento evento = invocacao.getArgument(0);
                    if (evento.getId() == null) {
                        evento.setId(20);
                    }
                    return evento;
                });
    }

    @Nested
    @DisplayName("Cadastrar evento diretamente - Diretoria")
    class CadastrarEvento {

        @Test
        @DisplayName("Deve permitir eventos no mesmo horário em espaços diferentes")
        void devePermitirEspacosDiferentes() {
            // Arrange
            configurarAutorizacaoDiretoria();
            configurarBloqueioTransacional();
            configurarSalvamento();
            Evento eventoExistente = ocupado(EspacoEvento.SALAO, "14:00", "18:00");
            EventoRequest dados = request(EspacoEvento.COZINHA, "16:00", "17:00");
            Mockito.when(eventoRepository.findByStatusAndDtInicioLessThanEqualAndDtFimGreaterThanEqual(
                    eq("APROVADO"), any(), any())).thenReturn(List.of(eventoExistente));

            // Act
            EventoResponse resposta = eventoService.criar(dados, 7);

            // Assert
            Assertions.assertEquals("APROVADO", resposta.status());
            Assertions.assertEquals(EspacoEvento.COZINHA, resposta.codigoEspaco());
            Assertions.assertEquals("PENDENTE", resposta.statusSincronizacao());
            Mockito.verify(eventoRepository).saveAndFlush(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando houver conflito no mesmo espaço")
        void deveImpedirSobreposicaoMesmoEspaco() {
            // Arrange
            configurarAutorizacaoDiretoria();
            configurarBloqueioTransacional();
            EventoRequest dados = request(EspacoEvento.SALAO, "16:00", "19:00");
            Mockito.when(eventoRepository.findByStatusAndDtInicioLessThanEqualAndDtFimGreaterThanEqual(
                            eq("APROVADO"), any(), any()))
                    .thenReturn(List.of(ocupado(EspacoEvento.SALAO, "14:00", "18:00")));

            // Act
            EventoConflitoException excecao = Assertions.assertThrows(
                    EventoConflitoException.class, () -> eventoService.criar(dados, 7));

            // Assert
            Assertions.assertEquals(409, excecao.getStatusCode().value());
            Mockito.verify(eventoRepository, Mockito.never()).saveAndFlush(any(Evento.class));
        }

        @Test
        @DisplayName("Deve impedir reserva de Cozinha durante reserva de Espaço completo")
        void deveImpedirConflitoComEspacoCompleto() {
            // Arrange
            configurarAutorizacaoDiretoria();
            configurarBloqueioTransacional();
            EventoRequest dados = request(EspacoEvento.COZINHA, "15:00", "17:00");
            Mockito.when(eventoRepository.findByStatusAndDtInicioLessThanEqualAndDtFimGreaterThanEqual(
                            eq("APROVADO"), any(), any()))
                    .thenReturn(List.of(ocupado(EspacoEvento.ESPACO_COMPLETO, "14:00", "18:00")));

            // Act
            EventoConflitoException excecao = Assertions.assertThrows(
                    EventoConflitoException.class, () -> eventoService.criar(dados, 7));

            // Assert
            Assertions.assertEquals(409, excecao.getStatusCode().value());
            Mockito.verify(eventoRepository, Mockito.never()).saveAndFlush(any(Evento.class));
        }

        @Test
        @DisplayName("Deve permitir começar exatamente quando termina a reserva anterior")
        void devePermitirHorarioAdjacente() {
            // Arrange
            configurarAutorizacaoDiretoria();
            configurarBloqueioTransacional();
            configurarSalvamento();
            EventoRequest dados = request(EspacoEvento.SALAO, "18:00", "20:00");
            Mockito.when(eventoRepository.findByStatusAndDtInicioLessThanEqualAndDtFimGreaterThanEqual(
                            eq("APROVADO"), any(), any()))
                    .thenReturn(List.of(ocupado(EspacoEvento.SALAO, "14:00", "18:00")));

            // Act
            EventoResponse resposta = eventoService.criar(dados, 7);

            // Assert
            Assertions.assertEquals("APROVADO", resposta.status());
            Mockito.verify(eventoRepository).saveAndFlush(any(Evento.class));
        }

        @Test
        @DisplayName("Deve rejeitar horário inicial igual ao horário final")
        void deveRejeitarHorarioIgual() {
            // Arrange
            configurarAutorizacaoDiretoria();
            EventoRequest dados = request(EspacoEvento.PALCO, "18:00", "18:00");

            // Act
            EventoDadosInvalidosException excecao = Assertions.assertThrows(
                    EventoDadosInvalidosException.class, () -> eventoService.criar(dados, 7));

            // Assert
            Assertions.assertEquals(400, excecao.getStatusCode().value());
            Mockito.verify(entityManager, Mockito.never()).createNativeQuery(anyString());
        }

        @Test
        @DisplayName("Deve rejeitar data final anterior à inicial")
        void deveRejeitarDataInvertida() {
            // Arrange
            configurarAutorizacaoDiretoria();
            EventoRequest dados = new EventoRequest("Reunião", "Descrição",
                    LocalDate.of(2026, 12, 13), LocalDate.of(2026, 12, 12),
                    true, null, null, EspacoEvento.SALAO, null, null, null);

            // Act
            EventoDadosInvalidosException excecao = Assertions.assertThrows(
                    EventoDadosInvalidosException.class, () -> eventoService.criar(dados, 7));

            // Assert
            Assertions.assertEquals(400, excecao.getStatusCode().value());
            Mockito.verify(eventoRepository, Mockito.never()).saveAndFlush(any());
        }
    }

    @Nested
    @DisplayName("Solicitar reserva - Associado responsável")
    class SolicitarReserva {

        @Test
        @DisplayName("Deve vincular automaticamente a família do solicitante")
        void deveVincularFamiliaDoUsuario() {
            // Arrange
            Usuario responsavel = new Usuario();
            responsavel.setId(8);
            Familia familia = new Familia();
            familia.setId(3);
            familia.setNome("Tanaka");
            responsavel.setFamilia(familia);
            Mockito.when(permissaoAgendaService.usuario(8)).thenReturn(responsavel);
            Mockito.when(permissaoAgendaService.papel(responsavel)).thenReturn("FAMILIA");
            Mockito.when(familiaRepository.findById(3)).thenReturn(Optional.of(familia));
            configurarSalvamento();
            EventoRequest dados = request(EspacoEvento.PALCO, "10:00", "12:00");

            // Act
            EventoResponse resposta = eventoService.solicitar(dados, 8);

            // Assert
            Assertions.assertEquals("PENDENTE", resposta.status());
            Assertions.assertEquals("SOLICITACAO", resposta.origemCadastro());
            Assertions.assertEquals(3, resposta.fkFamilia());
            Assertions.assertEquals("NAO_APLICAVEL", resposta.statusSincronizacao());
            Mockito.verify(eventoRepository).saveAndFlush(any(Evento.class));
        }

        @Test
        @DisplayName("Deve rejeitar solicitante sem família vinculada")
        void deveRejeitarFamiliaAusente() {
            // Arrange
            Usuario responsavel = new Usuario();
            responsavel.setId(8);
            Mockito.when(permissaoAgendaService.usuario(8)).thenReturn(responsavel);
            Mockito.when(permissaoAgendaService.papel(responsavel)).thenReturn("FAMILIA");
            EventoRequest dados = request(EspacoEvento.PALCO, "10:00", "12:00");

            // Act
            EventoConflitoException excecao = Assertions.assertThrows(
                    EventoConflitoException.class, () -> eventoService.solicitar(dados, 8));

            // Assert
            Assertions.assertEquals(409, excecao.getStatusCode().value());
            Mockito.verify(eventoRepository, Mockito.never()).saveAndFlush(any());
        }
    }

    @Nested
    @DisplayName("Aprovar solicitação - Diretoria")
    class AprovarSolicitacao {

        @Test
        @DisplayName("Deve registrar a decisão e preparar a sincronização com o Google")
        void deveAprovarSolicitacaoPendente() {
            // Arrange
            configurarAutorizacaoDiretoria();
            configurarBloqueioTransacional();
            configurarSalvamento();
            Evento solicitacao = ocupado(EspacoEvento.PALCO, "08:00", "10:00");
            solicitacao.setStatus("PENDENTE");
            solicitacao.setOrigemCadastro("SOLICITACAO");
            Mockito.when(eventoRepository.findById(5)).thenReturn(Optional.of(solicitacao));
            Mockito.when(eventoRepository.findByStatusAndDtInicioLessThanEqualAndDtFimGreaterThanEqual(
                    eq("APROVADO"), any(), any())).thenReturn(List.of());

            // Act
            EventoResponse resposta = eventoService.aprovar(5, 7);

            // Assert
            Assertions.assertEquals("APROVADO", resposta.status());
            Assertions.assertEquals(7, solicitacao.getFkUsuarioDecisao());
            Assertions.assertEquals("CRIAR", solicitacao.getOperacaoSincronizacao());
            Assertions.assertNotNull(solicitacao.getDataDecisao());
            Mockito.verify(eventoRepository).saveAndFlush(solicitacao);
        }

        @Test
        @DisplayName("Deve impedir aprovar evento que já foi aprovado")
        void deveImpedirAprovacaoDuplicada() {
            // Arrange
            configurarAutorizacaoDiretoria();
            configurarBloqueioTransacional();
            Evento evento = ocupado(EspacoEvento.SALAO, "14:00", "18:00");
            evento.setOrigemCadastro("SOLICITACAO");
            Mockito.when(eventoRepository.findById(5)).thenReturn(Optional.of(evento));

            // Act
            EventoConflitoException excecao = Assertions.assertThrows(
                    EventoConflitoException.class, () -> eventoService.aprovar(5, 7));

            // Assert
            Assertions.assertEquals(409, excecao.getStatusCode().value());
            Mockito.verify(eventoRepository, Mockito.never()).saveAndFlush(any());
        }

        @Test
        @DisplayName("Deve impedir aprovação se o espaço ficou ocupado")
        void deveRevalidarConflitoNaAprovacao() {
            // Arrange
            configurarAutorizacaoDiretoria();
            configurarBloqueioTransacional();
            Evento solicitacao = ocupado(EspacoEvento.SALAO, "14:00", "18:00");
            solicitacao.setStatus("PENDENTE");
            solicitacao.setOrigemCadastro("SOLICITACAO");
            Evento outroEvento = ocupado(EspacoEvento.SALAO, "16:00", "19:00");
            outroEvento.setId(6);
            Mockito.when(eventoRepository.findById(5)).thenReturn(Optional.of(solicitacao));
            Mockito.when(eventoRepository.findByStatusAndDtInicioLessThanEqualAndDtFimGreaterThanEqual(
                    eq("APROVADO"), any(), any())).thenReturn(List.of(outroEvento));

            // Act
            EventoConflitoException excecao = Assertions.assertThrows(
                    EventoConflitoException.class, () -> eventoService.aprovar(5, 7));

            // Assert
            Assertions.assertEquals(409, excecao.getStatusCode().value());
            Assertions.assertEquals("PENDENTE", solicitacao.getStatus());
            Mockito.verify(eventoRepository, Mockito.never()).saveAndFlush(any());
        }
    }

    @Nested
    @DisplayName("Recusar solicitação - Diretoria")
    class RecusarSolicitacao {

        @Test
        @DisplayName("Deve recusar pedido sem criar evento no Google Calendar")
        void deveRecusarSemSincronizacao() {
            // Arrange
            configurarAutorizacaoDiretoria();
            configurarSalvamento();
            Evento solicitacao = ocupado(EspacoEvento.PALCO, "08:00", "10:00");
            solicitacao.setStatus("PENDENTE");
            solicitacao.setOrigemCadastro("SOLICITACAO");
            Mockito.when(eventoRepository.findById(5)).thenReturn(Optional.of(solicitacao));

            // Act
            EventoResponse resposta = eventoService.recusar(5, "Espaço indisponível", 7);

            // Assert
            Assertions.assertEquals("RECUSADO", resposta.status());
            Assertions.assertEquals("Espaço indisponível", resposta.motivoRecusa());
            Mockito.verifyNoInteractions(googleCalendarService);
        }
    }

    @Nested
    @DisplayName("Cancelar solicitação - autor")
    class CancelarSolicitacao {

        @Test
        @DisplayName("Deve permitir que o autor cancele seu pedido pendente")
        void deveCancelarPropriaSolicitacao() {
            // Arrange
            Usuario autor = new Usuario();
            autor.setId(8);
            Evento solicitacao = ocupado(EspacoEvento.SALAO, "08:00", "10:00");
            solicitacao.setFkUsuario(8);
            solicitacao.setStatus("PENDENTE");
            solicitacao.setOrigemCadastro("SOLICITACAO");
            Mockito.when(permissaoAgendaService.usuario(8)).thenReturn(autor);
            Mockito.when(eventoRepository.findById(5)).thenReturn(Optional.of(solicitacao));
            configurarSalvamento();

            // Act
            EventoResponse resposta = eventoService.cancelarSolicitacao(5, 8);

            // Assert
            Assertions.assertEquals("CANCELADO", resposta.status());
            Assertions.assertEquals(8, solicitacao.getFkUsuarioCancelamento());
            Assertions.assertNotNull(solicitacao.getDataCancelamento());
        }

        @Test
        @DisplayName("Deve ocultar solicitação pertencente a outro usuário")
        void deveImpedirCancelamentoPorTerceiros() {
            // Arrange
            Usuario solicitante = new Usuario();
            solicitante.setId(8);
            Evento pedidoAlheio = ocupado(EspacoEvento.SALAO, "08:00", "10:00");
            pedidoAlheio.setFkUsuario(9);
            Mockito.when(permissaoAgendaService.usuario(8)).thenReturn(solicitante);
            Mockito.when(eventoRepository.findById(5)).thenReturn(Optional.of(pedidoAlheio));

            // Act
            EventoNaoEncontradoException excecao = Assertions.assertThrows(
                    EventoNaoEncontradoException.class, () -> eventoService.cancelarSolicitacao(5, 8));

            // Assert
            Assertions.assertEquals(404, excecao.getStatusCode().value());
            Mockito.verify(eventoRepository, Mockito.never()).saveAndFlush(any());
        }
    }

    @Nested
    @DisplayName("Consultar calendário e informações privadas")
    class ConsultarCalendario {

        @Test
        @DisplayName("Deve exibir somente reservas aprovadas no calendário")
        void deveMostrarSomenteEventosAprovados() {
            // Arrange
            Usuario dependente = new Usuario();
            dependente.setId(10);
            Evento aprovado = ocupado(EspacoEvento.PALCO, "14:00", "18:00");
            Evento pendente = ocupado(EspacoEvento.SALAO, "10:00", "12:00");
            pendente.setStatus("PENDENTE");
            Mockito.when(permissaoAgendaService.usuario(10)).thenReturn(dependente);
            Mockito.when(eventoRepository.buscarPorPeriodo(any(), any()))
                    .thenReturn(List.of(aprovado, pendente));
            LocalDate inicio = LocalDate.of(2026, 12, 1);
            LocalDate fim = LocalDate.of(2026, 12, 31);

            // Act
            List<EventoResumoResponse> resposta = eventoService.calendario(10, inicio, fim);

            // Assert
            Assertions.assertEquals(1, resposta.size());
            Assertions.assertEquals("Diretoria", resposta.getFirst().reservadoPara());
            Assertions.assertFalse(resposta.getFirst().toString().contains("Reunião interna"));
            Assertions.assertFalse(resposta.getFirst().toString().contains("PALCO"));
        }

        @Test
        @DisplayName("Deve impedir que solicitante consulte os detalhes de pedido de terceiro")
        void deveRestringirDetalheAoAutor() {
            // Arrange
            Usuario solicitante = new Usuario();
            solicitante.setId(8);
            Evento pedidoAlheio = ocupado(EspacoEvento.SALAO, "08:00", "10:00");
            pedidoAlheio.setOrigemCadastro("SOLICITACAO");
            pedidoAlheio.setFkUsuario(9);
            Mockito.when(permissaoAgendaService.usuario(8)).thenReturn(solicitante);
            Mockito.when(eventoRepository.findById(5)).thenReturn(Optional.of(pedidoAlheio));

            // Act
            EventoNaoEncontradoException excecao = Assertions.assertThrows(
                    EventoNaoEncontradoException.class, () -> eventoService.minhaSolicitacao(5, 8));

            // Assert
            Assertions.assertEquals(404, excecao.getStatusCode().value());
        }

        @Test
        @DisplayName("Deve informar que evento inexistente não foi encontrado")
        void deveLancarExcecaoEventoNaoEncontrado() {
            // Arrange
            configurarAutorizacaoDiretoria();
            Mockito.when(eventoRepository.findById(999)).thenReturn(Optional.empty());

            // Act
            EventoNaoEncontradoException excecao = Assertions.assertThrows(
                    EventoNaoEncontradoException.class, () -> eventoService.buscar(999, 7));

            // Assert
            Assertions.assertEquals(404, excecao.getStatusCode().value());
        }
    }

    @Nested
    @DisplayName("Calcular sobreposição de períodos")
    class CalcularSobreposicao {

        @Test
        @DisplayName("Deve identificar conflito entre dia inteiro e evento por horário")
        void deveDetectarConflitoDeDiaInteiro() {
            // Arrange
            Evento diaInteiro = ocupado(EspacoEvento.SALAO, "09:00", "10:00");
            diaInteiro.setDiaInteiro(true);
            diaInteiro.setHoraInicio(null);
            diaInteiro.setHoraFim(null);
            Evento parcial = ocupado(EspacoEvento.SALAO, "14:00", "15:00");

            // Act
            boolean sobrepoe = EventoService.sobrepoe(diaInteiro, parcial);

            // Assert
            Assertions.assertTrue(sobrepoe);
        }

        @Test
        @DisplayName("Deve considerar horários adjacentes sem sobreposição")
        void deveConsiderarHorariosAdjacentes() {
            // Arrange
            Evento primeiro = ocupado(EspacoEvento.SALAO, "14:00", "18:00");
            Evento segundo = ocupado(EspacoEvento.SALAO, "18:00", "20:00");

            // Act
            boolean sobrepoe = EventoService.sobrepoe(primeiro, segundo);

            // Assert
            Assertions.assertFalse(sobrepoe);
        }
    }
}