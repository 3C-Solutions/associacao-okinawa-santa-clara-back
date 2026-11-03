package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.DespesasCreateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.DespesasResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.DespesasUpdateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Despesas;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FinanceiroRegras;
public final class DespesasMapper {
    private DespesasMapper() {}
    public static Despesas toEntity(DespesasCreateRequest request) {
        Despesas despesa = new Despesas();
        despesa.setDtDepesa(request.dtDepesa());
        despesa.setDescricao(request.descricao().trim());
        despesa.setCategoria(request.categoria().trim());
        despesa.setValor(request.valor());
        despesa.setStatus(FinanceiroRegras.status(request.status()));
        despesa.setFkUsuario(request.fkUsuario());
        despesa.setFkFamilia(request.fkFamilia());
        return despesa;
    }
    public static void updateEntity(DespesasUpdateRequest request, Despesas despesa) {
        despesa.setDtDepesa(request.dtDepesa());
        despesa.setDescricao(request.descricao().trim());
        despesa.setCategoria(request.categoria().trim());
        despesa.setValor(request.valor());
        despesa.setStatus(FinanceiroRegras.status(request.status()));
        despesa.setFkUsuario(request.fkUsuario());
        despesa.setFkFamilia(request.fkFamilia());
    }
    public static DespesasResponse toResponse(Despesas despesa) {
        return new DespesasResponse(
                despesa.getIdDespesas(),
                despesa.getDtDepesa(),
                despesa.getDescricao(),
                despesa.getCategoria(),
                despesa.getValor(),
                despesa.getStatus(),
                despesa.getFkUsuario(),
                despesa.getFkFamilia()
        );
    }
}
