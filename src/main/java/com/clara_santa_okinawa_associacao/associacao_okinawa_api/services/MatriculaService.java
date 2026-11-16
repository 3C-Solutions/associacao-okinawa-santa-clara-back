package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final FamiliaRepository familiaRepository;
    private final DepartamentoRepository departamentoRepository;

    public MatriculaService(
            MatriculaRepository matriculaRepository,
            UsuarioRepository usuarioRepository,
            FamiliaRepository familiaRepository,
            DepartamentoRepository departamentoRepository
    ) {
        this.matriculaRepository = matriculaRepository;
        this.usuarioRepository = usuarioRepository;
        this.familiaRepository = familiaRepository;
        this.departamentoRepository = departamentoRepository;
    }

    @Transactional
    public Matricula cadastrar(Matricula matricula) {

        validarRelacionamentos(matricula);

        validarVagas(
                matricula.getDepartamento(),
                null,
                matricula.getAtivo()
        );

        return matriculaRepository.save(matricula);
    }

    @Transactional
    public Matricula editar(Integer id, Matricula atualizado) {

        Matricula matricula = buscarPorId(id);

        validarRelacionamentos(atualizado);

        validarVagas(
                atualizado.getDepartamento(),
                id,
                atualizado.getAtivo()
        );

        matricula.setUsuario(atualizado.getUsuario());
        matricula.setFamilia(atualizado.getFamilia());
        matricula.setDepartamento(atualizado.getDepartamento());
        matricula.setDataInicioMatricula(
                atualizado.getDataInicioMatricula()
        );
        matricula.setAtivo(atualizado.getAtivo());

        return matriculaRepository.save(matricula);
    }

    @Transactional(readOnly = true)
    public List<Matricula> listar() {
        return matriculaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Matricula buscarPorId(Integer id) {

        return matriculaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Matrícula não encontrada com o ID: " + id
                        )
                );
    }

    @Transactional
    public void excluir(Integer id) {

        Matricula matricula = buscarPorId(id);

        matriculaRepository.delete(matricula);
    }

    private void validarVagas(
            Departamento departamento,
            Integer idMatricula,
            Boolean ativo
    ) {

        // Matrículas inativas não ocupam vagas.
        if (!Boolean.TRUE.equals(ativo)) {
            return;
        }

        if (!Boolean.TRUE.equals(departamento.getAtivo())) {
            throw new RegraNegocioException(
                    "Não é possível matricular um usuário em um departamento inativo."
            );
        }

        Integer quantidadeVagas = departamento.getQuantidadeVagas();

        if (quantidadeVagas == null || quantidadeVagas <= 0) {
            throw new RegraNegocioException(
                    "O departamento não possui vagas disponíveis."
            );
        }

        long matriculasAtivas;

        if (idMatricula == null) {

            matriculasAtivas = matriculaRepository
                    .countByDepartamento_IdAndAtivoTrue(
                            departamento.getId()
                    );

        } else {

            matriculasAtivas = matriculaRepository
                    .countByDepartamento_IdAndAtivoTrueAndIdNot(
                            departamento.getId(),
                            idMatricula
                    );
        }

        if (matriculasAtivas >= quantidadeVagas) {
            throw new RegraNegocioException(
                    "O departamento atingiu o limite máximo de vagas."
            );
        }
    }

    private void validarRelacionamentos(Matricula matricula) {

        if (matricula.getUsuario() == null
                || matricula.getUsuario().getId() == null) {

            throw new RegraNegocioException(
                    "É obrigatório informar o usuário da matrícula."
            );
        }

        Integer idUsuario = matricula.getUsuario().getId();

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado com o ID: " + idUsuario
                        )
                );

        if (matricula.getFamilia() == null
                || matricula.getFamilia().getId() == null) {

            throw new RegraNegocioException(
                    "É obrigatório informar a família da matrícula."
            );
        }

        Integer idFamilia = matricula.getFamilia().getId();

        Familia familia = familiaRepository.findById(idFamilia)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Família não encontrada com o ID: " + idFamilia
                        )
                );

        if (matricula.getDepartamento() == null
                || matricula.getDepartamento().getId() == null) {

            throw new RegraNegocioException(
                    "É obrigatório informar o departamento da matrícula."
            );
        }

        Integer idDepartamento = matricula.getDepartamento().getId();

        Departamento departamento = departamentoRepository
                .findById(idDepartamento)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Departamento não encontrado com o ID: " + idDepartamento
                        )
                );

        matricula.setUsuario(usuario);
        matricula.setFamilia(familia);
        matricula.setDepartamento(departamento);
    }
}