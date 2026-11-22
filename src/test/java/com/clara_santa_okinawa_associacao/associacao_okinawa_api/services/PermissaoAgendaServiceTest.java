package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncaoSistema;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.AgendaAcessoNegadoException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.UsuarioAgendaNaoIdentificadoException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Permissões dos perfis na Agenda")
class PermissaoAgendaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PermissaoAgendaService permissaoAgendaService;

    private Usuario usuario(String funcao) {
        Usuario usuario = new Usuario();
        usuario.setId(8);
        usuario.setAtivo(true);
        FuncaoSistema perfil = new FuncaoSistema();
        perfil.setNome(funcao);
        usuario.setFuncaoSistema(perfil);
        return usuario;
    }

    @Nested
    @DisplayName("Consultar usuário")
    class ConsultarUsuario {

        @Test
        @DisplayName("Deve lançar exceção quando usuário não existe")
        void deveRejeitarUsuarioNaoEncontrado() {
            // Arrange
            Integer id = 99;
            Mockito.when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

            // Act
            UsuarioAgendaNaoIdentificadoException excecao = Assertions.assertThrows(
                    UsuarioAgendaNaoIdentificadoException.class,
                    () -> permissaoAgendaService.usuario(id));

            // Assert
            Assertions.assertEquals(401, excecao.getStatusCode().value());
        }

        @Test
        @DisplayName("Deve impedir acesso de usuário inativo")
        void deveRejeitarUsuarioInativo() {
            // Arrange
            Usuario inativo = usuario("Diretoria");
            inativo.setAtivo(false);
            Mockito.when(usuarioRepository.findById(8)).thenReturn(Optional.of(inativo));

            // Act
            AgendaAcessoNegadoException excecao = Assertions.assertThrows(
                    AgendaAcessoNegadoException.class,
                    () -> permissaoAgendaService.usuario(8));

            // Assert
            Assertions.assertEquals(403, excecao.getStatusCode().value());
        }
    }

    @Nested
    @DisplayName("Administrar eventos")
    class AdministrarEventos {

        @Test
        @DisplayName("Deve permitir que a Diretoria administre eventos")
        void devePermitirDiretoria() {
            // Arrange
            Usuario diretoria = usuario("Diretoria");

            // Act
            String papel = permissaoAgendaService.papel(diretoria);

            // Assert
            Assertions.assertEquals("DIRETORIA", papel);
            Assertions.assertDoesNotThrow(() -> permissaoAgendaService.exigirDiretoria(diretoria));
        }

        @Test
        @DisplayName("Deve impedir Departamento de administrar eventos")
        void deveBloquearDepartamento() {
            // Arrange
            Usuario departamento = usuario("Departamento");

            // Act
            AgendaAcessoNegadoException excecao = Assertions.assertThrows(
                    AgendaAcessoNegadoException.class,
                    () -> permissaoAgendaService.exigirDiretoria(departamento));

            // Assert
            Assertions.assertEquals(403, excecao.getStatusCode().value());
        }
    }

    @Nested
    @DisplayName("Solicitar reservas")
    class SolicitarReserva {

        @Test
        @DisplayName("Deve permitir que o Departamento solicite reservas")
        void devePermitirSolicitacaoDepartamento() {
            // Arrange
            Usuario departamento = usuario("Departamento");

            // Act
            String papel = permissaoAgendaService.papel(departamento);

            // Assert
            Assertions.assertEquals("DEPARTAMENTO", papel);
            Assertions.assertDoesNotThrow(() -> permissaoAgendaService.exigirSolicitante(departamento));
        }

        @Test
        @DisplayName("Deve permitir que o Associado responsável solicite reservas")
        void devePermitirSolicitacaoAssociadoResponsavel() {
            // Arrange
            Usuario responsavel = usuario("Associado responsável");

            // Act
            String papel = permissaoAgendaService.papel(responsavel);

            // Assert
            Assertions.assertEquals("FAMILIA", papel);
            Assertions.assertDoesNotThrow(() -> permissaoAgendaService.exigirSolicitante(responsavel));
        }

        @Test
        @DisplayName("Deve impedir Dependente de solicitar reservas")
        void deveBloquearDependente() {
            // Arrange
            Usuario dependente = usuario("Associado dependente");

            // Act
            AgendaAcessoNegadoException excecao = Assertions.assertThrows(
                    AgendaAcessoNegadoException.class,
                    () -> permissaoAgendaService.exigirSolicitante(dependente));

            // Assert
            Assertions.assertEquals(403, excecao.getStatusCode().value());
        }

        @Test
        @DisplayName("Deve impedir função desconhecida")
        void deveBloquearFuncaoNaoMapeada() {
            // Arrange
            Usuario outro = usuario("Outro");

            // Act
            AgendaAcessoNegadoException excecao = Assertions.assertThrows(
                    AgendaAcessoNegadoException.class,
                    () -> permissaoAgendaService.papel(outro));

            // Assert
            Assertions.assertEquals(403, excecao.getStatusCode().value());
        }
    }
}