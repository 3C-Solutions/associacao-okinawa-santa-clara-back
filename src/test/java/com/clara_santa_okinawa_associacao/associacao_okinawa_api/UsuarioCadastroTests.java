package com.clara_santa_okinawa_associacao.associacao_okinawa_api;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario.UsuarioCreateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Familia;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncaoSistema;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.AlreadyExistsByEmailException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.NotExistsByIdException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FamiliaRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FuncaoSistemaRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.EmailService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioCadastroTests {
    @Mock private UsuarioRepository usuarios;
    @Mock private FamiliaRepository familias;
    @Mock private FuncaoSistemaRepository funcoes;
    @Mock private PasswordEncoder encoder;
    @Mock private EmailService emails;

    @Test
    void cadastraUsuarioAtivoComVinculosESenhaProvisoriaCodificada() {
        var familia = new Familia();
        familia.setId(2);
        var funcao = new FuncaoSistema();
        funcao.setId(3);
        when(familias.findById(2)).thenReturn(Optional.of(familia));
        when(funcoes.findById(3)).thenReturn(Optional.of(funcao));
        when(encoder.encode(any(CharSequence.class))).thenReturn("hash-senha-provisoria");
        when(usuarios.save(any(Usuario.class))).thenAnswer(invocacao -> {
            Usuario usuario = invocacao.getArgument(0);
            usuario.setId(42);
            return usuario;
        });

        var resposta = servico().cadastrar(dados("joao@example.com"));

        var usuarioSalvo = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarios).save(usuarioSalvo.capture());
        var usuario = usuarioSalvo.getValue();
        assertTrue(usuario.isAtivo());
        assertTrue(usuario.isPrimeiroAcesso());
        assertNotNull(usuario.getDataCriacao());
        assertEquals("hash-senha-provisoria", usuario.getSenha());
        var senha = ArgumentCaptor.forClass(CharSequence.class);
        verify(encoder).encode(senha.capture());
        assertTrue(senha.getValue().toString().matches("[A-Z0-9]{6}"));
        var mensagem = ArgumentCaptor.forClass(String.class);
        verify(emails).enviarEmail(eq("joao@example.com"), eq("Senha provisória"), mensagem.capture());
        assertTrue(mensagem.getValue().contains(senha.getValue()));
        assertEquals(42, resposta.id());
        assertEquals("João da Silva", resposta.nome());
        assertEquals("joao@example.com", resposta.email());
        assertEquals(2, resposta.familia().id());
        assertEquals(3, resposta.funcaoSistema().id());
    }

    @Test
    void rejeitaEmailJaCadastradoConsultandoEmailNormalizado() {
        when(usuarios.existsByEmail("joao@example.com")).thenReturn(true);

        assertThrows(AlreadyExistsByEmailException.class,
                () -> servico().cadastrar(dados(" Joao@Example.com ")));

        verify(usuarios, never()).save(any());
        verifyNoInteractions(familias, funcoes, encoder, emails);
    }

    @Test
    void rejeitaFamiliaInexistenteSemSalvarOuEnviarEmail() {
        when(familias.findById(2)).thenReturn(Optional.empty());

        assertThrows(NotExistsByIdException.class,
                () -> servico().cadastrar(dados("joao@example.com")));

        verify(usuarios, never()).save(any());
        verifyNoInteractions(funcoes, encoder, emails);
    }

    @Test
    void rejeitaFuncaoInexistenteSemSalvarOuEnviarEmail() {
        when(familias.findById(2)).thenReturn(Optional.of(new Familia()));
        when(funcoes.findById(3)).thenReturn(Optional.empty());

        assertThrows(NotExistsByIdException.class,
                () -> servico().cadastrar(dados("joao@example.com")));

        verify(usuarios, never()).save(any());
        verifyNoInteractions(encoder, emails);
    }

    private UsuarioService servico() {
        return new UsuarioService(usuarios, familias, funcoes, encoder, emails);
    }

    private UsuarioCreateRequest dados(String email) {
        return new UsuarioCreateRequest(2, "João da Silva", email, "11999999999",
                LocalDate.of(1999, 9, 19), 3);
    }
}
