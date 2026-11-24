
package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.evento.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.EventoMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.*;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Comparator;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final FamiliaRepository familiaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final PermissaoAgendaService permissaoAgendaService;
    private final GoogleCalendarService googleCalendarService;
    private final EntityManager entityManager;

    public EventoService(
            EventoRepository eventoRepository,
            FamiliaRepository familiaRepository,
            DepartamentoRepository departamentoRepository,
            PermissaoAgendaService permissaoAgendaService,
            GoogleCalendarService googleCalendarService,
            EntityManager entityManager
    ) {
        this.eventoRepository = eventoRepository;
        this.familiaRepository = familiaRepository;
        this.departamentoRepository = departamentoRepository;
        this.permissaoAgendaService = permissaoAgendaService;
        this.googleCalendarService = googleCalendarService;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public ReservantesAgendaResponse listarReservantes(
            Integer usuarioId
    ) {
        permissaoAgendaService.exigirDiretoria(
                permissaoAgendaService.usuario(usuarioId)
        );

        Comparator<ReservantesAgendaResponse.OpcaoReservante>
                porNome = Comparator
                .comparing(
                        ReservantesAgendaResponse.OpcaoReservante::nome,
                        String.CASE_INSENSITIVE_ORDER
                )
                .thenComparing(
                        ReservantesAgendaResponse.OpcaoReservante::id
                );

        var departamentos = departamentoRepository.findAll()
                .stream()
                .map(d ->
                        new ReservantesAgendaResponse.OpcaoReservante(
                                d.getId(),
                                d.getNomeDepartamento()
                        )
                )
                .sorted(porNome)
                .toList();

        var familias = familiaRepository.findAll()
                .stream()
                .map(f ->
                        new ReservantesAgendaResponse.OpcaoReservante(
                                f.getId(),
                                f.getNome()
                        )
                )
                .sorted(porNome)
                .toList();

        return new ReservantesAgendaResponse(
                departamentos,
                familias
        );
    }

    @Transactional(readOnly = true)
    public List<EventoResumoResponse> calendario(
            Integer usuarioId,
            LocalDate inicio,
            LocalDate fim
    ) {
        permissaoAgendaService.papel(
                permissaoAgendaService.usuario(usuarioId)
        );

        validarConsulta(inicio, fim);

        return eventoRepository
                .buscarPorPeriodo(inicio, fim)
                .stream()
                .filter(evento ->
                        "APROVADO".equals(evento.getStatus())
                )
                .map(evento ->
                        EventoMapper.toResumo(
                                evento,
                                reservadoPara(evento)
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> listar(
            Integer usuarioId,
            LocalDate inicio,
            LocalDate fim
    ) {
        permissaoAgendaService.exigirDiretoria(
                permissaoAgendaService.usuario(usuarioId)
        );

        validarConsulta(inicio, fim);

        return eventoRepository
                .buscarPorPeriodo(inicio, fim)
                .stream()
                .map(evento ->
                        EventoMapper.toResponse(
                                evento,
                                reservadoPara(evento)
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public EventoResponse buscar(
            Integer id,
            Integer usuarioId
    ) {
        permissaoAgendaService.exigirDiretoria(
                permissaoAgendaService.usuario(usuarioId)
        );

        Evento evento = buscarEvento(id);

        return EventoMapper.toResponse(
                evento,
                reservadoPara(evento)
        );
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> solicitacoes(
            Integer usuarioId
    ) {
        permissaoAgendaService.exigirDiretoria(
                permissaoAgendaService.usuario(usuarioId)
        );

        return eventoRepository.findAll()
                .stream()
                .filter(e ->
                        "SOLICITACAO".equals(
                                e.getOrigemCadastro()
                        )
                )
                .sorted(
                        Comparator.comparing(
                                Evento::getDtInicio
                        ).reversed()
                )
                .map(e ->
                        EventoMapper.toResponse(
                                e,
                                reservadoPara(e)
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> minhasSolicitacoes(
            Integer usuarioId
    ) {
        Usuario usuario =
                permissaoAgendaService.usuario(usuarioId);

        permissaoAgendaService.exigirSolicitante(usuario);

        return eventoRepository
                .findByFkUsuarioOrderByDtInicioDesc(usuarioId)
                .stream()
                .filter(e ->
                        "SOLICITACAO".equals(
                                e.getOrigemCadastro()
                        )
                )
                .map(e ->
                        EventoMapper.toResponse(
                                e,
                                reservadoPara(e)
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public EventoResponse minhaSolicitacao(
            Integer id,
            Integer usuarioId
    ) {
        permissaoAgendaService.exigirSolicitante(
                permissaoAgendaService.usuario(usuarioId)
        );

        Evento evento = buscarEvento(id);

        if (
                !"SOLICITACAO".equals(
                        evento.getOrigemCadastro()
                ) ||
                        !usuarioId.equals(evento.getFkUsuario())
        ) {
            throw new EventoNaoEncontradoException(
                    "Solicitação não encontrada"
            );
        }

        return EventoMapper.toResponse(
                evento,
                reservadoPara(evento)
        );
    }

    @Transactional
    public EventoResponse criar(
            EventoRequest request,
            Integer usuarioId
    ) {
        Usuario usuario =
                permissaoAgendaService.usuario(usuarioId);

        permissaoAgendaService.exigirDiretoria(usuario);

        validarPeriodo(request);
        bloquearAgenda();

        Evento evento = novoEvento(
                request,
                usuarioId,
                "DIRETO",
                "APROVADO"
        );

        atribuirBeneficiarioDiretoria(evento, request);
        validarConflitos(evento);

        evento.setOperacaoSincronizacao("CRIAR");
        evento.setStatusSincronizacao("PENDENTE");

        return resposta(
                eventoRepository.saveAndFlush(evento)
        );
    }

    @Transactional
    public EventoResponse solicitar(
            EventoRequest request,
            Integer usuarioId
    ) {
        Usuario usuario =
                permissaoAgendaService.usuario(usuarioId);

        permissaoAgendaService.exigirSolicitante(usuario);

        validarPeriodo(request);

        Evento evento = novoEvento(
                request,
                usuarioId,
                "SOLICITACAO",
                "PENDENTE"
        );

        atribuirBeneficiarioSolicitante(evento, usuario);

        evento.setStatusSincronizacao("NAO_APLICAVEL");

        return resposta(
                eventoRepository.saveAndFlush(evento)
        );
    }

    @Transactional
    public EventoResponse editar(
            Integer id,
            EventoRequest request,
            Integer usuarioId
    ) {
        permissaoAgendaService.exigirDiretoria(
                permissaoAgendaService.usuario(usuarioId)
        );

        validarPeriodo(request);
        bloquearAgenda();

        Evento evento = buscarEvento(id);

        if (!"APROVADO".equals(evento.getStatus())) {
            erroEstado();
        }

        EventoMapper.preencher(evento, request);

        atribuirBeneficiarioDiretoria(evento, request);
        validarConflitos(evento);

        evento.setOperacaoSincronizacao(
                evento.getGoogleEventId() == null
                        ? "CRIAR"
                        : "ATUALIZAR"
        );
        evento.setStatusSincronizacao("PENDENTE");

        return resposta(
                eventoRepository.saveAndFlush(evento)
        );
    }

    @Transactional
    public EventoResponse aprovar(
            Integer id,
            Integer usuarioId
    ) {
        permissaoAgendaService.exigirDiretoria(
                permissaoAgendaService.usuario(usuarioId)
        );

        bloquearAgenda();

        Evento evento = buscarEvento(id);

        exigirPendente(evento);
        validarConflitos(evento);

        evento.setStatus("APROVADO");
        evento.setFkUsuarioDecisao(usuarioId);
        evento.setDataDecisao(OffsetDateTime.now());
        evento.setOperacaoSincronizacao("CRIAR");
        evento.setStatusSincronizacao("PENDENTE");

        return resposta(
                eventoRepository.saveAndFlush(evento)
        );
    }

    @Transactional
    public EventoResponse recusar(
            Integer id,
            String motivoRecusa,
            Integer usuarioId
    ) {
        permissaoAgendaService.exigirDiretoria(
                permissaoAgendaService.usuario(usuarioId)
        );

        Evento evento = buscarEvento(id);

        exigirPendente(evento);

        if (
                motivoRecusa != null &&
                        motivoRecusa.length() > 500
        ) {
            throw new EventoDadosInvalidosException(
                    "Motivo muito longo"
            );
        }

        evento.setStatus("RECUSADO");
        evento.setMotivoRecusa(motivoRecusa);
        evento.setFkUsuarioDecisao(usuarioId);
        evento.setDataDecisao(OffsetDateTime.now());

        return resposta(
                eventoRepository.saveAndFlush(evento)
        );
    }

    @Transactional
    public EventoResponse cancelarSolicitacao(
            Integer id,
            Integer usuarioId
    ) {
        permissaoAgendaService.exigirSolicitante(
                permissaoAgendaService.usuario(usuarioId)
        );

        Evento evento = buscarEvento(id);

        if (!usuarioId.equals(evento.getFkUsuario())) {
            throw new EventoNaoEncontradoException(
                    "Solicitação não encontrada"
            );
        }

        exigirPendente(evento);

        evento.setStatus("CANCELADO");
        evento.setFkUsuarioCancelamento(usuarioId);
        evento.setDataCancelamento(OffsetDateTime.now());

        return resposta(
                eventoRepository.saveAndFlush(evento)
        );
    }

    @Transactional
    public EventoResponse cancelar(
            Integer id,
            Integer usuarioId
    ) {
        permissaoAgendaService.exigirDiretoria(
                permissaoAgendaService.usuario(usuarioId)
        );

        Evento evento = buscarEvento(id);

        if (!"APROVADO".equals(evento.getStatus())) {
            erroEstado();
        }

        evento.setStatus("CANCELADO");
        evento.setFkUsuarioCancelamento(usuarioId);
        evento.setDataCancelamento(OffsetDateTime.now());

        evento.setOperacaoSincronizacao(
                evento.getGoogleEventId() == null
                        ? null
                        : "EXCLUIR"
        );

        evento.setStatusSincronizacao(
                evento.getGoogleEventId() == null
                        ? "NAO_APLICAVEL"
                        : "PENDENTE"
        );

        return resposta(
                eventoRepository.saveAndFlush(evento)
        );
    }

    @Transactional
    public EventoResponse sincronizar(
            Integer id,
            Integer usuarioId
    ) {
        permissaoAgendaService.exigirDiretoria(
                permissaoAgendaService.usuario(usuarioId)
        );

        Evento evento = buscarEvento(id);

        if (
                "PENDENTE".equals(evento.getStatus()) ||
                        "RECUSADO".equals(evento.getStatus())
        ) {
            erroEstado();
        }

        if (
                !googleCalendarService.habilitado() ||
                        evento.getOperacaoSincronizacao() == null
        ) {
            return resposta(evento);
        }

        try {
            googleCalendarService.sincronizar(evento);

            evento.setStatusSincronizacao(
                    "SINCRONIZADO"
            );
            evento.setErroSincronizacao(null);

        } catch (RuntimeException ex) {
            evento.setStatusSincronizacao("ERRO");
            evento.setErroSincronizacao(
                    "Falha na integração Google Calendar; tente novamente"
            );
        }

        evento.setDataUltimaSincronizacao(
                OffsetDateTime.now()
        );

        return resposta(
                eventoRepository.saveAndFlush(evento)
        );
    }

    private Evento novoEvento(
            EventoRequest request,
            Integer usuarioId,
            String origem,
            String status
    ) {
        Evento evento = new Evento();

        EventoMapper.preencher(evento, request);

        evento.setFkUsuario(usuarioId);
        evento.setOrigemCadastro(origem);
        evento.setStatus(status);

        return evento;
    }

    private void atribuirBeneficiarioDiretoria(
            Evento evento,
            EventoRequest request
    ) {
        if (
                request.fkFamilia() != null &&
                        request.fkDepartamento() != null
        ) {
            throw new EventoDadosInvalidosException(
                    "Escolha família OU departamento"
            );
        }

        evento.setFkFamilia(null);
        evento.setFkDepartamento(null);
        evento.setTipoReservante("DIRETORIA");

        if (request.fkFamilia() != null) {
            if (
                    !familiaRepository.existsById(
                            request.fkFamilia()
                    )
            ) {
                throw new EventoNaoEncontradoException(
                        "Família inexistente"
                );
            }

            evento.setFkFamilia(request.fkFamilia());
            evento.setTipoReservante("FAMILIA");

        } else if (request.fkDepartamento() != null) {
            if (
                    !departamentoRepository.existsById(
                            request.fkDepartamento()
                    )
            ) {
                throw new EventoNaoEncontradoException(
                        "Departamento inexistente"
                );
            }

            evento.setFkDepartamento(
                    request.fkDepartamento()
            );
            evento.setTipoReservante("DEPARTAMENTO");
        }
    }

    private void atribuirBeneficiarioSolicitante(
            Evento evento,
            Usuario usuario
    ) {
        String papel =
                permissaoAgendaService.papel(usuario);

        if ("FAMILIA".equals(papel)) {
            if (
                    usuario.getFamilia() == null ||
                            usuario.getFamilia().getId() == null
            ) {
                throw new EventoConflitoException(
                        "Usuário sem família vinculada"
                );
            }

            evento.setFkFamilia(
                    usuario.getFamilia().getId()
            );
            evento.setTipoReservante("FAMILIA");

        } else {
            List<Departamento> departamentos =
                    departamentoRepository
                            .findByFkResponsavelDepartamento(
                                    usuario.getId()
                            );

            if (departamentos.size() != 1) {
                throw new EventoConflitoException(
                        "É necessário vincular exatamente um departamento responsável ao usuário"
                );
            }

            evento.setFkDepartamento(
                    departamentos.getFirst().getId()
            );
            evento.setTipoReservante("DEPARTAMENTO");
        }
    }

    private String reservadoPara(Evento evento) {
        if (
                "DIRETORIA".equals(
                        evento.getTipoReservante()
                )
        ) {
            return "Diretoria";
        }

        if (
                "FAMILIA".equals(
                        evento.getTipoReservante()
                )
        ) {
            return "Família " +
                    familiaRepository
                            .findById(evento.getFkFamilia())
                            .map(Familia::getNome)
                            .orElse("não identificada");
        }

        if (
                "DEPARTAMENTO".equals(
                        evento.getTipoReservante()
                )
        ) {
            return departamentoRepository
                    .findById(evento.getFkDepartamento())
                    .map(Departamento::getNomeDepartamento)
                    .orElse("Departamento não identificado");
        }

        return "Associação";
    }

    private void validarPeriodo(EventoRequest request) {
        if (request.dtInicio().isAfter(request.dtFim())) {
            throw new EventoDadosInvalidosException(
                    "Data final anterior à inicial"
            );
        }

        if (!Boolean.TRUE.equals(request.diaInteiro())) {
            if (
                    request.horaInicio() == null ||
                            request.horaFim() == null
            ) {
                throw new EventoDadosInvalidosException(
                        "Informe início e fim do horário"
                );
            }

            if (
                    !LocalDateTime.of(
                            request.dtInicio(),
                            request.horaInicio()
                    ).isBefore(
                            LocalDateTime.of(
                                    request.dtFim(),
                                    request.horaFim()
                            )
                    )
            ) {
                throw new EventoDadosInvalidosException(
                        "O término deve ser posterior ao início"
                );
            }
        } else if (
                request.horaInicio() != null ||
                        request.horaFim() != null
        ) {
            throw new EventoDadosInvalidosException(
                    "Dia inteiro não utiliza horários"
            );
        }
    }

    private void validarConsulta(
            LocalDate inicio,
            LocalDate fim
    ) {
        if (
                inicio == null ||
                        fim == null ||
                        fim.isBefore(inicio) ||
                        inicio.plusYears(2).isBefore(fim)
        ) {
            throw new EventoDadosInvalidosException(
                    "Período de consulta inválido"
            );
        }
    }

    private void bloquearAgenda() {
        entityManager.createNativeQuery(
                "SELECT pg_advisory_xact_lock(837112905)"
        ).getSingleResult();
    }

    private void validarConflitos(Evento candidato) {
        List<Evento> ocupados =
                eventoRepository
                        .findByStatusAndDtInicioLessThanEqualAndDtFimGreaterThanEqual(
                                "APROVADO",
                                candidato.getDtFim(),
                                candidato.getDtInicio()
                        );

        for (Evento existente : ocupados) {
            if (
                    candidato.getId() != null &&
                            candidato.getId().equals(existente.getId())
            ) {
                continue;
            }

            if (
                    candidato.getCodigoEspaco().conflitaCom(
                            existente.getCodigoEspaco()
                    ) &&
                            sobrepoe(candidato, existente)
            ) {
                String horario =
                        Boolean.TRUE.equals(
                                existente.getDiaInteiro()
                        )
                                ? "dia inteiro"
                                : existente.getHoraInicio() +
                                " às " +
                                existente.getHoraFim();

                throw new EventoConflitoException(
                        "Conflito com o evento confirmado #" +
                                existente.getId() +
                                " (" + existente.getTitulo() + ")" +
                                " no espaço " +
                                existente.getCodigoEspaco()
                                        .getDescricao() +
                                ", de " +
                                existente.getDtInicio() +
                                " a " +
                                existente.getDtFim() +
                                " (" + horario + ")."
                );
            }
        }
    }

    public static boolean sobrepoe(
            Evento primeiro,
            Evento segundo
    ) {
        LocalDateTime inicio1 =
                primeiro.getDtInicio().atStartOfDay();

        LocalDateTime fim1 =
                primeiro.getDtFim()
                        .plusDays(1)
                        .atStartOfDay();

        LocalDateTime inicio2 =
                segundo.getDtInicio().atStartOfDay();

        LocalDateTime fim2 =
                segundo.getDtFim()
                        .plusDays(1)
                        .atStartOfDay();

        if (
                !Boolean.TRUE.equals(
                        primeiro.getDiaInteiro()
                ) &&
                        primeiro.getHoraInicio() != null &&
                        primeiro.getHoraFim() != null
        ) {
            inicio1 = primeiro.getDtInicio()
                    .atTime(primeiro.getHoraInicio());

            fim1 = primeiro.getDtFim()
                    .atTime(primeiro.getHoraFim());
        }

        if (
                !Boolean.TRUE.equals(
                        segundo.getDiaInteiro()
                ) &&
                        segundo.getHoraInicio() != null &&
                        segundo.getHoraFim() != null
        ) {
            inicio2 = segundo.getDtInicio()
                    .atTime(segundo.getHoraInicio());

            fim2 = segundo.getDtFim()
                    .atTime(segundo.getHoraFim());
        }

        return inicio1.isBefore(fim2) &&
                inicio2.isBefore(fim1);
    }

    private Evento buscarEvento(Integer id) {
        return eventoRepository.findById(id)
                .orElseThrow(() ->
                        new EventoNaoEncontradoException(
                                "Evento não encontrado"
                        )
                );
    }

    private void exigirPendente(Evento evento) {
        if (
                !"SOLICITACAO".equals(
                        evento.getOrigemCadastro()
                ) ||
                        !"PENDENTE".equals(evento.getStatus())
        ) {
            erroEstado();
        }
    }

    private void erroEstado() {
        throw new EventoConflitoException(
                "Operação incompatível com o status atual"
        );
    }

    private EventoResponse resposta(Evento evento) {
        return EventoMapper.toResponse(
                evento,
                reservadoPara(evento)
        );
    }
}
