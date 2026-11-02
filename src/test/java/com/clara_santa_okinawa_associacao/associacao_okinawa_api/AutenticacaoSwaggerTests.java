package com.clara_santa_okinawa_associacao.associacao_okinawa_api;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.components.PermissoesJwtConverter;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.configs.JwtConfig;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.configs.OpenApiConfig;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.configs.SecurityConfig;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers.AuthController;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers.UsuarioController;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncaoSistema;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncionalidadeSistema;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.GlobalExceptionHandler;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.UsuarioAutenticadoService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.AuthService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.TokenService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.UsuarioDetailsService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.UsuarioService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = AutenticacaoSwaggerTests.TestConfig.class, properties = {
        "app.jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=",
        "app.jwt.issuer=associacao-api",
        "app.jwt.expiration-minutes=15"
})
@AutoConfigureMockMvc
class AutenticacaoSwaggerTests {

    @Configuration(proxyBeanMethods = false)
    @TestComponent
    @EnableAutoConfiguration(excludeName = {
            "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
            "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
            "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration"
    })
    @Import({SecurityConfig.class, JwtConfig.class, OpenApiConfig.class,
            PermissoesJwtConverter.class, AuthController.class, UsuarioController.class,
            UsuarioAutenticadoService.class, AuthService.class, TokenService.class, UsuarioDetailsService.class,
            GlobalExceptionHandler.class})
    static class TestConfig {
    }

