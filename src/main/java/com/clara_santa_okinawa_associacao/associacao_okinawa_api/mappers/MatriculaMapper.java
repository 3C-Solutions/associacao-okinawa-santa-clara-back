package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.matricula.MatriculaRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.matricula.MatriculaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.*;

import java.util.List;

public class MatriculaMapper {

    private MatriculaMapper() {
    }

    public static Matricula toEntity(MatriculaRequest dto) {

        Matricula matricula = new Matricula();

        matricula.setDataInicioMatricula(dto.dataInicioMatricula());
        matricula.setAtivo(dto.ativo());

        Usuario usuario = new Usuario();
        usuario.setId(dto.fkUsuario());
        matricula.setUsuario(usuario);

        Familia familia = new Familia();
        familia.setId(dto.fkFamilia());
        matricula.setFamilia(familia);

        Departamento departamento = new Departamento();
        departamento.setId(dto.fkDepartamento());
        matricula.setDepartamento(departamento);

        return matricula;
    }

    public static MatriculaResponse toResponse(Matricula matricula) {

        if (matricula == null) {
            return null;
        }

        return new MatriculaResponse(
                matricula.getId(),

                matricula.getUsuario() != null
                        ? matricula.getUsuario().getId() : null,

                matricula.getFamilia() != null
                        ? matricula.getFamilia().getId() : null,

                matricula.getDepartamento() != null
                        ? matricula.getDepartamento().getId() : null,

                matricula.getDataInicioMatricula(),
                matricula.getAtivo()
        );
    }

    public static List<MatriculaResponse> toResponse(List<Matricula> matriculas) {

        if (matriculas == null || matriculas.isEmpty()) {
            return List.of();
        }

        return matriculas.stream()
                .map(MatriculaMapper::toResponse)
                .toList();
    }
}