package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;

    public UsuarioDetailsService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario =
                repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                        "Credenciais inválidas!"));

        return new Principal(usuario);
    }

    public static final class Principal extends User {
        private final Integer id;
        private final boolean primeiroAcesso;

        private Principal(Usuario usuario) {
            super(usuario.getEmail(), usuario.getSenha(), usuario.isAtivo(), true, true, true,
                    List.of(new SimpleGrantedAuthority("USUARIO_AUTENTICADO")));
            this.id = usuario.getId();
            this.primeiroAcesso = usuario.isPrimeiroAcesso();
        }

        public Integer getId() {
            return id;
        }

        public boolean isPrimeiroAcesso() {
            return primeiroAcesso;
        }
    }
}
