package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@DisplayName("Espaços disponíveis para aluguel")
class EspacoEventoTest {

    @Nested
    @DisplayName("Selecionar espaços")
    class SelecionarEspacos {

        @Test
        @DisplayName("Deve disponibilizar os cinco espaços cadastrados")
        void devePossuirCincoEspacos() {
            // Arrange
            String[] esperados = {
                    "COZINHA", "ESPACO_EXTERNO", "PALCO", "SALAO", "ESPACO_COMPLETO"
            };

            // Act
            String[] atuais = Arrays.stream(EspacoEvento.values())
                    .map(Enum::name)
                    .toArray(String[]::new);

            // Assert
            Assertions.assertArrayEquals(esperados, atuais);
        }
    }

    @Nested
    @DisplayName("Validar conflitos entre espaços")
    class ValidarConflitos {

        @Test
        @DisplayName("Deve bloquear qualquer espaço individual quando Espaço completo estiver reservado")
        void deveBloquearTodosComEspacoCompleto() {
            // Arrange
            EspacoEvento reserva = EspacoEvento.ESPACO_COMPLETO;
            EspacoEvento[] opcoes = EspacoEvento.values();

            // Act
            boolean todosConflitam = Arrays.stream(opcoes)
                    .allMatch(opcao -> reserva.conflitaCom(opcao)
                            && opcao.conflitaCom(reserva));

            // Assert
            Assertions.assertTrue(todosConflitam);
        }

        @Test
        @DisplayName("Deve permitir Cozinha e Salão simultaneamente")
        void devePermitirEspacosIndividuaisDiferentes() {
            // Arrange
            EspacoEvento primeiro = EspacoEvento.COZINHA;
            EspacoEvento segundo = EspacoEvento.SALAO;

            // Act
            boolean conflito = primeiro.conflitaCom(segundo);

            // Assert
            Assertions.assertFalse(conflito);
        }

        @Test
        @DisplayName("Deve bloquear duas reservas do mesmo espaço")
        void deveBloquearMesmoEspaco() {
            // Arrange
            EspacoEvento primeiro = EspacoEvento.PALCO;
            EspacoEvento segundo = EspacoEvento.PALCO;

            // Act
            boolean conflito = primeiro.conflitaCom(segundo);

            // Assert
            Assertions.assertTrue(conflito);
        }
    }
}