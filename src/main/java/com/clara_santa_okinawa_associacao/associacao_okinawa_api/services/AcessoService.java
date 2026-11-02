package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth.AcessosResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AcessoService {

    public AcessosResponse listar(Collection<? extends GrantedAuthority> authorities) {
        return new AcessosResponse(authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(codigo -> codigo.startsWith("TELA_"))
                .distinct()
                .sorted()
                .toList());
    }
}
