package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Imagem;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ImagemService {

    private static final long TAMANHO_MAXIMO = 1024 * 1024;
    private static final Map<String, String> EXTENSOES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private final ImagemRepository imagemRepository;
    private final DepartamentoRepository departamentoRepository;
    private final Path pasta;

    public ImagemService(
            ImagemRepository imagemRepository,
            DepartamentoRepository departamentoRepository,
            @Value("${app.upload.dir:./uploads}") String dir
    ) {
        this.imagemRepository = imagemRepository;
        this.departamentoRepository = departamentoRepository;
        this.pasta = Paths.get(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.pasta);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível criar a pasta de uploads.", e);
        }
    }

    @Transactional
    public Imagem cadastrar(MultipartFile arquivo) {
        Imagem imagem = new Imagem();
        gravar(imagem, arquivo);
        return imagemRepository.save(imagem);
    }

    @Transactional
    public Imagem editar(Integer id, MultipartFile arquivo) {
        Imagem imagem = buscarPorId(id);
        String arquivoAntigo = imagem.getNomeArquivo();
        gravar(imagem, arquivo);
        Imagem salva = imagemRepository.save(imagem);
        apagarArquivo(arquivoAntigo);
        return salva;
    }

    @Transactional(readOnly = true)
    public List<Imagem> listar() {
        return imagemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Imagem buscarPorId(Integer id) {
        return imagemRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Imagem não encontrada com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public Resource carregar(Imagem imagem) {
        if (imagem.getNomeArquivo() == null) {
            throw new RecursoNaoEncontradoException("A imagem não possui arquivo.");
        }
        Path caminho = pasta.resolve(imagem.getNomeArquivo()).normalize();
        if (!caminho.startsWith(pasta) || !Files.exists(caminho)) {
            throw new RecursoNaoEncontradoException("Arquivo da imagem não encontrado.");
        }
        return new FileSystemResource(caminho);
    }

    @Transactional
    public void excluir(Integer id) {
        Imagem imagem = buscarPorId(id);
        if (departamentoRepository.existsByImagem_Id(id)) {
            throw new RecursoEmUsoException(
                    "Não é possível excluir a imagem, pois ela está vinculada a um departamento.");
        }
        imagemRepository.delete(imagem);
        apagarArquivo(imagem.getNomeArquivo());
    }

    // Usado quando o departamento dono da imagem acabou de ser excluído.
    @Transactional
    public void excluirOrfa(Imagem imagem) {
        imagemRepository.delete(imagem);
        apagarArquivo(imagem.getNomeArquivo());
    }

    private void gravar(Imagem imagem, MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraNegocioException("O arquivo da imagem é obrigatório.");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new RegraNegocioException("A imagem deve ter no máximo 1 MB.");
        }
        String extensao = EXTENSOES.get(arquivo.getContentType());
        if (extensao == null) {
            throw new RegraNegocioException("Formato inválido. Use PNG, JPG ou WebP.");
        }

        // O nome é gerado aqui. O nome enviado pelo cliente é ignorado (evita path traversal).
        String nomeArquivo = UUID.randomUUID() + extensao;

        try (InputStream entrada = arquivo.getInputStream()) {
            Files.copy(entrada, pasta.resolve(nomeArquivo));
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao salvar a imagem.", e);
        }

        imagem.setNomeArquivo(nomeArquivo);
        imagem.setNomeOriginal(arquivo.getOriginalFilename());
        imagem.setTipoConteudo(arquivo.getContentType());
        imagem.setTamanho(arquivo.getSize());
    }

    private void apagarArquivo(String nomeArquivo) {
        if (nomeArquivo == null) return;
        try {
            Files.deleteIfExists(pasta.resolve(nomeArquivo).normalize());
        } catch (IOException ignored) {
        }
    }
}