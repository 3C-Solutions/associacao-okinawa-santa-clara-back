package com.clara_santa_okinawa_associacao.associacao_okinawa_api;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.ImagemService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.NoticiaService;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.noticia.NoticiaRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Noticia.Status;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.clara_santa_okinawa_associacao.associacao_okinawa_api.components.FinalidadeToken.ACESSO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnabledIfEnvironmentVariable(named = "NOTICIAS_INTEGRATION", matches = "true")
@SpringBootTest(properties = "app.bootstrap.enabled=false")
@AutoConfigureMockMvc
@Transactional
class NoticiasIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired EntityManager entityManager;
    @Autowired ImagemService imagens;
    @Autowired NoticiaService noticias;
    @Autowired PlatformTransactionManager transacoes;

    private final String identificador = UUID.randomUUID().toString();
    private final String diretoria = "Diretoria " + identificador;
    private Integer idGestor;
    private Integer idLeitor;

    @BeforeEach
    void prepararContas() {
        idGestor = criarUsuario(diretoria, "gestor");
        idLeitor = criarUsuario("Associado " + identificador, "leitor");
    }

    @Test
    void capaRespeitaPublicoEStatusEApenasARotaDaNoticiaEntregaOArquivo() throws Exception {
        byte[] bytes = {1, 2, 3};
        String noticia = criar(dados("PUBLICADO", diretoria, false), imagem(bytes));
        String geral = criar(dados("PUBLICADO", "TODOS", false), null);
        String imagemGenerica = "/imagens/" + idImagem(noticia) + "/arquivo";
        var imagemPublica = imagens.cadastrar(imagem(new byte[]{4}));

        mvc.perform(get("/noticias").param("busca", identificador).with(conta(idLeitor, false)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalItens").value(1));
        mvc.perform(get(geral).with(conta(idLeitor, false))).andExpect(status().isOk())
                .andExpect(jsonPath("$.imagemUrl").isEmpty());
        mvc.perform(get(noticia).with(conta(idLeitor, false))).andExpect(status().isNotFound());
        mvc.perform(get(noticia + "/imagem").with(conta(idLeitor, true))).andExpect(status().isNotFound());
        mvc.perform(get(imagemGenerica)).andExpect(status().isNotFound());
        mvc.perform(get(imagemPublica.getUrl())).andExpect(status().isOk())
                .andExpect(content().bytes(new byte[]{4}));
        mvc.perform(get(noticia + "/imagem").with(conta(idGestor, false)))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().bytes(bytes));
        mvc.perform(patch(noticia + "/status").with(conta(idGestor, true)).contentType("application/json")
                        .content("{\"status\":\"RASCUNHO\"}")).andExpect(status().isNoContent());
        mvc.perform(get(noticia + "/imagem").with(conta(idGestor, false))).andExpect(status().isNotFound());
        mvc.perform(get(noticia + "/imagem").with(conta(idGestor, true))).andExpect(status().isOk());
    }

    @Test
    void trocaERemocaoLimpamAImagemAnterior() throws Exception {
        String noticia = criar(dados("RASCUNHO", "TODOS", false), imagem(new byte[]{1}));
        int anterior = idImagem(noticia);
        mvc.perform(multipart(HttpMethod.PUT, noticia).file(dados("RASCUNHO", "TODOS", true))
                        .file(imagem(new byte[]{2})).with(conta(idGestor, true)))
                .andExpect(status().isBadRequest());
        mvc.perform(multipart(HttpMethod.PUT, noticia).file(dados("RASCUNHO", "TODOS", false))
                        .file(imagem(new byte[]{3})).with(conta(idGestor, true)))
                .andExpect(status().isNoContent());
        int atual = idImagem(noticia);
        verificarExclusao(anterior);
        mvc.perform(get(noticia + "/imagem").with(conta(idGestor, true))).andExpect(content().bytes(new byte[]{3}));
        mvc.perform(multipart(HttpMethod.PUT, noticia).file(dados("RASCUNHO", "TODOS", true))
                        .with(conta(idGestor, true))).andExpect(status().isNoContent());
        mvc.perform(get(noticia + "/imagem").with(conta(idGestor, true))).andExpect(status().isNotFound());
        verificarExclusao(atual);
    }

    @Test
    void rollbackDesfazNoticiaMetadadosEBytes() {
        var transacao = new TransactionTemplate(transacoes);
        transacao.setPropagationBehaviorName("PROPAGATION_REQUIRES_NEW");
        var ids = transacao.execute(status -> {
            var dados = new NoticiaRequest("Comunicado", "Evento", "Texto da notícia", Status.RASCUNHO, "TODOS", false);
            int id = noticias.cadastrar(dados, imagem(new byte[]{1, 2}));
            entityManager.flush();
            int capa = idImagem("/noticias/" + id);
            status.setRollbackOnly();
            return new int[]{id, capa};
        });
        assertEquals(0, jdbc.queryForObject("select count(*) from noticia where id = ?", Integer.class, ids[0]));
        verificarExclusao(ids[1]);
    }

    private void verificarExclusao(int imagemId) {
        entityManager.flush();
        assertEquals(0, jdbc.queryForObject("select count(*) from imagem where id = ?", Integer.class, imagemId));
        assertEquals(0, jdbc.queryForObject("select count(*) from imagem_conteudo where imagem_id = ?", Integer.class, imagemId));
    }

    private String criar(MockMultipartFile dados, MockMultipartFile imagem) throws Exception {
        var requisicao = multipart("/noticias").file(dados);
        if (imagem != null) requisicao.file(imagem);
        return "/" + mvc.perform(requisicao.with(conta(idGestor, true))).andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }

    private Integer idImagem(String noticia) {
        int id = Integer.parseInt(noticia.substring(noticia.lastIndexOf('/') + 1));
        return jdbc.queryForObject("select fk_imagem from noticia where id = ?", Integer.class, id);
    }

    private Integer criarUsuario(String funcao, String nome) {
        Integer id = jdbc.queryForObject("insert into funcao_sistema (nome) values (?) returning id", Integer.class, funcao);
        return jdbc.queryForObject("""
                insert into usuario (nome, email, senha, ativo, primeiro_acesso, fk_funcao_sistema)
                values (?, ?, 'sem-login', true, false, ?) returning id
                """, Integer.class, nome, nome + identificador + "@example.com", id);
    }

    private MockMultipartFile dados(String status, String publico, boolean removerImagem) {
        String json = """
                {"titulo":"Comunicado %s","classificacao":"Evento","conteudo":"Texto da notícia",
                 "status":"%s","publico":"%s","removerImagem":%b}
                """.formatted(identificador, status, publico, removerImagem);
        return new MockMultipartFile("dados", "", "application/json", json.getBytes(StandardCharsets.UTF_8));
    }

    private MockMultipartFile imagem(byte[] bytes) {
        return new MockMultipartFile("imagem", "capa.png", "image/png", bytes);
    }

    private RequestPostProcessor conta(Integer id, boolean gestor) {
        var token = jwt().jwt(jwt -> jwt.subject(id.toString()));
        var acesso = new SimpleGrantedAuthority(ACESSO.autoridade());
        return gestor ? token.authorities(acesso, new SimpleGrantedAuthority("GERENCIAR_NOTICIAS"))
                : token.authorities(acesso);
    }
}
