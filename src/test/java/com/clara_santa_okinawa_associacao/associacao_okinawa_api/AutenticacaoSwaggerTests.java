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
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.AuthService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.TokenService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.UsuarioDetailsService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.UsuarioService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
            AuthService.class, TokenService.class, UsuarioDetailsService.class,
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
        funcao.getFuncionalidades().add(permissao);
        usuario = new Usuario();
        usuario.setId(1);
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
    }
}
