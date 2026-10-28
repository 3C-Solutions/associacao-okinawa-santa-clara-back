package com.clara_santa_okinawa_associacao.associacao_okinawa_api.components;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

        List<GrantedAuthority> authorities =
                usuario.getFuncaoSistema()
                        .getFuncionalidades()
                        .stream()
                        .<GrantedAuthority>map(funcionalidade ->
                                new SimpleGrantedAuthority(
                                        funcionalidade.getCodigo()
                                )
                        )
                        .toList();

        return new JwtAuthenticationToken(
                jwt,
                authorities,
                usuario.getEmail()
        );
    }

    private OAuth2AuthenticationException tokenInvalido() {
        return new OAuth2AuthenticationException(
                new OAuth2Error("invalid token")
        );
    }
}
