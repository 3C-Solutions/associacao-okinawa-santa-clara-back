package com.clara_santa_okinawa_associacao.associacao_okinawa_api;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.ImagemService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnabledIfEnvironmentVariable(named = "IMAGEM_INTEGRATION", matches = "true")
@SpringBootTest(properties = "app.bootstrap.enabled=false")
@AutoConfigureMockMvc
class ImagemIntegrationTests {

    @Autowired ImagemService service;
    @Autowired JdbcTemplate jdbc;
    @Autowired MockMvc mvc;
    @Autowired DataSource dataSource;
    @Autowired EntityManager entityManager;
    @Autowired PlatformTransactionManager transactionManager;

    @Test
    @Transactional
    void cadastraLeEditaERemoveConteudo() throws Exception {
        aplicarMigracao();
        byte[] original = {1, 2, 3};
        var imagem = service.cadastrar(new MockMultipartFile("arquivo", "original.png", "image/png", original));
        mvc.perform(get(imagem.getUrl())).andExpect(status().isOk())
                .andExpect(content().contentType("image/png")).andExpect(content().bytes(original));

        byte[] substituta = {4, 5, 6};
        var editada = service.editar(imagem.getId(),
                new MockMultipartFile("arquivo", "substituta.webp", "image/webp", substituta));
        assertEquals(imagem.getUrl(), editada.getUrl());
        mvc.perform(get(editada.getUrl())).andExpect(status().isOk())
                .andExpect(content().contentType("image/webp")).andExpect(content().bytes(substituta));

        service.excluir(imagem.getId());
        entityManager.flush();
        mvc.perform(get(imagem.getUrl())).andExpect(status().isNotFound());
        assertEquals(0, jdbc.queryForObject(
                "SELECT COUNT(*) FROM imagem_conteudo WHERE imagem_id = ?", Integer.class, imagem.getId()));
    }

    @Test
    @Transactional
    void substituiArquivoDeCadastroLegadoSemConteudo() throws Exception {
        aplicarMigracao();
        Integer id = jdbc.queryForObject("""
                INSERT INTO imagem (nome_arquivo, tipo_conteudo, url)
                VALUES ('antiga.png', 'image/png', '/imagens') RETURNING id
                """, Integer.class);
        String url = "/imagens/" + id + "/arquivo";
        jdbc.update("UPDATE imagem SET url = ? WHERE id = ?", url, id);
        mvc.perform(get(url)).andExpect(status().isNotFound());

        byte[] conteudo = {7, 8, 9};
        var imagem = service.editar(id, new MockMultipartFile("arquivo", "nova.png", "image/png", conteudo));
        assertEquals(url, imagem.getUrl());
        mvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().bytes(conteudo));
    }

    @Test
    void desfazCadastroDosMetadadosEDosBytesNaMesmaTransacao() {
        aplicarMigracao();
        Integer id = new TransactionTemplate(transactionManager).execute(transacao -> {
            var imagem = service.cadastrar(
                    new MockMultipartFile("arquivo", "teste.png", "image/png", new byte[]{1}));
            transacao.setRollbackOnly();
            return imagem.getId();
        });
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM imagem WHERE id = ?", Integer.class, id));
        assertEquals(0, jdbc.queryForObject(
                "SELECT COUNT(*) FROM imagem_conteudo WHERE imagem_id = ?", Integer.class, id));
    }

    private void aplicarMigracao() {
        new ResourceDatabasePopulator(
                new FileSystemResource("database/migrations/20261120_imagem_conteudo.sql")).execute(dataSource);
    }
}
