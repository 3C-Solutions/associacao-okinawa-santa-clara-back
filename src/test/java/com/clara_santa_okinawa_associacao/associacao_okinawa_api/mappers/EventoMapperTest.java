package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.evento.EventoRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.evento.EventoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.evento.EventoResumoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.EspacoEvento;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Evento;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

@DisplayName("Conversões do EventoMapper")
class EventoMapperTest {

    private Evento evento() {
        Evento evento = new Evento();
        evento.setId(3);
        evento.setTitulo("Reunião privada");
        evento.setDescricao("Dados da Diretoria");
        evento.setDtInicio(LocalDate.of(2026, 12, 12));
        evento.setDtFim(LocalDate.of(2026, 12, 12));
        evento.setDiaInteiro(false);
        evento.setHoraInicio(LocalTime.of(14, 0));
        evento.setHoraFim(LocalTime.of(18, 0));
        evento.setCodigoEspaco(EspacoEvento.SALAO);
        evento.setStatus("APROVADO");
        return evento;
    }

    @Nested
    @DisplayName("Converter Request para Entity")
    class ConverterRequest {

        @Test
        @DisplayName("Deve preencher título e descrição removendo espaços excedentes")
        void devePreencherCampos() {
            // Arrange
            Evento destino = new Evento();
            EventoRequest request = new EventoRequest("  Ensaio  ", "  Ensaio de dança  ",
                    LocalDate.of(2026, 12, 12), LocalDate.of(2026, 12, 12),
                    false, LocalTime.of(10, 0), LocalTime.of(12, 0),
                    EspacoEvento.PALCO, null, null, null);

            // Act
            EventoMapper.preencher(destino, request);

            // Assert
            Assertions.assertEquals("Ensaio", destino.getTitulo());
            Assertions.assertEquals("Ensaio de dança", destino.getDescricao());
            Assertions.assertEquals(EspacoEvento.PALCO, destino.getCodigoEspaco());
            Assertions.assertEquals("Palco", destino.getLocal());
        }

        @Test
        @DisplayName("Deve remover horários quando o evento for de dia inteiro")
        void deveLimparHorariosDiaInteiro() {
            // Arrange
            Evento destino = evento();
            EventoRequest request = new EventoRequest("Evento", null,
                    LocalDate.of(2026, 12, 12), LocalDate.of(2026, 12, 13),
                    true, null, null, EspacoEvento.ESPACO_COMPLETO, null, null, null);

            // Act
            EventoMapper.preencher(destino, request);

            // Assert
            Assertions.assertNull(destino.getHoraInicio());
            Assertions.assertNull(destino.getHoraFim());
            Assertions.assertTrue(destino.getDiaInteiro());
        }
    }

    @Nested
    @DisplayName("Converter Entity para Response")
    class ConverterResponse {

        @Test
        @DisplayName("Deve ocultar título e descrição no resumo público")
        void deveOcultarDadosPrivados() {
            // Arrange
            Evento origem = evento();

            // Act
            EventoResumoResponse resposta = EventoMapper.toResumo(origem, "Diretoria");

            // Assert
            Assertions.assertEquals(3, resposta.id());
            Assertions.assertEquals("Diretoria", resposta.reservadoPara());
            Assertions.assertFalse(resposta.toString().contains("Reunião privada"));
            Assertions.assertFalse(resposta.toString().contains("SALAO"));
        }

        @Test
        @DisplayName("Deve preservar os detalhes na resposta administrativa")
        void deveExibirDadosParaDiretoria() {
            // Arrange
            Evento origem = evento();

            // Act
            EventoResponse resposta = EventoMapper.toResponse(origem, "Diretoria");

            // Assert
            Assertions.assertEquals("Reunião privada", resposta.titulo());
            Assertions.assertEquals("Dados da Diretoria", resposta.descricao());
            Assertions.assertEquals(EspacoEvento.SALAO, resposta.codigoEspaco());
        }
    }
}