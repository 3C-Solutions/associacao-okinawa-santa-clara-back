package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final FamiliaRepository familiaRepository;
    private final ImagemRepository imagemRepository;

    public DepartamentoService(
            DepartamentoRepository departamentoRepository,
            MatriculaRepository matriculaRepository,
            UsuarioRepository usuarioRepository,
            FamiliaRepository familiaRepository,
            ImagemRepository imagemRepository
    ) {
        this.departamentoRepository = departamentoRepository;
        this.matriculaRepository = matriculaRepository;
        this.usuarioRepository = usuarioRepository;
        this.familiaRepository = familiaRepository;
        this.imagemRepository = imagemRepository;
    }

    @Transactional
    public Departamento cadastrar(Departamento departamento) {

        validarNome(departamento.getNome(), null);
        validarDiaHorario(departamento, null);
        validarImagem(departamento);
        validarResponsavelEFamilia(departamento);
        validarQuantidadeVagas(departamento.getQuantidadeVagas());

        Imagem imagemSalva = imagemRepository.save(
                departamento.getImagem()
        );

        departamento.setImagem(imagemSalva);

        return departamentoRepository.save(departamento);
    }

    @Transactional
    public Departamento editar(Integer id, Departamento atualizado) {

        Departamento departamento = buscarPorId(id);

        validarNome(atualizado.getNome(), id);
        validarDiaHorario(atualizado, id);
        validarImagem(atualizado);
        validarResponsavelEFamilia(atualizado);
        validarQuantidadeVagas(atualizado.getQuantidadeVagas());

        departamento.setNome(atualizado.getNome());
        departamento.setDescricao(atualizado.getDescricao());
        departamento.setDiaSemana(atualizado.getDiaSemana());
        departamento.setHorario(atualizado.getHorario());
        departamento.setProfessor(atualizado.getProfessor());
        departamento.setEscola(atualizado.getEscola());
        departamento.setValorMensal(atualizado.getValorMensal());
        departamento.setQuantidadeVagas(atualizado.getQuantidadeVagas());
        departamento.setAtivo(atualizado.getAtivo());

        departamento.setResponsavelDepartamento(
                atualizado.getResponsavelDepartamento()
        );

        departamento.setFamilia(atualizado.getFamilia());

        // Atualiza a imagem já vinculada ao departamento.
        if (departamento.getImagem() != null) {

            departamento.getImagem().setUrl(
                    atualizado.getImagem().getUrl()
            );

        } else {

            Imagem imagemSalva = imagemRepository.save(
                    atualizado.getImagem()
            );

            departamento.setImagem(imagemSalva);
        }

        return departamentoRepository.save(departamento);
    }

    @Transactional(readOnly = true)
    public List<Departamento> listar() {
        return departamentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Departamento buscarPorId(Integer id) {

        return departamentoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Departamento não encontrado com o ID: " + id
                        )
                );
    }

    @Transactional
    public void excluir(Integer id) {

        Departamento departamento = buscarPorId(id);

        if (matriculaRepository.existsByDepartamento_Id(id)) {
            throw new RecursoEmUsoException(
                    "Não é possível excluir o departamento, pois existem matrículas vinculadas a ele."
            );
        }

        departamentoRepository.delete(departamento);
    }

    private void validarNome(String nome, Integer id) {

        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException(
                    "O nome do departamento é obrigatório."
            );
        }

        boolean existe;

        if (id == null) {
            existe = departamentoRepository.existsByNomeIgnoreCase(nome.trim());
        } else {
            existe = departamentoRepository
                    .existsByNomeIgnoreCaseAndIdNot(nome.trim(), id);
        }

        if (existe) {
            throw new RegraNegocioException(
                    "Já existe um departamento cadastrado com esse nome."
            );
        }
    }

    private void validarDiaHorario(
            Departamento departamento,
            Integer id
    ) {

        if (!Boolean.TRUE.equals(departamento.getAtivo())) {
            return;
        }

        boolean existe;

        if (id == null) {
            existe = departamentoRepository
                    .existsByDiaSemanaIgnoreCaseAndHorarioAndAtivoTrue(
                            departamento.getDiaSemana(),
                            departamento.getHorario()
                    );
        } else {
            existe = departamentoRepository
                    .existsByDiaSemanaIgnoreCaseAndHorarioAndAtivoTrueAndIdNot(
                            departamento.getDiaSemana(),
                            departamento.getHorario(),
                            id
                    );
        }

        if (existe) {
            throw new RegraNegocioException(
                    "Já existe um departamento ativo cadastrado nesse dia e horário."
            );
        }
    }

    private void validarImagem(Departamento departamento) {

        if (departamento.getImagem() == null
                || departamento.getImagem().getUrl() == null
                || departamento.getImagem().getUrl().isBlank()) {

            throw new RegraNegocioException(
                    "É obrigatório informar uma imagem para o departamento."
            );
        }
    }

    private void validarQuantidadeVagas(Integer quantidadeVagas) {

        if (quantidadeVagas == null || quantidadeVagas <= 0) {
            throw new RegraNegocioException(
                    "A quantidade de vagas deve ser maior que zero."
            );
        }
    }

    private void validarResponsavelEFamilia(Departamento departamento) {

        if (departamento.getResponsavelDepartamento() == null
                || departamento.getResponsavelDepartamento().getId() == null) {

            throw new RegraNegocioException(
                    "É obrigatório informar o responsável pelo departamento."
            );
        }

        Integer idResponsavel =
                departamento.getResponsavelDepartamento().getId();

        Usuario responsavel = usuarioRepository.findById(idResponsavel)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Responsável não encontrado com o ID: " + idResponsavel
                        )
                );

        if (departamento.getFamilia() == null
                || departamento.getFamilia().getId() == null) {

            throw new RegraNegocioException(
                    "É obrigatório informar a família do departamento."
            );
        }

        Integer idFamilia = departamento.getFamilia().getId();

        Familia familia = familiaRepository.findById(idFamilia)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Família não encontrada com o ID: " + idFamilia
                        )
                );

        departamento.setResponsavelDepartamento(responsavel);
        departamento.setFamilia(familia);
    }
}