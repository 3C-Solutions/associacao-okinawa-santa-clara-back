package com.clara_santa_okinawa_associacao.associacao_okinawa_api;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.configs.UsuarioTesteInitializer;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth.LoginRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Endereco;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Familia;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncaoSistema;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncionalidadeSistema;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FuncaoSistemaRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.EnderecoRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FamiliaRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FuncionalidadeSistemaRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioTesteInitializerTests {

    @Mock
    private UsuarioRepository usuarios;
    @Mock
    private FuncaoSistemaRepository funcoes;
    @Mock
    private FuncionalidadeSistemaRepository funcionalidades;
    @Mock
    private FamiliaRepository familias;
    @Mock
    private EnderecoRepository enderecos;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private Validator validator;

    @BeforeEach
    void prepararValidacao() {
        when(validator.validate(any(LoginRequest.class))).thenReturn(Set.of());
    }

    private UsuarioTesteInitializer initializer(String senha) {
        return new UsuarioTesteInitializer(usuarios, funcoes, funcionalidades, familias, enderecos, encoder, validator,
                " Swagger@Example.com ", senha);
    }

    @Test
    void criaUsuarioAtivoComSenhaCodificadaEPermissao() {
        when(funcionalidades.findByCodigo("TELA_USUARIOS")).thenReturn(Optional.empty());
        when(funcionalidades.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(funcoes.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(encoder.encode("SenhaLocal123!")).thenReturn("hash-bcrypt");
        when(enderecos.save(any())).thenAnswer(invocation -> {
            Endereco endereco = invocation.getArgument(0);
            endereco.setId(10);
            return endereco;
        });
        when(familias.save(any())).thenAnswer(invocation -> {
            Familia familia = invocation.getArgument(0);
            assertNotNull(familia.getNome());
            assertNotNull(familia.getEndereco().getId());
            familia.setId(20);
            return familia;
        });

        initializer("SenhaLocal123!").run();

        ArgumentCaptor<Usuario> conta = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarios).save(conta.capture());
        Usuario usuario = conta.getValue();
        assertEquals("swagger@example.com", usuario.getEmail());
        assertEquals("hash-bcrypt", usuario.getSenha());
        assertTrue(usuario.isAtivo());
        assertNotNull(usuario.getDataCriacao());
        assertNotNull(usuario.getTelefone());
        assertFalse(usuario.getTelefone().isBlank());
        assertNotNull(usuario.getDataNascimento());
        assertTrue(usuario.getDataNascimento().isBefore(LocalDate.now()));
        assertNotNull(usuario.getFamilia());
        assertEquals(20, usuario.getFamilia().getId());
        ArgumentCaptor<Endereco> endereco = ArgumentCaptor.forClass(Endereco.class);
        verify(enderecos).save(endereco.capture());
        assertNotNull(endereco.getValue().getRua());
        assertNotNull(endereco.getValue().getBairro());
        assertNotNull(endereco.getValue().getCidade());
        assertNotNull(endereco.getValue().getNumero());
        assertNotNull(endereco.getValue().getUf());
        assertNotNull(endereco.getValue().getCep());
        assertNotNull(endereco.getValue().getComplemento());
        var ordem = inOrder(enderecos, familias, usuarios);
        ordem.verify(enderecos).save(any());
        ordem.verify(familias).save(any());
        ordem.verify(usuarios).save(any());
        assertEquals(Set.of("TELA_USUARIOS"), usuario.getFuncaoSistema().getFuncionalidades()
                .stream().map(FuncionalidadeSistema::getCodigo).collect(java.util.stream.Collectors.toSet()));
    }

    @Test
    void preservaContaExistenteSemAlterarSenhaOuPermissoes() {
        when(usuarios.existsByEmail("swagger@example.com")).thenReturn(true);
        initializer("SenhaLocal123!").run();
        verify(usuarios, never()).save(any());
        verifyNoInteractions(funcoes, funcionalidades, familias, enderecos, encoder);
    }

    @Test
    void reutilizaPermissaoExistente() {
        FuncionalidadeSistema permissao = new FuncionalidadeSistema();
        permissao.setCodigo("TELA_USUARIOS");
        when(funcionalidades.findByCodigo("TELA_USUARIOS")).thenReturn(Optional.of(permissao));
        when(funcoes.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        initializer("SenhaLocal123!").run();
        verify(funcionalidades, never()).save(any());
        ArgumentCaptor<FuncaoSistema> funcao = ArgumentCaptor.forClass(FuncaoSistema.class);
        verify(funcoes).save(funcao.capture());
        assertTrue(funcao.getValue().getFuncionalidades().contains(permissao));
    }

    @Test
    void rejeitaSenhaCurtaAntesDeAlterarBanco() {
        assertThrows(IllegalStateException.class, () -> initializer("curta").run());
        verifyNoInteractions(usuarios, funcoes, funcionalidades, familias, enderecos, encoder);
    }
}
