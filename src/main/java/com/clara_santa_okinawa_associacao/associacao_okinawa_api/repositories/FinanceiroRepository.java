package com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Cobranca;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.PagamentoCobranca;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Lancamento;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Repository
public class FinanceiroRepository {

    @PersistenceContext
    private EntityManager manager;

    private final JdbcTemplate jdbc;

    public FinanceiroRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private ResponseStatusException naoEncontrado(String descricao) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                descricao + " não encontrado(a)"
        );
    }

    // =========================
    // CONVERSÕES
    // =========================

    public static int numero(
            Map<String, Object> linha,
            String coluna) {

        return ((Number) linha.get(coluna)).intValue();
    }

    public static Integer numeroOpcional(
            Map<String, Object> linha,
            String coluna) {

        Object valor = linha.get(coluna);

        return valor == null
                ? null
                : ((Number) valor).intValue();
    }

    public static BigDecimal dinheiro(
            Map<String, Object> linha,
            String coluna) {

        Object valor = linha.get(coluna);

        return valor instanceof BigDecimal numero
                ? numero
                : new BigDecimal(String.valueOf(valor));
    }

    public static LocalDate data(
            Map<String, Object> linha,
            String coluna) {

        Object valor = linha.get(coluna);

        if (valor == null) {
            throw new IllegalStateException(
                    "Registro ID " + linha.getOrDefault(
                            "id_lancamento",
                            "não informado"
                    ) + " está sem data na coluna " + coluna
            );
        }

        if (valor instanceof LocalDate data) {
            return data;
        }

        if (valor instanceof java.sql.Date dataSql) {
            return dataSql.toLocalDate();
        }

        if (valor instanceof java.sql.Timestamp timestamp) {
            return timestamp.toLocalDateTime().toLocalDate();
        }

        if (valor instanceof java.time.LocalDateTime dataHora) {
            return dataHora.toLocalDate();
        }

        if (valor instanceof String texto) {
            try {
                return LocalDate.parse(texto.trim());
            } catch (java.time.format.DateTimeParseException e) {
                throw new IllegalArgumentException(
                        "Formato de data inválido na coluna "
                                + coluna + ": " + texto,
                        e
                );
            }
        }

        throw new IllegalArgumentException(
                "Tipo de data não reconhecido na coluna "
                        + coluna + ": "
                        + valor.getClass().getName()
        );
    }

    // =========================
    // MAPEAMENTO DE COBRANÇAS
    // =========================

    private Map<String, Object> mapa(Cobranca entidade) {

        Map<String, Object> resultado = new LinkedHashMap<>();

        resultado.put(
                "id_cobranca",
                entidade.getIdCobranca()
        );

        resultado.put(
                "nome_da_cobranca",
                entidade.getNomeDaCobranca()
        );

        resultado.put(
                "periodo",
                entidade.getPeriodo()
        );

        resultado.put(
                "valor",
                entidade.getValor() == null
                        ? null
                        : BigDecimal.valueOf(entidade.getValor())
        );

        resultado.put(
                "vencimento",
                entidade.getVencimento()
        );

        return resultado;
    }

    // =========================
    // MAPEAMENTO DE PAGAMENTOS
    // =========================

    private Map<String, Object> mapa(PagamentoCobranca entidade) {

        Map<String, Object> resultado = new LinkedHashMap<>();

        resultado.put(
                "id_pagamento_cobranca",
                entidade.getIdPagamentoCobranca()
        );

        resultado.put(
                "fk_usuario",
                entidade.getFkUsuario()
        );

        resultado.put(
                "fk_familia",
                entidade.getFkFamilia()
        );

        resultado.put(
                "id_cobranca",
                entidade.getCobrancaIdCobranca()
        );

        resultado.put(
                "status",
                entidade.getStatus()
        );

        resultado.put(
                "forma_pagamento",
                entidade.getFormaPagamento()
        );

        return resultado;
    }

    // =========================
    // MAPEAMENTO DE LANÇAMENTOS
    // =========================

    private Map<String, Object> mapa(Lancamento entidade) {

        Map<String, Object> resultado = new LinkedHashMap<>();

        // O ID vem da coluna física "id".
        // A chave do Map mantém o contrato da API.
        resultado.put(
                "id_lancamento",
                entidade.getIdLancamento()
        );

        // A data vem da coluna física "data_lancamento".
        // A chave do Map mantém o contrato da API.
        resultado.put(
                "dt_lancamento",
                entidade.getDtLancamento()
        );

        resultado.put(
                "tipo_lancamento",
                entidade.getTipoLancamento()
        );

        resultado.put(
                "descricao",
                entidade.getDescricao()
        );

        resultado.put(
                "categoria",
                entidade.getCategoria()
        );

        resultado.put(
                "valor",
                entidade.getValor()
        );

        resultado.put(
                "status",
                entidade.getStatus()
        );

        resultado.put(
                "fk_usuario",
                entidade.getFkUsuario()
        );

        resultado.put(
                "fk_familia",
                entidade.getFkFamilia()
        );

        return resultado;
    }

    // =========================
    // LISTAGENS
    // =========================

    public List<Map<String, Object>> cobrancas() {

        return manager.createQuery(
                        "SELECT c FROM Cobranca c",
                        Cobranca.class
                )
                .getResultList()
                .stream()
                .map(this::mapa)
                .toList();
    }

    public List<Map<String, Object>> pagamentos() {

        return manager.createQuery(
                        "SELECT p FROM PagamentoCobranca p",
                        PagamentoCobranca.class
                )
                .getResultList()
                .stream()
                .map(this::mapa)
                .toList();
    }

    public List<Map<String, Object>> lancamentos() {

        return manager.createQuery(
                        "SELECT l FROM Lancamento l",
                        Lancamento.class
                )
                .getResultList()
                .stream()
                .map(this::mapa)
                .toList();
    }

    // =========================
    // BUSCAS POR ID
    // =========================

    private Cobranca buscarCobranca(int id) {

        Cobranca resultado = manager.find(
                Cobranca.class,
                id
        );

        if (resultado == null) {
            throw naoEncontrado("Cobrança");
        }

        return resultado;
    }

    private PagamentoCobranca buscarPagamento(int id) {

        PagamentoCobranca resultado = manager.find(
                PagamentoCobranca.class,
                id
        );

        if (resultado == null) {
            throw naoEncontrado("Pagamento");
        }

        return resultado;
    }

    private Lancamento buscarLancamento(int id) {

        // Usa a chave primária "id", mapeada na Entity.
        Lancamento resultado = manager.find(
                Lancamento.class,
                id
        );

        if (resultado == null) {
            throw naoEncontrado("Lançamento");
        }

        return resultado;
    }

    public Map<String, Object> cobranca(int id) {
        return mapa(buscarCobranca(id));
    }

    public Map<String, Object> pagamento(int id) {
        return mapa(buscarPagamento(id));
    }

    public Map<String, Object> lancamento(int id) {
        return mapa(buscarLancamento(id));
    }

    // =========================
    // CADASTRO DE COBRANÇAS
    // =========================

    public int criarCobranca(
            String nome,
            String periodo,
            BigDecimal valor,
            LocalDate vencimento) {

        Cobranca nova = new Cobranca();

        nova.setNomeDaCobranca(nome);
        nova.setPeriodo(periodo);
        nova.setValor(valor.doubleValue());
        nova.setVencimento(vencimento);

        manager.persist(nova);
        manager.flush();

        return nova.getIdCobranca();
    }

    // =========================
    // CADASTRO DE PAGAMENTOS
    // =========================

    public int criarPagamento(
            int usuario,
            Integer familia,
            int cobranca) {

        PagamentoCobranca novo = new PagamentoCobranca();

        novo.setFkUsuario(usuario);
        novo.setFkFamilia(familia);
        novo.setCobrancaIdCobranca(cobranca);

        novo.setStatus("Pendente");
        novo.setFormaPagamento("A definir");

        manager.persist(novo);
        manager.flush();

        return novo.getIdPagamentoCobranca();
    }

    // =========================
    // CADASTRO DE LANÇAMENTOS
    // =========================

    public int criarLancamento(
            LocalDate data,
            String tipo,
            String descricao,
            String categoria,
            BigDecimal valor,
            String status,
            int usuario,
            Integer familia) {

        Lancamento novo = new Lancamento();

        // O campo Java dtLancamento está mapeado
        // para a coluna data_lancamento no PostgreSQL.
        novo.setDtLancamento(data);

        novo.setTipoLancamento(tipo);
        novo.setDescricao(descricao);
        novo.setCategoria(categoria);
        novo.setValor(valor);
        novo.setStatus(status);
        novo.setFkUsuario(usuario);
        novo.setFkFamilia(familia);

        manager.persist(novo);
        manager.flush();

        // Retorna a chave primária física "id".
        return novo.getIdLancamento();
    }

    // =========================
    // EDIÇÃO DE LANÇAMENTOS
    // =========================

    public int atualizarLancamento(
            int id,
            LocalDate data,
            String tipo,
            String descricao,
            String categoria,
            BigDecimal valor,
            String status,
            Integer familia) {

        Lancamento atual = buscarLancamento(id);

        // Atualiza data_lancamento pela Entity.
        atual.setDtLancamento(data);

        atual.setTipoLancamento(tipo);
        atual.setDescricao(descricao);
        atual.setCategoria(categoria);
        atual.setValor(valor);
        atual.setStatus(status);
        atual.setFkFamilia(familia);

        manager.flush();

        return 1;
    }

    // =========================
    // CONFIRMAÇÃO DE LANÇAMENTOS
    // =========================

    public int atualizarStatusLancamento(
            int id,
            String status) {

        Lancamento atual = buscarLancamento(id);

        atual.setStatus(status);

        manager.flush();

        return 1;
    }

    // =========================
    // EXCLUSÃO DE LANÇAMENTOS
    // =========================

    public int excluirLancamento(int id) {

        Lancamento atual = buscarLancamento(id);

        manager.remove(atual);
        manager.flush();

        return 1;
    }

    // =========================
    // INFORMAÇÃO DE PAGAMENTO
    // =========================

    public int informar(
            int id,
            String metodo,
            String statusAnterior) {

        int alterados = manager.createQuery(
                        "UPDATE PagamentoCobranca p " +
                                "SET p.formaPagamento = :metodo, " +
                                "p.status = :novo " +
                                "WHERE p.idPagamentoCobranca = :id " +
                                "AND p.status = :anterior"
                )
                .setParameter("metodo", metodo)
                .setParameter("novo", "Em verificação")
                .setParameter("id", id)
                .setParameter("anterior", statusAnterior)
                .executeUpdate();

        manager.clear();

        return alterados;
    }

    // =========================
    // REVISÃO DE PAGAMENTO
    // =========================

    public int revisar(
            int id,
            String status,
            String anterior) {

        int alterados = manager.createQuery(
                        "UPDATE PagamentoCobranca p " +
                                "SET p.status = :novo " +
                                "WHERE p.idPagamentoCobranca = :id " +
                                "AND p.status = :anterior"
                )
                .setParameter("novo", status)
                .setParameter("id", id)
                .setParameter("anterior", anterior)
                .executeUpdate();

        manager.clear();

        return alterados;
    }

    // =========================
    // RESPONSÁVEIS DOS DEPARTAMENTOS
    // =========================

    public Set<Integer> responsaveisDepartamento() {

        return new LinkedHashSet<>(
                jdbc.query(
                        "SELECT DISTINCT fk_responsavel_departamento " +
                                "FROM departamento " +
                                "WHERE fk_responsavel_departamento IS NOT NULL",
                        (rs, numeroLinha) -> rs.getInt(1)
                )
        );
    }

    // =========================
    // BLOQUEIO DE EMISSÃO
    // =========================

    public void bloquearEmissao() {

        jdbc.execute(
                "SELECT pg_advisory_xact_lock(778812345)"
        );
    }
}
