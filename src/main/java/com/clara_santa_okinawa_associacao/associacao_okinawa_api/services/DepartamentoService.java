package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final FamiliaRepository familiaRepository;
    private final ImagemService imagemService;

    public DepartamentoService(
            DepartamentoRepository departamentoRepository,
            MatriculaRepository matriculaRepository,
            UsuarioRepository usuarioRepository,
            FamiliaRepository familiaRepository,
            ImagemService imagemService
    ) {
        this.departamentoRepository = departamentoRepository;
        this.matriculaRepository = matriculaRepository;
        this.usuarioRepository = usuarioRepository;
        this.familiaRepository = familiaRepository;
        this.imagemService = imagemService;
    }

    @Transactional
    public Departamento cadastrar(Departamento departamento, MultipartFile imagem) {

        validarNome(departamento.getNome(), null);
        validarDiaHorario(departamento, null);
        validarResponsavelEFamilia(departamento);
        validarQuantidadeVagas(departamento.getQuantidadeVagas());

        // valida arquivo obrigatório, tipo e tamanho
        departamento.setImagem(imagemService.cadastrar(imagem));

        return departamentoRepository.save(departamento);
    }

    @Transactional
    public Departamento editar(Integer id, Departamento atualizado, MultipartFile imagem) {

        Departamento departamento = buscarPorId(id);

        validarNome(atualizado.getNome(), id);
        validarDiaHorario(atualizado, id);
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
        departamento.setResponsavelDepartamento(atualizado.getResponsavelDepartamento());
        departamento.setFamilia(atualizado.getFamilia());

        // Imagem é opcional na edição. Se não vier, mantém a atual.
        if (imagem != null && !imagem.isEmpty()) {
            if (departamento.getImagem() != null) {
                imagemService.editar(departamento.getImagem().getId(), imagem);
            } else {
                departamento.setImagem(imagemService.cadastrar(imagem));
            }
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
        Imagem imagem = departamento.getImagem();
        departamentoRepository.delete(departamento);
        if (imagem != null) {
            imagemService.excluirOrfa(imagem);
        }
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

    @Transactional(readOnly = true)
    public List<Departamento> listarAtivos() {
        return departamentoRepository.findByAtivoTrueOrderByNomeAsc();
    }
}