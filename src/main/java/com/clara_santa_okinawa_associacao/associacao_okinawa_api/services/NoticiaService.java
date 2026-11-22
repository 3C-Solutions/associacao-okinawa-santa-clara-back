package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.noticia.NoticiaRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.noticia.NoticiaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Imagem;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Noticia;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Noticia.Status;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.RecursoNaoEncontradoException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.RegraNegocioException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FuncaoSistemaRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.ImagemConteudoRepository.Arquivo;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.NoticiaRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;

import static com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Noticia.TODOS;

@Service
@Transactional(readOnly = true)
public class NoticiaService {
    private final NoticiaRepository noticias;
    private final ImagemService imagens;
    private final FuncaoSistemaRepository funcoes;
    private final UsuarioRepository usuarios;

    public NoticiaService(NoticiaRepository noticias, ImagemService imagens,
                          FuncaoSistemaRepository funcoes, UsuarioRepository usuarios) {
        this.noticias = noticias;
        this.imagens = imagens;
        this.funcoes = funcoes;
        this.usuarios = usuarios;
    }

    public List<String> listarPublicos() {
        return Stream.concat(Stream.of(TODOS), funcoes.listarNomesParaNoticias().stream()).toList();
    }

    public NoticiaResponse.Pagina listar(Integer usuarioId, boolean gestor, Status status, String busca, Pageable pagina) {
        return NoticiaResponse.Pagina.de(noticias.listar(funcaoAtual(usuarioId), gestor, status != null,
                status == null ? Status.PUBLICADO : status, busca.trim(), pagina));
    }

    public NoticiaResponse consultar(Integer usuarioId, boolean gestor, Integer id) {
        return noticias.buscarVisivel(id, funcaoAtual(usuarioId), gestor)
                .map(NoticiaResponse::de).orElseThrow(this::naoEncontrada);
    }

    public Arquivo carregarCapa(Integer usuarioId, boolean gestor, Integer id) {
        return noticias.buscarCapa(id, funcaoAtual(usuarioId), gestor).orElseThrow(this::naoEncontrada);
    }

    @Transactional
    public Integer cadastrar(NoticiaRequest dados, MultipartFile arquivo) {
        if (dados.removerImagem()) throw new RegraNegocioException("A remoção de capa só é aceita na edição.");
        Noticia noticia = new Noticia(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
        aplicarDados(noticia, dados);
        if (arquivo != null) noticia.setImagem(imagens.cadastrar(arquivo));
        return noticias.save(noticia).getId();
    }

    @Transactional
    public void editar(Integer usuarioId, Integer id, NoticiaRequest dados, MultipartFile arquivo) {
        Noticia noticia = buscarParaAlterar(usuarioId, id);
        if (arquivo != null && dados.removerImagem()) {
            throw new RegraNegocioException("Escolha entre enviar uma nova capa e remover a capa.");
        }
        aplicarDados(noticia, dados);
        if (arquivo != null || dados.removerImagem()) {
            Imagem anterior = noticia.getImagem();
            noticia.setImagem(arquivo == null ? null : imagens.cadastrar(arquivo));
            excluirCapaOrfa(anterior);
        }
    }

    @Transactional
    public void alterarStatus(Integer usuarioId, Integer id, Status status) {
        buscarParaAlterar(usuarioId, id).setStatus(status);
    }

    @Transactional
    public void excluir(Integer usuarioId, Integer id) {
        Noticia noticia = buscarParaAlterar(usuarioId, id);
        Imagem capa = noticia.getImagem();
        noticias.delete(noticia);
        excluirCapaOrfa(capa);
    }

    private void excluirCapaOrfa(Imagem capa) {
        if (capa != null) {
            noticias.flush();
            imagens.excluirOrfa(capa);
        }
    }

    private void aplicarDados(Noticia noticia, NoticiaRequest dados) {
        String publico = dados.publico().trim();
        String canonico = TODOS.equalsIgnoreCase(publico) ? TODOS : funcoes.buscarNomeCanonico(publico)
                .orElseThrow(() -> new RegraNegocioException("Selecione Todos ou uma função cadastrada."));
        noticia.atualizar(dados.titulo().trim(), dados.classificacao().trim(),
                dados.conteudo().trim(), dados.status(), canonico);
    }

    private Noticia buscarParaAlterar(Integer usuarioId, Integer id) {
        return noticias.buscarParaAlterar(id, funcaoAtual(usuarioId)).orElseThrow(this::naoEncontrada);
    }

    private String funcaoAtual(Integer usuarioId) {
        return usuarios.buscarNomeFuncaoAtiva(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Conta sem acesso válido."));
    }

    private RecursoNaoEncontradoException naoEncontrada() {
        return new RecursoNaoEncontradoException("Notícia ou capa não encontrada.");
    }
}
