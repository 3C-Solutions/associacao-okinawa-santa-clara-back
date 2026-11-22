package com.clara_santa_okinawa_associacao.associacao_okinawa_api;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth.LoginRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth.LoginResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.AuthService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.TokenService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {
    @Mock private AuthenticationManager authenticationManager;
    @Mock private TokenService tokenService;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void loginAutenticaCredenciaisEGeraTokenRespeitandoPrimeiroAcesso(boolean primeiroAcesso) {
        var principal = mock(UsuarioDetailsService.Principal.class);
        when(principal.getId()).thenReturn(42);
        when(principal.isPrimeiroAcesso()).thenReturn(primeiroAcesso);
        var autenticacao = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(autenticacao);
        var token = new LoginResponse("token-de-teste", "Bearer", 900, primeiroAcesso);
        when(tokenService.gerarToken(42, primeiroAcesso)).thenReturn(token);
        var servico = new AuthService(authenticationManager, tokenService);

        var resposta = servico.login(new LoginRequest("joao@example.com", "SenhaDeTeste123!"));

        var credenciais = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(credenciais.capture());
        assertEquals("joao@example.com", credenciais.getValue().getPrincipal());
        assertEquals("SenhaDeTeste123!", credenciais.getValue().getCredentials());
        assertEquals(token, resposta);
    }

    @Test
    void credenciaisInvalidasNaoGeramToken() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Credenciais inválidas!"));
        var servico = new AuthService(authenticationManager, tokenService);

        assertThrows(BadCredentialsException.class,
                () -> servico.login(new LoginRequest("joao@example.com", "senha-incorreta")));

        verifyNoInteractions(tokenService);
    }

    @Test
    void usuarioDesativadoNaoRecebeToken() {
        when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("Usuário desativado"));
        var servico = new AuthService(authenticationManager, tokenService);

        assertThrows(DisabledException.class,
                () -> servico.login(new LoginRequest("joao@example.com", "SenhaDeTeste123!")));

        verifyNoInteractions(tokenService);
    }
}
