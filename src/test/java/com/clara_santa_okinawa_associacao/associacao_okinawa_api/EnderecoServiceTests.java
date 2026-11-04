package com.clara_santa_okinawa_associacao.associacao_okinawa_api;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Endereco;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.EnderecoRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.EnderecoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnderecoServiceTests {

    @Mock
    private EnderecoRepository enderecoRepository;

    @Test
    void cadastraEnderecoEDevolveRespostaComId() {
        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(invocation -> {
            Endereco endereco = invocation.getArgument(0);
            endereco.setId(42);
            return endereco;
        });

        var resposta = new EnderecoService(enderecoRepository).cadastrar(new EnderecoRequest(
                " Rua das Flores ", " Liberdade ", " São Paulo ", " 245 ", "sp", "01504-001", " Casa 2 "));

        ArgumentCaptor<Endereco> enderecoSalvo = ArgumentCaptor.forClass(Endereco.class);
        verify(enderecoRepository).save(enderecoSalvo.capture());
        assertEquals("Rua das Flores", enderecoSalvo.getValue().getRua());
        assertEquals("Liberdade", enderecoSalvo.getValue().getBairro());
        assertEquals("São Paulo", enderecoSalvo.getValue().getCidade());
        assertEquals("245", enderecoSalvo.getValue().getNumero());
        assertEquals("SP", enderecoSalvo.getValue().getUf());
        assertEquals("01504-001", enderecoSalvo.getValue().getCep());
        assertEquals("Casa 2", enderecoSalvo.getValue().getComplemento());
        assertEquals(42, resposta.id());
    }

    @Test
    void converteCamposOpcionaisEmNulo() {
        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var resposta = new EnderecoService(enderecoRepository).cadastrar(
                new EnderecoRequest("Rua das Flores", "", null, " ", "", "", null));

        assertEquals("Rua das Flores", resposta.rua());
        assertEquals(null, resposta.bairro());
        assertEquals(null, resposta.cidade());
        assertEquals(null, resposta.numero());
        assertEquals(null, resposta.uf());
        assertEquals(null, resposta.cep());
        assertEquals(null, resposta.complemento());
    }
}