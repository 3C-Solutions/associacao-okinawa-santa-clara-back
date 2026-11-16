package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Imagem;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImagemService {

    private final ImagemRepository imagemRepository;
    private final DepartamentoRepository departamentoRepository;

    public ImagemService(
            ImagemRepository imagemRepository,
            DepartamentoRepository departamentoRepository
    ) {
        this.imagemRepository = imagemRepository;
        this.departamentoRepository = departamentoRepository;
    }

    @Transactional
    public Imagem cadastrar(Imagem imagem) {

        validarUrl(imagem.getUrl());

        imagem.setUrl(imagem.getUrl().trim());

        return imagemRepository.save(imagem);
    }

    @Transactional
    public Imagem editar(Integer id, Imagem atualizada) {

        Imagem imagem = buscarPorId(id);

        validarUrl(atualizada.getUrl());

        imagem.setUrl(atualizada.getUrl().trim());

        return imagemRepository.save(imagem);
    }

    @Transactional(readOnly = true)
    public List<Imagem> listar() {
        return imagemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Imagem buscarPorId(Integer id) {

        return imagemRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Imagem não encontrada com o ID: " + id
                        )
                );
    }

    @Transactional
    public void excluir(Integer id) {

        Imagem imagem = buscarPorId(id);

        if (departamentoRepository.existsByImagem_Id(id)) {
            throw new RecursoEmUsoException(
                    "Não é possível excluir a imagem, pois ela está vinculada a um departamento."
            );
        }

        imagemRepository.delete(imagem);
    }

    private void validarUrl(String url) {

        if (url == null || url.isBlank()) {
            throw new RegraNegocioException(
                    "A URL da imagem é obrigatória."
            );
        }
    }
}