package com.clara_santa_okinawa_associacao.associacao_okinawa_api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Opt-in: usa o banco configurado, mas todas as alteracoes sofrem rollback.
@EnabledIfEnvironmentVariable(named = "FINANCE_INTEGRATION", matches = "true")
@SpringBootTest(properties = {"spring.jpa.hibernate.ddl-auto=none", "app.bootstrap.enabled=false"})
@AutoConfigureMockMvc
@Transactional
class EntradasFinanceirasIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    private static final String URL = "/api/financeiro/entradas";
    private Integer usuarioId() {
        return jdbc.queryForObject("select min(id) from public.usuario", Integer.class);
    }
    private RequestPostProcessor autorizado() {
        return jwt().jwt(j -> j.subject(usuarioId().toString()))
                .authorities(new SimpleGrantedAuthority("TELA_FINANCEIRO"));
    }
    private String dados(String status, String valor) {
        return """
            {"data":"%s","descricao":"TESTE ENTRADA COM ROLLBACK",
             "categoria":"Doação","valor":%s,"status":"%s","fkFamilia":null}
            """.formatted(LocalDate.now(), valor, status);
    }
    private BigDecimal entradasAno() throws Exception {
        String json = mvc.perform(get("/api/financeiro/dashboard")
                .param("ano", String.valueOf(LocalDate.now().getYear()))
                .param("mes", String.valueOf(LocalDate.now().getMonthValue())).with(autorizado()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return new BigDecimal(JsonPath.read(json, "$.resumo.entradasAnuais").toString());
    }
    @Test
    void cicloCompletoSemFamiliaAtualizaSaldoEPreservaResponsavel() throws Exception {
        BigDecimal antes = entradasAno();
        String json = mvc.perform(post(URL).with(autorizado()).contentType("application/json")
                .content(dados("Pendente", "12.50")))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.fkFamilia").isEmpty())
                .andExpect(jsonPath("$.fkUsuario").value(usuarioId()))
                .andReturn().getResponse().getContentAsString();
        int id = JsonPath.read(json, "$.id");
        assertEquals(0, antes.compareTo(entradasAno()));
        mvc.perform(put(URL + "/" + id).with(autorizado()).contentType("application/json")
                .content(dados("Pendente", "20.25")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.fkUsuario").value(usuarioId()));
        mvc.perform(patch(URL + "/" + id + "/confirmar").with(autorizado()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("Recebido"));
        assertEquals(0, antes.add(new BigDecimal("20.25")).compareTo(entradasAno()));
        assertEquals(0, new BigDecimal("20.25").compareTo(jdbc.queryForObject(
                "select valor from public.entradas_financeiras where id=? and fk_familia is null", BigDecimal.class, id)));
        mvc.perform(patch(URL + "/" + id + "/confirmar").with(autorizado()))
                .andExpect(status().isConflict());
        mvc.perform(delete(URL + "/" + id).with(autorizado())).andExpect(status().isNoContent());
        assertEquals(0, antes.compareTo(entradasAno()));
        mvc.perform(put(URL + "/" + id).with(autorizado()).contentType("application/json")
                .content(dados("Recebido", "1.00"))).andExpect(status().isNotFound());
    }
    @Test
    void rejeitaValoresInvalidosEStatusDesconhecido() throws Exception {
        for (String valor : new String[]{"-1", "0", "1.001", "1000000000000"}) {
            mvc.perform(post(URL).with(autorizado()).contentType("application/json")
                    .content(dados("Recebido", valor))).andExpect(status().isBadRequest());
        }
        mvc.perform(post(URL).with(autorizado()).contentType("application/json")
                .content(dados("Pago", "1.00"))).andExpect(status().isBadRequest());
    }
    @Test
    void exigeAutenticacaoEPermissaoFinanceira() throws Exception {
        mvc.perform(get(URL)).andExpect(status().isUnauthorized());
        mvc.perform(post(URL).with(jwt().authorities(new SimpleGrantedAuthority("TELA_USUARIOS")))
                .contentType("application/json").content(dados("Recebido", "1.00")))
                .andExpect(status().isForbidden());
    }
    @Test
    void consultaFamiliasRespeitaContratoEPermissaoDoDevelop() throws Exception {
        mvc.perform(get("/familias")).andExpect(status().isUnauthorized());
        mvc.perform(get("/familias").with(autorizado())).andExpect(status().isForbidden());
        String json = mvc.perform(get("/familias").with(jwt()
                .authorities(new SimpleGrantedAuthority("TELA_FAMILIAS"))))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        java.util.List<Integer> ids = JsonPath.read(json, "$[*].id");
        assertEquals(jdbc.queryForList("select id from public.familia order by nome,id", Integer.class), ids);
        if (!ids.isEmpty()) {
            mvc.perform(get("/familias/" + ids.get(0)).with(jwt()
                    .authorities(new SimpleGrantedAuthority("TELA_FAMILIAS"))))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(ids.get(0)))
                    .andExpect(jsonPath("$.nome").isString());
        }
    }
    @Test
    void vinculaDespesaAOutraFamiliaSemAlterarResponsavel() throws Exception {
        Integer familia = jdbc.queryForObject("""
                select min(f.id) from public.familia f
                where f.id <> (select fk_familia from public.usuario where id=? limit 1)
                """, Integer.class, usuarioId());
        assertNotNull(familia, "O banco de integração deve ter outra família para validar o vínculo independente");
        String body = """
                {"dtDepesa":"%s","descricao":"TESTE SELETOR FAMILIA COM ROLLBACK",
                "categoria":"Outros","valor":0.01,"status":"Pendente","fkFamilia":%d}
                """.formatted(LocalDate.now(), familia);
        String json = mvc.perform(post("/api/financeiro/despesas").with(autorizado())
                .contentType("application/json").content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.fkFamilia").value(familia))
                .andExpect(jsonPath("$.fkUsuario").value(usuarioId()))
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(json, "$.idDespesas");
        assertEquals(familia, jdbc.queryForObject("select fk_familia from public.despesas where id=?", Integer.class, id));
        mvc.perform(delete("/api/financeiro/despesas/" + id).with(autorizado())).andExpect(status().isNoContent());
        String entrada = dados("Pendente", "1.00").replace("\"fkFamilia\":null", "\"fkFamilia\":" + familia);
        mvc.perform(post(URL).with(autorizado()).contentType("application/json").content(entrada))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.fkFamilia").value(familia));
    }
}
