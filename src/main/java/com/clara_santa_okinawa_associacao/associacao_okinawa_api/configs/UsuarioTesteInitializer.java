package com.clara_santa_okinawa_associacao.associacao_okinawa_api.configs;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Component
@Profile("dev")
@ConditionalOnProperty(name = "app.bootstrap.enabled", havingValue = "true")
public class UsuarioTesteInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UsuarioTesteInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final FuncaoSistemaRepository funcaoRepository;
    private final FuncionalidadeSistemaRepository funcionalidadeRepository;
    private final FamiliaRepository familiaRepository;
    private final EnderecoRepository enderecoRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final String email;
    private final String senha;

    public UsuarioTesteInitializer(UsuarioRepository usuarioRepository,
                                  FuncaoSistemaRepository funcaoRepository,
                                  FuncionalidadeSistemaRepository funcionalidadeRepository,
                                  FamiliaRepository familiaRepository,
                                  EnderecoRepository enderecoRepository,
                                  PasswordEncoder passwordEncoder,
                                  Validator validator,
                                  @Value("${app.bootstrap.email}") String email,
                                  @Value("${app.bootstrap.password}") String senha) {
        this.usuarioRepository = usuarioRepository;
        this.funcaoRepository = funcaoRepository;
        this.funcionalidadeRepository = funcionalidadeRepository;
        this.familiaRepository = familiaRepository;
        this.enderecoRepository = enderecoRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.email = email.trim().toLowerCase(Locale.ROOT);
        this.senha = senha;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!validator.validate(new LoginRequest(email, senha)).isEmpty() || senha.length() < 8) {
            throw new IllegalStateException("Informe APP_BOOTSTRAP_EMAIL válido e "
                    + "APP_BOOTSTRAP_PASSWORD com pelo menos 8 caracteres para criar o usuário de teste.");
        }

        if (usuarioRepository.existsByEmail(email)) {
            log.info("Usuário de teste já cadastrado; conta, senha e permissões preservadas.");
            return;
        }

        FuncionalidadeSistema funcionalidade = funcionalidadeRepository.findByCodigo("TELA_USUARIOS")
                .orElseGet(() -> {
                    FuncionalidadeSistema nova = new FuncionalidadeSistema();
                    nova.setCodigo("TELA_USUARIOS");
                    nova.setNome("Gerenciar usuários");
                    nova.setDescricao("Acesso aos endpoints de usuários");
                    return funcionalidadeRepository.save(nova);
                });

        FuncaoSistema funcao = new FuncaoSistema();
        funcao.setNome("Teste local Swagger");
        funcao.setDescricao("Função criada pela carga inicial do perfil dev");
        funcao.getFuncionalidades().add(funcionalidade);
        funcao = funcaoRepository.save(funcao);

        Usuario usuario = new Usuario();
        usuario.setNome("Usuário de teste Swagger");
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setTelefone("11999999999");
        usuario.setDataNascimento(LocalDate.of(2000, 1, 1));
        usuario.setFamilia(criarFamiliaTeste());
        usuario.setAtivo(true);
        usuario.setFuncaoSistema(funcao);
        usuario.setDataCriacao(LocalDateTime.now());
        usuarioRepository.save(usuario);
        log.info("Usuário de teste criado com a permissão TELA_USUARIOS.");
    }

    private Familia criarFamiliaTeste() {
        Endereco endereco = new Endereco();
        endereco.setRua("Rua de teste Swagger");
        endereco.setBairro("Bairro de teste");
        endereco.setCidade("São Paulo");
        endereco.setNumero("0");
        endereco.setUf("SP");
        endereco.setCep("00000000");
        endereco.setComplemento("Dados fictícios para testes locais");
        endereco = enderecoRepository.save(endereco);

        Familia familia = new Familia();
        familia.setNome("Família de teste Swagger");
        familia.setEndereco(endereco);
        return familiaRepository.save(familia);
    }
}
