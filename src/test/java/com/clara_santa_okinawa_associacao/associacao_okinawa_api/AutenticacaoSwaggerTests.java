package com.clara_santa_okinawa_associacao.associacao_okinawa_api;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.components.PermissoesJwtConverter;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.configs.JwtConfig;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.configs.OpenApiConfig;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.configs.SecurityConfig;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers.AuthController;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers.MinhaContaController;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.MinhaContaService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers.FamiliaController;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers.EnderecoController;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FamiliaService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.EnderecoService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.EmailService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaComEnderecoRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.NotExistsByIdException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers.FuncionalidadeSistemaController;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FuncionalidadeSistemaService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FuncionalidadeSistemaRepository;
import org.springframework.data.domain.Sort;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers.FuncaoSistemaController;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaCreateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario.UsuarioUpdateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FamiliaRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FuncaoSistemaRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FuncaoSistemaService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers.UsuarioController;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncaoSistema;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncionalidadeSistema;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.GlobalExceptionHandler;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.UsuarioAutenticadoService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.AuthService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.EmailService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.DespesasService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FinanceiroDashboardService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers.DespesasController;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.controllers.FinanceiroDashboardController;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.DespesasCreateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.TokenService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.UsuarioDetailsService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.UsuarioService;
import com.jayway.jsonpath.JsonPath;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaFuncionalidadesRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
            "org.springframework.boot.mail.autoconfigure.MailSenderAutoConfiguration",
            "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
            "org.springframework.boot.mail.autoconfigure.MailSenderAutoConfiguration",
            "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
            "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration"
    })
    @Import({SecurityConfig.class, JwtConfig.class, OpenApiConfig.class,
            PermissoesJwtConverter.class, AuthController.class, UsuarioController.class, FuncaoSistemaController.class,
            UsuarioAutenticadoService.class, AuthService.class, TokenService.class, UsuarioDetailsService.class,
            GlobalExceptionHandler.class, FuncionalidadeSistemaController.class, FuncionalidadeSistemaService.class,
            DespesasController.class, FinanceiroDashboardController.class,
            FamiliaController.class, EnderecoController.class, MinhaContaController.class, MinhaContaService.class})
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
    @MockitoBean
    private FuncaoSistemaService funcaoSistemaService;
    @MockitoBean
    private FuncionalidadeSistemaRepository funcionalidadeSistemaRepository;
    @MockitoBean
    private FamiliaService familiaService;
    @MockitoBean
    private EnderecoService enderecoService;

    private Usuario usuario;

    @MockitoBean
    private DespesasService despesasService;
    @MockitoBean
    private FinanceiroDashboardService financeiroService;

    @Test
    void financeiroExigeAutenticacaoEPermissao() throws Exception {
        mvc.perform(get("/api/financeiro/dashboard?ano=2026&mes=11"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/financeiro/dashboard?ano=2026&mes=11")
                .header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(financeiroService);
    }

    @Test
    void despesaUsaIdDoTokenERejeitaValorComMaisDeDuasCasas() throws Exception {
        adicionarPermissao(usuario, "TELA_FINANCEIRO");
        String token = tokenService.gerarToken(1, false).accesToken();
        mvc.perform(post("/api/financeiro/despesas").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"dtDepesa":"2026-11-03","descricao":"Materiais","categoria":"Outros","valor":12.50,"status":"Pago","fkUsuario":999}
                    """))
                .andExpect(status().isCreated());
        verify(despesasService).criar(any(DespesasCreateRequest.class), eq(1));
        mvc.perform(post("/api/financeiro/despesas").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"dtDepesa":"2026-11-03","descricao":"Materiais","categoria":"Outros","valor":12.501,"status":"Pago"}
                    """))
                .andExpect(status().isBadRequest());
        verify(despesasService, times(1)).criar(any(), eq(1));
    }

    @BeforeEach
    void prepararUsuario() {
        prepararUsuario(false);
    }

    private void prepararUsuario(boolean primeiroAcesso) {
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
        if (!primeiroAcesso) {
            usuario.concluirPrimeiroAcesso();
        }
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
                .andExpect(jsonPath("$.components.schemas.UsuarioAutenticadoContextoResponse.properties", aMapWithSize(3)))
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
                .andExpect(jsonPath("$", aMapWithSize(3)))
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
                        .header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissoes", hasSize(2)))
                .andExpect(jsonPath("$.permissoes[0]").value("TELA_FUNCOES_SISTEMA"))
                .andExpect(jsonPath("$.permissoes[1]").value("TELA_USUARIOS"));
    }

    @Test
    void funcaoSemPermissoesPodeConsultarSeusAcessosVazios() throws Exception {
        usuario.getFuncaoSistema().getFuncionalidades().clear();

        mvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario.id").value(1))
                .andExpect(jsonPath("$.permissoes").isEmpty());
    }

    @Test
    void todasAsPermissoesDaFuncaoCorrespondemATelasIndependentementeDoPrefixo() throws Exception {
        usuario.getFuncaoSistema().getFuncionalidades().clear();
        adicionarPermissao(usuario, "RELATORIOS");

        mvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissoes", hasSize(1)))
                .andExpect(jsonPath("$.permissoes[0]").value("RELATORIOS"));
    }

    @Test
    void acessosSaoDoUsuarioIdentificadoPeloToken() throws Exception {
        Usuario outroUsuario = new Usuario();
        outroUsuario.concluirPrimeiroAcesso();
        outroUsuario.setId(2);
        outroUsuario.setNome("Outro usuário");
        outroUsuario.setEmail("outro@example.com");
        outroUsuario.setAtivo(true);
        outroUsuario.setFuncaoSistema(new FuncaoSistema());
        adicionarPermissao(outroUsuario, "TELA_FUNCOES_SISTEMA");
        when(usuarioRepository.buscarComPermissoes(2)).thenReturn(Optional.of(outroUsuario));

        mvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + tokenService.gerarToken(2, false).accesToken()))
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
        String token = tokenService.gerarToken(1, false).accesToken();
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
                        .header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken()))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"REMOVIDO", "INATIVO", "SEM_FUNCAO"})
    void revogacaoEntreAutenticacaoEConsultaRetorna401(String motivo) throws Exception {
        Usuario usuarioRevogado = new Usuario();
        usuarioRevogado.concluirPrimeiroAcesso();
        usuarioRevogado.setAtivo(!motivo.equals("INATIVO"));
        usuarioRevogado.setFuncaoSistema(motivo.equals("SEM_FUNCAO") ? null : new FuncaoSistema());
        Optional<Usuario> resultadoConsulta = motivo.equals("REMOVIDO")
                ? Optional.empty() : Optional.of(usuarioRevogado);
        when(usuarioRepository.buscarComPermissoes(1))
                .thenReturn(Optional.of(usuario), resultadoConsulta);

        mvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioRemovidoNaoPodeConsultarAcessos() throws Exception {
        String token = tokenService.gerarToken(1, false).accesToken();
        when(usuarioRepository.buscarComPermissoes(1)).thenReturn(Optional.empty());

        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioSemFuncaoNaoPodeConsultarAcessos() throws Exception {
        String token = tokenService.gerarToken(1, false).accesToken();
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

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void tokenExpiradoOuSemFinalidadeRetorna401(boolean expirado) throws Exception {
        Instant agora = Instant.now();
        var claims = JwtClaimsSet.builder().issuer("associacao-api").subject("1")
                .issuedAt(agora.minusSeconds(1800)).expiresAt(agora.plusSeconds(expirado ? -300 : 300));
        if (expirado) {
            claims.claim("finalidade", "ACESSO");
        }
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims.build())).getTokenValue();
        mvc.perform(get("/usuarios").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioSemPermissaoRetorna403() throws Exception {
        usuario.getFuncaoSistema().getFuncionalidades().clear();
        mvc.perform(get("/usuarios").header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void desativacaoRevogaAcessoMesmoComTokenValido() throws Exception {
        String token = tokenService.gerarToken(1, false).accesToken();
        usuario.setAtivo(false);
        mvc.perform(get("/usuarios").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {"TELA_USUARIOS", "TELA_FUNCOES_SISTEMA", "OUTRA_PERMISSAO"})
    void consultaECadastroDeFuncoesRespeitamPermissoesDistintas(String permissao) throws Exception {
        usuario.getFuncaoSistema().getFuncionalidades().clear();
        adicionarPermissao(usuario, permissao);
        when(funcaoSistemaService.listar()).thenReturn(List.of(new FuncaoSistemaResponse(2, "Diretoria", "")));
        when(funcaoSistemaService.cadastrar(any(FuncaoSistemaCreateRequest.class)))
                .thenReturn(new FuncaoSistemaResponse(3, "Nova função", ""));
        String token = tokenService.gerarToken(1, false).accesToken();
        boolean consultaPermitida = !permissao.equals("OUTRA_PERMISSAO");

        var consulta = mvc.perform(get("/funcoes-sistema").header("Authorization", "Bearer " + token));
        if (consultaPermitida) {
            consulta.andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(2));
        } else {
            consulta.andExpect(status().isForbidden());
        }
        var cadastro = mvc.perform(post("/funcoes-sistema").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nome":"Nova função","idsFuncionalidades":[1]}
                        """));
        if (permissao.equals("TELA_FUNCOES_SISTEMA")) {
            cadastro.andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(3));
        } else {
            cadastro.andExpect(status().isForbidden());
        }
    }

    @Test
    void consultaDeFuncoesSemTokenRetorna401() throws Exception {
        mvc.perform(get("/funcoes-sistema")).andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {"MANTER_ACESSO", "REVOGAR_ACESSO", "DESATIVAR"})
    void edicaoDaPropriaContaAtualizaFuncaoEPermissoesComMesmoToken(String cenario) throws Exception {
        boolean ativo = !cenario.equals("DESATIVAR");
        boolean manterAcesso = cenario.equals("MANTER_ACESSO");
        FuncaoSistema novaFuncao = new FuncaoSistema();
        novaFuncao.setId(3);
        novaFuncao.setNome("Nova função");
        FuncionalidadeSistema funcionalidade = new FuncionalidadeSistema();
        funcionalidade.setCodigo(manterAcesso ? "TELA_USUARIOS" : "TELA_FUNCOES_SISTEMA");
        novaFuncao.getFuncionalidades().add(funcionalidade);
        FuncaoSistemaRepository funcoes = mock(FuncaoSistemaRepository.class);
        when(funcoes.findById(3)).thenReturn(Optional.of(novaFuncao));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
        // Serviço e mapper reais, com persistência simulada para dispensar o banco.
        UsuarioService servico = new UsuarioService(usuarioRepository, mock(FamiliaRepository.class), funcoes, passwordEncoder, mock(EmailService.class));
        when(usuarioService.atualizar(eq(1), any(UsuarioUpdateRequest.class)))
                .thenAnswer(invocacao -> servico.atualizar(1, invocacao.getArgument(1)));
        when(usuarioService.buscarPorId(1)).thenAnswer(invocacao -> servico.buscarPorId(1));
        String token = tokenService.gerarToken(1, false).accesToken();

        mvc.perform(put("/usuarios/1").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"João atualizado","telefone":"11988887777","ativo":%s,"idFuncaoSistema":3}
                                """.formatted(ativo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.funcaoSistema.id").value(3))
                .andExpect(jsonPath("$.ativo").value(ativo));
        verify(funcoes).findById(3);
        verify(usuarioRepository).save(usuario);

        var contexto = mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token));
        if (!ativo) {
            contexto.andExpect(status().isUnauthorized());
        } else {
            contexto.andExpect(status().isOk())
                    .andExpect(jsonPath("$.usuario.nome").value("João atualizado"))
                    .andExpect(jsonPath("$.usuario.funcaoSistema.id").value(3))
                    .andExpect(jsonPath("$.permissoes", hasSize(1)))
                    .andExpect(jsonPath("$.permissoes[0]").value(funcionalidade.getCodigo()));
        }
        var reabrir = mvc.perform(get("/usuarios/1").header("Authorization", "Bearer " + token));
        if (manterAcesso) {
            reabrir.andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("João atualizado"))
                    .andExpect(jsonPath("$.telefone").value("11988887777"))
                    .andExpect(jsonPath("$.funcaoSistema.id").value(3));
        } else if (ativo) {
            reabrir.andExpect(status().isForbidden());
        } else {
            reabrir.andExpect(status().isUnauthorized());
        }
    }

    @Test
    void catalogoExigeGerenciamentoDeFuncoesERetornaTodosOsCodigos() throws Exception {
        String token = tokenService.gerarToken(1, false).accesToken();
        mvc.perform(get("/funcionalidades-sistema")).andExpect(status().isUnauthorized());
        mvc.perform(get("/funcionalidades-sistema").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
        verifyNoInteractions(funcionalidadeSistemaRepository);
        adicionarPermissao(usuario, "TELA_FUNCOES_SISTEMA");
        FuncionalidadeSistema funcionalidade = funcionalidadeDeTeste();
        when(funcionalidadeSistemaRepository.findAll(Sort.by("id"))).thenReturn(List.of(funcionalidade));
        mvc.perform(get("/funcionalidades-sistema").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", aMapWithSize(4)))
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].nome").value("Relatórios"))
                .andExpect(jsonPath("$[0].descricao").value("Consulta de relatórios"))
                .andExpect(jsonPath("$[0].codigo").value("RELATORIOS"));
        verify(funcionalidadeSistemaRepository).findAll(Sort.by("id"));
    }

    @Test
    void catalogoVazioRetornaArrayVazio() throws Exception {
        adicionarPermissao(usuario, "TELA_FUNCOES_SISTEMA");
        when(funcionalidadeSistemaRepository.findAll(Sort.by("id"))).thenReturn(List.of());
        mvc.perform(get("/funcionalidades-sistema")
                        .header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken()))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"SEM_NOME", "NOME_LONGO", "DESCRICAO_LONGA", "SEM_FUNCIONALIDADES", "ID_INVALIDO"})
    void cadastroDeFuncaoValidaCamposAntesDeChamarServico(String cenario) throws Exception {
        adicionarPermissao(usuario, "TELA_FUNCOES_SISTEMA");
        String nome = cenario.equals("SEM_NOME") ? " " : cenario.equals("NOME_LONGO") ? "x".repeat(101) : "Nova função";
        String descricao = cenario.equals("DESCRICAO_LONGA") ? "x".repeat(256) : "Descrição";
        String ids = cenario.equals("SEM_FUNCIONALIDADES") ? "[]" : cenario.equals("ID_INVALIDO") ? "[0]" : "[7]";
        mvc.perform(post("/funcoes-sistema")
                        .header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"%s","descricao":"%s","idsFuncionalidades":%s}
                                """.formatted(nome, descricao, ids)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(funcaoSistemaService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"NOME_DUPLICADO", "FUNCIONALIDADE_REMOVIDA"})
    void cadastroDeFuncaoNaoSalvaQuandoNomeOuFuncionalidadesSaoInvalidos(String cenario) throws Exception {
        adicionarPermissao(usuario, "TELA_FUNCOES_SISTEMA");
        FuncaoSistemaRepository funcoes = mock(FuncaoSistemaRepository.class);
        when(funcoes.existsByNomeNormalizado("Nova função")).thenReturn(cenario.equals("NOME_DUPLICADO"));
        when(funcionalidadeSistemaRepository.findAllById(any())).thenReturn(List.of());
        FuncaoSistemaService servico = new FuncaoSistemaService(funcoes, funcionalidadeSistemaRepository);
        when(funcaoSistemaService.cadastrar(any(FuncaoSistemaCreateRequest.class)))
                .thenAnswer(invocacao -> servico.cadastrar(invocacao.getArgument(0)));
        mvc.perform(post("/funcoes-sistema")
                        .header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"  Nova função  ","idsFuncionalidades":[7]}
                                """))
                .andExpect(status().is(cenario.equals("NOME_DUPLICADO") ? 409 : 404));
        verify(funcoes, never()).save(any());
    }

    @Test
    void funcaoCriadaPodeSerListadaEAtribuidaComPermissoesReais() throws Exception {
        adicionarPermissao(usuario, "TELA_FUNCOES_SISTEMA");
        FuncaoSistemaRepository funcoes = mock(FuncaoSistemaRepository.class);
        FuncaoSistema[] criada = new FuncaoSistema[1];
        when(funcionalidadeSistemaRepository.findAllById(any())).thenReturn(List.of(funcionalidadeDeTeste()));
        when(funcoes.save(any(FuncaoSistema.class))).thenAnswer(invocacao -> {
            criada[0] = invocacao.getArgument(0);
            criada[0].setId(3);
            return criada[0];
        });
        when(funcoes.findAll(Sort.by("id"))).thenAnswer(invocacao -> List.of(criada[0]));
        when(funcoes.findById(3)).thenAnswer(invocacao -> Optional.ofNullable(criada[0]));
        FuncaoSistemaService servicoFuncoes = new FuncaoSistemaService(funcoes, funcionalidadeSistemaRepository);
        when(funcaoSistemaService.cadastrar(any(FuncaoSistemaCreateRequest.class)))
                .thenAnswer(invocacao -> servicoFuncoes.cadastrar(invocacao.getArgument(0)));
        when(funcaoSistemaService.listar()).thenAnswer(invocacao -> servicoFuncoes.listar());
        String token = tokenService.gerarToken(1, false).accesToken();
        mvc.perform(post("/funcoes-sistema").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"  Relatórios  ","descricao":"Consulta","idsFuncionalidades":[7,7]}
                                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nome").value("Relatórios"));
        mvc.perform(get("/funcoes-sistema").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(3));
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.usuario.funcaoSistema.id").value(2));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
        UsuarioService servicoUsuarios = new UsuarioService(usuarioRepository, mock(FamiliaRepository.class), funcoes, passwordEncoder, mock(EmailService.class));
        when(usuarioService.atualizar(eq(1), any(UsuarioUpdateRequest.class)))
                .thenAnswer(invocacao -> servicoUsuarios.atualizar(1, invocacao.getArgument(1)));
        mvc.perform(put("/usuarios/1").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"João","ativo":true,"idFuncaoSistema":3}
                                """))
                .andExpect(status().isOk());
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.usuario.funcaoSistema.id").value(3))
                .andExpect(jsonPath("$.permissoes", hasSize(1)))
                .andExpect(jsonPath("$.permissoes[0]").value("RELATORIOS"));
        mvc.perform(get("/usuarios").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    private FuncionalidadeSistema funcionalidadeDeTeste() {
        FuncionalidadeSistema funcionalidade = new FuncionalidadeSistema();
        funcionalidade.setId(7);
        funcionalidade.setNome("Relatórios");
        funcionalidade.setDescricao("Consulta de relatórios");
        funcionalidade.setCodigo("RELATORIOS");
        return funcionalidade;
    }

    @Test
    void detalhesEEdicaoDeAcessosExigemPermissaoAdministrativa() throws Exception {
        String token = tokenService.gerarToken(1, false).accesToken();
        mvc.perform(get("/funcoes-sistema/2")).andExpect(status().isUnauthorized());
        mvc.perform(put("/funcoes-sistema/2/funcionalidades").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idsFuncionalidades\":[]}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/funcoes-sistema/2").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
        mvc.perform(put("/funcoes-sistema/2/funcionalidades").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"idsFuncionalidades\":[]}"))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = {"LISTA_AUSENTE", "ID_INVALIDO"})
    void edicaoDeAcessosValidaIds(String cenario) throws Exception {
        adicionarPermissao(usuario, "TELA_FUNCOES_SISTEMA");
        String ids = cenario.equals("LISTA_AUSENTE") ? "null" : cenario.equals("ID_INVALIDO") ? "[0]" : "[]";
        mvc.perform(put("/funcoes-sistema/2/funcionalidades")
                        .header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idsFuncionalidades":%s}
                                """.formatted(ids)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(funcaoSistemaService);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void editarPerfilAtualizaTodosOsUsuariosComMesmoToken(boolean removerTodos) throws Exception {
        // Os dois usuários compartilham a mesma função, como ocorre na relação do banco.
        usuario.getFuncaoSistema().getFuncionalidades().clear();
        FuncionalidadeSistema gerenciar = funcionalidadeDeTeste();
        gerenciar.setCodigo("TELA_FUNCOES_SISTEMA");
        usuario.getFuncaoSistema().getFuncionalidades().add(gerenciar);
        Usuario outro = new Usuario();
        outro.concluirPrimeiroAcesso();
        outro.setId(2);
        outro.setNome("Outra pessoa");
        outro.setEmail("outra@example.com");
        outro.setAtivo(true);
        outro.setFuncaoSistema(usuario.getFuncaoSistema());
        when(usuarioRepository.buscarComPermissoes(2)).thenReturn(Optional.of(outro));
        FuncaoSistemaRepository funcoes = mock(FuncaoSistemaRepository.class);
        when(funcoes.buscarComFuncionalidades(2)).thenReturn(Optional.of(usuario.getFuncaoSistema()));
        when(funcoes.saveAndFlush(any(FuncaoSistema.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
        when(funcionalidadeSistemaRepository.findAllById(any())).thenReturn(removerTodos ? List.of() : List.of(funcionalidadeDeTeste()));
        FuncaoSistemaService servico = new FuncaoSistemaService(funcoes, funcionalidadeSistemaRepository);
        when(funcaoSistemaService.buscar(2)).thenAnswer(invocacao -> servico.buscar(2));
        when(funcaoSistemaService.atualizarFuncionalidades(eq(2), any(FuncaoSistemaFuncionalidadesRequest.class)))
                .thenAnswer(invocacao -> servico.atualizarFuncionalidades(2, invocacao.getArgument(1)));
        String token = tokenService.gerarToken(1, false).accesToken();
        String outroToken = tokenService.gerarToken(2, false).accesToken();
        mvc.perform(get("/funcoes-sistema/2").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$", aMapWithSize(4)))
                .andExpect(jsonPath("$.funcionalidades[0].codigo").value("TELA_FUNCOES_SISTEMA"));
        mvc.perform(put("/funcoes-sistema/2/funcionalidades").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idsFuncionalidades":%s}
                                """.formatted(removerTodos ? "[]" : "[7]")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.funcionalidades", hasSize(removerTodos ? 0 : 1)));
        for (String credencial : List.of(token, outroToken)) {
            var me = mvc.perform(get("/auth/me").header("Authorization", "Bearer " + credencial))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.permissoes", hasSize(removerTodos ? 0 : 1)));
            if (!removerTodos) me.andExpect(jsonPath("$.permissoes[0]").value("RELATORIOS"));
            mvc.perform(get("/funcoes-sistema/2").header("Authorization", "Bearer " + credencial))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    void listaDeUsuariosVaziaRetornaArrayJson() throws Exception {
        when(usuarioRepository.findAll()).thenReturn(List.of());
        UsuarioService servico = new UsuarioService(usuarioRepository, mock(FamiliaRepository.class), mock(FuncaoSistemaRepository.class), passwordEncoder, mock(EmailService.class));
        when(usuarioService.listar()).thenAnswer(invocacao -> servico.listar());
        mvc.perform(get("/usuarios").header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken()))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/familias", "/familias/1"})
    void consultasFamiliaExigemPermissaoEspecifica(String caminho) throws Exception {
        mvc.perform(get(caminho)).andExpect(status().isUnauthorized());
        mvc.perform(get(caminho).header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(familiaService);
    }

    @Test
    void listaFamiliasVaziaEDetalheInexistente() throws Exception {
        adicionarPermissao(usuario, "TELA_FAMILIAS");
        when(familiaService.listar()).thenReturn(List.of());
        when(familiaService.buscarPorId(9)).thenThrow(new NotExistsByIdException("Família não encontrada: 9"));
        String token = tokenService.gerarToken(1, false).accesToken();
        mvc.perform(get("/familias").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(get("/familias/9").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void cadastroConjuntoEConsultasExigemApenasPermissaoFamilias() throws Exception {
        usuario.getFuncaoSistema().getFuncionalidades().clear();
        adicionarPermissao(usuario, "TELA_FAMILIAS");
        FamiliaResponse resposta = new FamiliaResponse(7, "Takara", new EnderecoResponse(
                8, "Rua das Flores", null, "São Paulo", "10", "SP", "01504-001", null));
        when(familiaService.cadastrarComEndereco(any())).thenReturn(resposta);
        when(familiaService.listar()).thenReturn(List.of(resposta));
        when(familiaService.buscarPorId(7)).thenReturn(resposta);
        String token = tokenService.gerarToken(1, false).accesToken();
        mvc.perform(post("/familias/com-endereco").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Takara\",\"endereco\":{\"rua\":\"Rua das Flores\"}}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.endereco.id").value(8));
        mvc.perform(get("/familias").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].nome").value("Takara"));
        mvc.perform(get("/familias/7").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.endereco.cidade").value("São Paulo"));
        mvc.perform(post("/enderecos").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"rua\":\"Rua das Flores\"}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(enderecoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"TELA_USUARIOS", "TELA_ENDERECOS"})
    void outrasPermissoesNaoAutorizamCadastroFamilia(String permissao) throws Exception {
        usuario.getFuncaoSistema().getFuncionalidades().clear();
        adicionarPermissao(usuario, permissao);
        String corpo = "{\"nome\":\"Takara\",\"endereco\":{\"rua\":\"Rua das Flores\"}}";
        mvc.perform(post("/familias/com-endereco").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/familias/com-endereco").header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken())
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isForbidden());
        verifyNoInteractions(familiaService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"nome\":\"Takara\"}", "{\"nome\":\"  a  \",\"endereco\":{\"rua\":\"Rua A\"}}",
            "{\"nome\":\"Takara\",\"endereco\":{}}", "{\"nome\":\"Takara\",\"endereco\":{\"rua\":\"Rua A\",\"uf\":\"SSP\"}}",
            "{\"nome\":\"Takara\",\"endereco\":{\"rua\":\"Rua A\",\"cep\":\"123\"}}"})
    void cadastroFamiliaValidaNomeEEnderecoAninhado(String corpo) throws Exception {
        adicionarPermissao(usuario, "TELA_FAMILIAS");
        mvc.perform(post("/familias/com-endereco").header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken())
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(familiaService);
    }

    @Test
    void minhaContaSemPermissoesConsultaSomentePropriosDadosSemSenha() throws Exception {
        usuario.getFuncaoSistema().getFuncionalidades().clear();
        String token = tokenService.gerarToken(1, false).accesToken();
        mvc.perform(get("/minha-conta")).andExpect(status().isUnauthorized());
        mvc.perform(get("/minha-conta").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value(usuario.getNome()))
                .andExpect(jsonPath("$.funcaoSistema.id").value(2))
                .andExpect(jsonPath("$.senha").doesNotExist()).andExpect(jsonPath("$.senhaAtual").doesNotExist());
        mvc.perform(get("/usuarios").header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
    }

    @Test
    void atualizacaoPropriaIgnoraIdsFuncaoESituacaoEnviadosNoCorpo() throws Exception {
        usuario.getFuncaoSistema().getFuncionalidades().clear();
        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
        mvc.perform(put("/minha-conta").header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":999,"nome":"  Nome atualizado  ","email":" TESTE@EXAMPLE.COM ","telefone":"11999999999",
                                 "idFuncaoSistema":999,"ativo":false,"idFamilia":999}
                                """))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Nome atualizado"))
                .andExpect(jsonPath("$.email").value("teste@example.com"))
                .andExpect(jsonPath("$.funcaoSistema.id").value(2));
        org.junit.jupiter.api.Assertions.assertTrue(usuario.isAtivo());
        org.junit.jupiter.api.Assertions.assertNull(usuario.getFamilia());
        verify(usuarioRepository, never()).buscarComPermissoes(999);
        verify(usuarioRepository, never()).findById(999);
    }

    @Test
    void emailNovoExigeSenhaAtualEConflitoNaoModificaConta() throws Exception {
        String token = tokenService.gerarToken(1, false).accesToken();
        mvc.perform(put("/minha-conta").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"João Silva\",\"email\":\"novo@example.com\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigo").value("SENHA_ATUAL_INCORRETA"));
        when(usuarioRepository.existsByEmailIgnoreCaseAndIdNot("novo@example.com", 1)).thenReturn(true);
        mvc.perform(put("/minha-conta").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"João Silva\",\"email\":\"novo@example.com\",\"senhaAtual\":\"SenhaDeTeste123!\"}"))
                .andExpect(status().isConflict());
        org.junit.jupiter.api.Assertions.assertEquals("teste@example.com", usuario.getEmail());
        verify(usuarioRepository, never()).saveAndFlush(any());
    }

    @Test
    void alterarEmailMantemJwtPorIdEAtualizaContextoELogin() throws Exception {
        usuario.getFuncaoSistema().getFuncionalidades().clear();
        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
        String token = tokenService.gerarToken(1, false).accesToken();
        mvc.perform(put("/minha-conta").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Nome Novo\",\"email\":\" NOVO@example.com \",\"senhaAtual\":\"SenhaDeTeste123!\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("novo@example.com"));
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.usuario.nome").value("Nome Novo"))
                .andExpect(jsonPath("$.usuario.email").value("novo@example.com"));
        when(usuarioRepository.findByEmail("teste@example.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("novo@example.com")).thenReturn(Optional.of(usuario));
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"novo@example.com\",\"senha\":\"SenhaDeTeste123!\"}"))
                .andExpect(status().isOk());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"teste@example.com\",\"senha\":\"SenhaDeTeste123!\"}"))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {"errada", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"})
    void senhaAtualIncorretaNaoEncerraSessaoNemAlteraHash(String senhaIncorreta) throws Exception {
        String hash = usuario.getSenha();
        String token = tokenService.gerarToken(1, false).accesToken();
        mvc.perform(put("/minha-conta/senha").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senhaAtual\":\"" + senhaIncorreta + "\",\"novaSenha\":\"OutraSenha123!\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigo").value("SENHA_ATUAL_INCORRETA"));
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
        org.junit.jupiter.api.Assertions.assertEquals(hash, usuario.getSenha());
        verify(usuarioRepository, never()).saveAndFlush(any());
    }

    @Test
    void primeiroAcessoRestringeRotasEExigeNovoLoginAposTroca() throws Exception {
        prepararUsuario(true);
        adicionarPermissao(usuario, "TELA_FINANCEIRO");
        adicionarPermissao(usuario, "TELA_FUNCOES_SISTEMA");
        usuario.setSenha(passwordEncoder.encode("ABC123"));
        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
        String loginInicial = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"teste@example.com\",\"senha\":\"ABC123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primeiroAcesso").value(true))
                .andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(loginInicial, "$.accesToken");
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primeiroAcesso").value(true))
                .andExpect(jsonPath("$.permissoes", hasSize(0)));
        for (String caminho : List.of("/usuarios", "/funcoes-sistema", "/funcionalidades-sistema",
                "/api/financeiro/dashboard", "/minha-conta")) {
            mvc.perform(get(caminho).header("Authorization", "Bearer " + token))
                    .andExpect(status().isForbidden());
        }
        mvc.perform(put("/minha-conta").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken()))
                .andExpect(status().isUnauthorized());
        mvc.perform(put("/minha-conta/senha").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senhaAtual\":\"errada\",\"novaSenha\":\"SenhaNova123!\"}"))
                .andExpect(status().isBadRequest());
        assertTrue(usuario.isPrimeiroAcesso());
        verify(usuarioRepository, never()).saveAndFlush(any());

        mvc.perform(put("/minha-conta/senha").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senhaAtual\":\"ABC123\",\"novaSenha\":\"SenhaNova123!\"}"))
                .andExpect(status().isNoContent()).andExpect(content().string(""));
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches("SenhaNova123!", usuario.getSenha()));
        org.junit.jupiter.api.Assertions.assertFalse(passwordEncoder.matches("ABC123", usuario.getSenha()));
        assertFalse(usuario.isPrimeiroAcesso());
        String loginNormal = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"teste@example.com\",\"senha\":\"SenhaNova123!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primeiroAcesso").value(false))
                .andReturn().getResponse().getContentAsString();
        String tokenNormal = JsonPath.read(loginNormal, "$.accesToken");
        mvc.perform(get("/usuarios").header("Authorization", "Bearer " + tokenNormal))
                .andExpect(status().isOk());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"teste@example.com\",\"senha\":\"ABC123\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token)).andExpect(status().isUnauthorized());
        mvc.perform(put("/minha-conta/senha").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senhaAtual\":\"SenhaNova123!\",\"novaSenha\":\"OutraSenha123!\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void novaSenhaIgualOuMaiorQueLimiteUtf8NaoEGravada() throws Exception {
        String token = tokenService.gerarToken(1, false).accesToken();
        mvc.perform(put("/minha-conta/senha").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senhaAtual\":\"SenhaDeTeste123!\",\"novaSenha\":\"SenhaDeTeste123!\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigo").value("NOVA_SENHA_IGUAL"));
        mvc.perform(put("/minha-conta/senha").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senhaAtual\":\"SenhaDeTeste123!\",\"novaSenha\":\"" + "á".repeat(37) + "\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigo").value("NOVA_SENHA_LONGA"));
        verify(usuarioRepository, never()).saveAndFlush(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"nome\":\"A\",\"email\":\"teste@example.com\"}", "{\"nome\":\"Nome válido\",\"email\":\"invalido\"}"})
    void minhaContaValidaDadosPessoais(String corpo) throws Exception {
        mvc.perform(put("/minha-conta").header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken())
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest());
        verify(usuarioRepository, never()).saveAndFlush(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"senhaAtual\":\"ABC123\",\"novaSenha\":\"curta\"}"})
    void minhaContaValidaDadosDaSenha(String corpo) throws Exception {
        mvc.perform(put("/minha-conta/senha").header("Authorization", "Bearer " + tokenService.gerarToken(1, false).accesToken())
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest());
        verify(usuarioRepository, never()).saveAndFlush(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/minha-conta", "/minha-conta/senha"})
    void atualizarMinhaContaExigeJwt(String caminho) throws Exception {
        mvc.perform(put(caminho).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }

    private void adicionarPermissao(Usuario usuario, String codigo) {
        FuncionalidadeSistema permissao = new FuncionalidadeSistema();
        permissao.setCodigo(codigo);
        usuario.getFuncaoSistema().getFuncionalidades().add(permissao);
    }

    @Test
    void exclusaoInativaUsuarioSemApagarERevogaAcesso() throws Exception {
        Usuario alvo = new Usuario();
        alvo.concluirPrimeiroAcesso();
        alvo.setId(5);
        alvo.setNome("Maria");
        alvo.setEmail("maria@example.com");
        alvo.setAtivo(true);
        alvo.setFuncaoSistema(usuario.getFuncaoSistema());
        when(usuarioRepository.findById(5)).thenReturn(Optional.of(alvo));
        when(usuarioRepository.buscarComPermissoes(5)).thenReturn(Optional.of(alvo));
        UsuarioService servico = new UsuarioService(usuarioRepository, mock(FamiliaRepository.class),
                mock(FuncaoSistemaRepository.class), passwordEncoder, mock(EmailService.class));
        doAnswer(invocacao -> { servico.excluir(invocacao.getArgument(0), invocacao.getArgument(1)); return null; })
                .when(usuarioService).excluir(any(), any());
        String token = tokenService.gerarToken(1, false).accesToken();

        mvc.perform(delete("/usuarios/5").header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        assertFalse(alvo.isAtivo());
        verify(usuarioRepository).save(alvo);
        verify(usuarioRepository, never()).deleteById(any());
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + tokenService.gerarToken(5, false).accesToken()))
                .andExpect(status().isUnauthorized());

        // Repetir a exclusão é idempotente.
        mvc.perform(delete("/usuarios/5").header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        verify(usuarioRepository, times(1)).save(alvo);
    }

    @Test
    void exclusaoDaPropriaContaRetorna409EInexistenteRetorna404() throws Exception {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());
        UsuarioService servico = new UsuarioService(usuarioRepository, mock(FamiliaRepository.class),
                mock(FuncaoSistemaRepository.class), passwordEncoder, mock(EmailService.class));
        doAnswer(invocacao -> { servico.excluir(invocacao.getArgument(0), invocacao.getArgument(1)); return null; })
                .when(usuarioService).excluir(any(), any());
        String token = tokenService.gerarToken(1, false).accesToken();

        mvc.perform(delete("/usuarios/1").header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("Não é possível excluir a própria conta"));
        assertTrue(usuario.isAtivo());
        mvc.perform(delete("/usuarios/99").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
        verify(usuarioRepository, never()).save(any());
    }
}
