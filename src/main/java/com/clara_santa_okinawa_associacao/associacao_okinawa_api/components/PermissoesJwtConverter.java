package com.clara_santa_okinawa_associacao.associacao_okinawa_api.components;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncionalidadeSistema;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class PermissoesJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UsuarioRepository usuarioRepository;

    public PermissoesJwtConverter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    @Override
    @Transactional
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Integer usuarioId;

        try {
            usuarioId = Integer.valueOf(jwt.getSubject());
        } catch (NumberFormatException e) {
            throw tokenInvalido();
        }

        Usuario usuario =
                usuarioRepository.buscarComPermissoes(usuarioId).orElseThrow(this::tokenInvalido);

        if (!usuario.isAtivo() || usuario.getFuncaoSistema() == null) throw tokenInvalido();

        FinalidadeToken finalidade = FinalidadeToken.de(usuario.isPrimeiroAcesso());
        if (!finalidade.corresponde(jwt)) throw tokenInvalido();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(finalidade.autoridade()));
        if (finalidade == FinalidadeToken.ACESSO) {
            usuario.getFuncaoSistema().getFuncionalidades().stream()
                    .map(FuncionalidadeSistema::getCodigo)
                    .filter(codigo -> !FinalidadeToken.autoridadeReservada(codigo))
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }

        return new JwtAuthenticationToken(
                jwt,
                authorities,
                usuario.getEmail()
        );
    }

    private OAuth2AuthenticationException tokenInvalido() {
        return new OAuth2AuthenticationException(
                BearerTokenErrors.invalidToken("Usuário ou finalidade do token sem acesso válido")
        );
    }
}
