package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.departamento.DepartamentoPublicoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.departamento.DepartamentoRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.departamento.DepartamentoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Departamento;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Familia;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;

import java.util.List;

public class DepartamentoMapper {

    private DepartamentoMapper() {
    }

    public static Departamento toEntity(DepartamentoRequest dto) {

        Departamento departamento = new Departamento();

        departamento.setNome(dto.nome().trim());
        departamento.setDescricao(dto.descricao());
        departamento.setDiaSemana(dto.diaSemana());
        departamento.setHorario(dto.horario());
        departamento.setProfessor(dto.professor());
        departamento.setEscola(dto.escola());
        departamento.setValorMensal(dto.valorMensal());
        departamento.setQuantidadeVagas(dto.quantidadeVagas());
        departamento.setAtivo(dto.ativo());

        Usuario responsavel = new Usuario();
        responsavel.setId(dto.fkResponsavelDepartamento());
        departamento.setResponsavelDepartamento(responsavel);

        Familia familia = new Familia();
        familia.setId(dto.fkFamilia());
        departamento.setFamilia(familia);

        return departamento;
    }

    public static DepartamentoResponse toResponse(Departamento departamento) {

        if (departamento == null) {
            return null;
        }

        return new DepartamentoResponse(
                departamento.getId(),
                departamento.getNome(),
                departamento.getDescricao(),
                departamento.getDiaSemana(),
                departamento.getHorario(),
                departamento.getProfessor(),
                departamento.getEscola(),
                departamento.getValorMensal(),

                departamento.getResponsavelDepartamento() != null
                        ? departamento.getResponsavelDepartamento().getId() : null,

                departamento.getFamilia() != null
                        ? departamento.getFamilia().getId() : null,

                ImagemMapper.toResponse(departamento.getImagem()),

                departamento.getQuantidadeVagas(),
                departamento.getAtivo()
        );
    }

    public static List<DepartamentoResponse> toResponse(List<Departamento> departamentos) {

        if (departamentos == null || departamentos.isEmpty()) {
            return List.of();
        }

        return departamentos.stream()
                .map(DepartamentoMapper::toResponse)
                .toList();
    }

    public static DepartamentoPublicoResponse toPublicoResponse(Departamento departamento) {
        return new DepartamentoPublicoResponse(
                departamento.getId(),
                departamento.getNome(),
                departamento.getDescricao(),
                departamento.getDiaSemana(),
                departamento.getHorario(),
                departamento.getProfessor(),
                ImagemMapper.toResponse(departamento.getImagem())
        );
    }

    public static List<DepartamentoPublicoResponse> toPublicoResponse(List<Departamento> departamentos) {
        return departamentos.stream()
                .map(DepartamentoMapper::toPublicoResponse)
                .toList();
    }
}