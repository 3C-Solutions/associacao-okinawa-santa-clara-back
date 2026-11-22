package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Imagem;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.ImagemConteudo;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.RecursoEmUsoException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.RecursoNaoEncontradoException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.RegraNegocioException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.DepartamentoRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.ImagemConteudoRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.ImagemRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.NoticiaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Set;

@Service
public class ImagemService {

    private static final long TAMANHO_MAXIMO = 1024 * 1024;
    private static final Set<String> TIPOS_PERMITIDOS = Set.of("image/jpeg", "image/png", "image/webp");

    private final ImagemRepository imagemRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ImagemConteudoRepository imagemConteudoRepository;
    private final NoticiaRepository noticiaRepository;

    public ImagemService(
            ImagemRepository imagemRepository,
            DepartamentoRepository departamentoRepository,
            ImagemConteudoRepository imagemConteudoRepository,
            NoticiaRepository noticiaRepository
    ) {
        this.imagemRepository = imagemRepository;
        this.departamentoRepository = departamentoRepository;
        this.imagemConteudoRepository = imagemConteudoRepository;
        this.noticiaRepository = noticiaRepository;
    }


    @Transactional
    public Imagem cadastrar(MultipartFile arquivo) {
        Imagem imagem = new Imagem();
        byte[] conteudo = prepararArquivo(imagem, arquivo);
        imagem.setUrl("/imagens");
        Imagem salva = imagemRepository.save(imagem);
        salva.setUrl("/imagens/" + salva.getId() + "/arquivo");
        imagemConteudoRepository.save(new ImagemConteudo(salva, conteudo));
        return salva;
    }

    @Transactional
    public Imagem editar(Integer id, MultipartFile arquivo) {
        protegerCapa(id);
        Imagem imagem = buscarImagem(id);
        byte[] conteudo = prepararArquivo(imagem, arquivo);
        imagemConteudoRepository.findById(id).ifPresentOrElse(
                existente -> existente.setConteudo(conteudo),
                () -> imagemConteudoRepository.save(new ImagemConteudo(imagem, conteudo)));
        return imagem;
    }

    @Transactional(readOnly = true)
    public List<Imagem> listar() {
        return imagemRepository.listarSemCapasDeNoticias();
    }

    @Transactional(readOnly = true)
    public Imagem buscarPorId(Integer id) {
        return imagemRepository.buscarSemCapasDeNoticias(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Imagem não encontrada com o ID: " + id));
    }

    private Imagem buscarImagem(Integer id) {
        return imagemRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Imagem não encontrada com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public ImagemConteudoRepository.Arquivo carregar(Integer id) {
        return imagemConteudoRepository.buscar(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Arquivo da imagem não encontrado."));
    }

    @Transactional
    public void excluir(Integer id) {
        protegerCapa(id);
        Imagem imagem = buscarImagem(id);
        if (departamentoRepository.existsByImagem_Id(id)) {
            throw new RecursoEmUsoException(
                    "Não é possível excluir a imagem, pois ela está vinculada a um departamento.");
        }
        excluirRegistro(imagem);
    }

    @Transactional
    public void excluirOrfa(Imagem imagem) {
        Integer id = imagem.getId();
        if (!departamentoRepository.existsByImagem_Id(id) && !noticiaRepository.existsByImagem_Id(id)) {
            excluirRegistro(imagem);
        }
    }

    private void excluirRegistro(Imagem imagem) {
        imagemConteudoRepository.excluirConteudo(imagem.getId());
        imagemRepository.delete(imagem);
    }

    private void protegerCapa(Integer id) {
        if (noticiaRepository.existsByImagem_Id(id)) {
            throw new RecursoEmUsoException("A imagem está vinculada a uma notícia.");
        }
    }

    private byte[] prepararArquivo(Imagem imagem, MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraNegocioException("O arquivo da imagem é obrigatório.");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new RegraNegocioException("A imagem deve ter no máximo 1 MB.");
        }
        String tipo = arquivo.getContentType();
        if (tipo == null || !TIPOS_PERMITIDOS.contains(tipo)) {
            throw new RegraNegocioException("Formato inválido. Use PNG, JPG ou WebP.");
        }

        byte[] conteudo;
        try {
            conteudo = arquivo.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao ler a imagem enviada.", e);
        }

        imagem.setNomeArquivo(null);
        imagem.setNomeOriginal(arquivo.getOriginalFilename());
        imagem.setTipoConteudo(tipo);
        imagem.setTamanho((long) conteudo.length);
        return conteudo;
    }
}