    @Autowired
    private MockMvc mvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private JwtEncoder jwtEncoder;
    @MockitoBean
    private UsuarioRepository usuarioRepository;
    @MockitoBean
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void prepararUsuario() {
        FuncionalidadeSistema permissao = new FuncionalidadeSistema();
        permissao.setCodigo("TELA_USUARIOS");
        FuncaoSistema funcao = new FuncaoSistema();
        funcao.setId(2);
        funcao.setNome("Diretoria");
        funcao.setDescricao("Administração da associação");
        funcao.getFuncionalidades().add(permissao);
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNome("João da Silva");
        usuario.setEmail("teste@example.com");
        usuario.setSenha(passwordEncoder.encode("SenhaDeTeste123!"));
        usuario.setAtivo(true);
        usuario.setFuncaoSistema(funcao);
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.buscarComPermissoes(1)).thenReturn(Optional.of(usuario));
        when(usuarioService.listar()).thenReturn(List.of());
    }

    @Test
    void swaggerDocumentaBearerELoginPublico() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.security[0].bearerAuth").isArray())
                .andExpect(jsonPath("$.paths['/auth/me'].get.responses['200'].content['application/json'].schema['$ref']")
                        .value("#/components/schemas/UsuarioAutenticadoContextoResponse"))
                .andExpect(jsonPath("$.paths['/auth/me'].get.security").doesNotExist())
                .andExpect(jsonPath("$.components.schemas.UsuarioAutenticadoContextoResponse.properties", aMapWithSize(2)))
                .andExpect(jsonPath("$.components.schemas.UsuarioAutenticadoContextoResponse.properties.permissoes.type").value("array"))
                .andExpect(jsonPath("$.components.schemas.UsuarioAutenticadoContextoResponse.properties.telas").doesNotExist())
                .andExpect(jsonPath("$.paths['/auth/acessos']").doesNotExist())
                .andExpect(jsonPath("$.paths['/auth/login'].post.security", hasSize(0)));
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }

    @Test
    void loginRetornaTokenQuePermiteAcessarUsuarios() throws Exception {
        String resposta = mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"teste@example.com","senha":"SenhaDeTeste123!"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900))
                .andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(resposta, "$.accesToken");
        mvc.perform(get("/usuarios").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.telas").doesNotExist())
                .andExpect(jsonPath("$.usuario", aMapWithSize(4)))
                .andExpect(jsonPath("$.usuario.funcaoSistema", aMapWithSize(3)))
                .andExpect(jsonPath("$.usuario.senha").doesNotExist())
                .andExpect(jsonPath("$.usuario.familia").doesNotExist())
                .andExpect(content().json("""
                        {
                          "usuario": {
                            "id": 1,
                            "nome": "João da Silva",
                            "email": "teste@example.com",
                            "funcaoSistema": {
                              "id": 2,
                              "nome": "Diretoria",
                              "descricao": "Administração da associação"
                            }
                          },
                          "permissoes": ["TELA_USUARIOS"]
                        }
                        """));
    }

    @Test
    void meRetornaPermissoesOrdenadasSemDuplicidades() throws Exception {
        adicionarPermissao(usuario, "TELA_FUNCOES_SISTEMA");
        adicionarPermissao(usuario, "TELA_USUARIOS");

        mvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + tokenService.gerarToken(1).accesToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissoes", hasSize(2)))
                .andExpect(jsonPath("$.permissoes[0]").value("TELA_FUNCOES_SISTEMA"))
                .andExpect(jsonPath("$.permissoes[1]").value("TELA_USUARIOS"));
    }

    @Test
    void funcaoSemPermissoesPodeConsultarSeusAcessosVazios() throws Exception {
        usuario.getFuncaoSistema().getFuncionalidades().clear();

        mvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + tokenService.gerarToken(1).accesToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario.id").value(1))
                .andExpect(jsonPath("$.permissoes").isEmpty());
    }

    @Test
    void todasAsPermissoesDaFuncaoCorrespondemATelasIndependentementeDoPrefixo() throws Exception {
        usuario.getFuncaoSistema().getFuncionalidades().clear();
        adicionarPermissao(usuario, "RELATORIOS");

        mvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + tokenService.gerarToken(1).accesToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissoes", hasSize(1)))
                .andExpect(jsonPath("$.permissoes[0]").value("RELATORIOS"));
    }

    @Test
    void acessosSaoDoUsuarioIdentificadoPeloToken() throws Exception {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2);
        outroUsuario.setNome("Outro usuário");
        outroUsuario.setEmail("outro@example.com");
        outroUsuario.setAtivo(true);
        outroUsuario.setFuncaoSistema(new FuncaoSistema());
        adicionarPermissao(outroUsuario, "TELA_FUNCOES_SISTEMA");
        when(usuarioRepository.buscarComPermissoes(2)).thenReturn(Optional.of(outroUsuario));

        mvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + tokenService.gerarToken(2).accesToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario.id").value(2))
                .andExpect(jsonPath("$.usuario.nome").value("Outro usuário"))
                .andExpect(jsonPath("$.usuario.email").value("outro@example.com"))
                .andExpect(content().json("""
                        {"permissoes":["TELA_FUNCOES_SISTEMA"]}
                        """));
    }

    @Test
    void alteracaoDeDadosEFuncaoAtualizaMeComMesmoToken() throws Exception {
        String token = tokenService.gerarToken(1).accesToken();
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario.nome").value("João da Silva"))
                .andExpect(jsonPath("$.usuario.funcaoSistema.id").value(2))
                .andExpect(jsonPath("$.permissoes[0]").value("TELA_USUARIOS"));

        usuario.setNome("João atualizado");
        usuario.setEmail("atualizado@example.com");
        FuncaoSistema novaFuncao = new FuncaoSistema();
        novaFuncao.setId(3);
        novaFuncao.setNome("Gestão de funções");
        usuario.setFuncaoSistema(novaFuncao);
        adicionarPermissao(usuario, "TELA_FUNCOES_SISTEMA");

        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario.nome").value("João atualizado"))
                .andExpect(jsonPath("$.usuario.email").value("atualizado@example.com"))
                .andExpect(jsonPath("$.usuario.funcaoSistema.id").value(3))
                .andExpect(jsonPath("$.usuario.funcaoSistema.nome").value("Gestão de funções"))
                .andExpect(content().json("""
                        {"permissoes":["TELA_FUNCOES_SISTEMA"]}
                        """));
        mvc.perform(get("/usuarios").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void endpointAntigoDeAcessosFoiRemovido() throws Exception {
        mvc.perform(get("/auth/acessos")
                        .header("Authorization", "Bearer " + tokenService.gerarToken(1).accesToken()))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"REMOVIDO", "INATIVO", "SEM_FUNCAO"})
    void revogacaoEntreAutenticacaoEConsultaRetorna401(String motivo) throws Exception {
        Usuario usuarioRevogado = new Usuario();
        usuarioRevogado.setAtivo(!motivo.equals("INATIVO"));
        usuarioRevogado.setFuncaoSistema(motivo.equals("SEM_FUNCAO") ? null : new FuncaoSistema());
        Optional<Usuario> resultadoConsulta = motivo.equals("REMOVIDO")
                ? Optional.empty() : Optional.of(usuarioRevogado);
        when(usuarioRepository.buscarComPermissoes(1))
                .thenReturn(Optional.of(usuario), resultadoConsulta);

        mvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + tokenService.gerarToken(1).accesToken()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioRemovidoNaoPodeConsultarAcessos() throws Exception {
        String token = tokenService.gerarToken(1).accesToken();
        when(usuarioRepository.buscarComPermissoes(1)).thenReturn(Optional.empty());

        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioSemFuncaoNaoPodeConsultarAcessos() throws Exception {
        String token = tokenService.gerarToken(1).accesToken();
        usuario.setFuncaoSistema(null);

        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void senhaIncorretaRetorna401() throws Exception {
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"teste@example.com","senha":"incorreta"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void usuarioDesativadoNaoConsegueLogar() throws Exception {
        usuario.setAtivo(false);
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"teste@example.com","senha":"SenhaDeTeste123!"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acessoSemTokenOuComTokenInvalidoRetorna401() throws Exception {
        mvc.perform(get("/usuarios")).andExpect(status().isUnauthorized());
        mvc.perform(get("/usuarios").header("Authorization", "Bearer invalido"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/auth/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/auth/me").header("Authorization", "Bearer invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenExpiradoRetorna401() throws Exception {
        Instant agora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer("associacao-api").subject("1")
                .issuedAt(agora.minusSeconds(1800)).expiresAt(agora.minusSeconds(300)).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        mvc.perform(get("/usuarios").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioSemPermissaoRetorna403() throws Exception {
        usuario.getFuncaoSistema().getFuncionalidades().clear();
        mvc.perform(get("/usuarios").header("Authorization", "Bearer " + tokenService.gerarToken(1).accesToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void desativacaoRevogaAcessoMesmoComTokenValido() throws Exception {
        String token = tokenService.gerarToken(1).accesToken();
        usuario.setAtivo(false);
        mvc.perform(get("/usuarios").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    private void adicionarPermissao(Usuario usuario, String codigo) {
        FuncionalidadeSistema permissao = new FuncionalidadeSistema();
        permissao.setCodigo(codigo);
        usuario.getFuncaoSistema().getFuncionalidades().add(permissao);
    }
}
