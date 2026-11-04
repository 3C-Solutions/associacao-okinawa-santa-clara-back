package com.clara_santa_okinawa_associacao.associacao_okinawa_api;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaComEnderecoRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Endereco;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Familia;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.EnderecoNaoExisteException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.NotExistsByIdException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.EnderecoRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FamiliaRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.services.FamiliaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FamiliaServiceTests {
    @Mock private FamiliaRepository familias;
    @Mock private EnderecoRepository enderecos;

    @Test
    void listaVaziaRetornaArrayEConsultaInexistenteFalha() {
        when(familias.findAll(any(Sort.class))).thenReturn(List.of());
        when(familias.findById(99)).thenReturn(Optional.empty());
        var servico = new FamiliaService(enderecos, familias);
        assertEquals(List.of(), servico.listar());
        assertThrows(NotExistsByIdException.class, () -> servico.buscarPorId(99));
    }

    @Test
    void consultaRetornaNomeEEnderecoSemInventarVinculos() {
        var familia = new Familia();
        familia.setId(7);
        familia.setNome("Takara");
        var endereco = new Endereco();
        endereco.setId(8);
        endereco.setRua("Rua A");
        familia.setEndereco(endereco);
        when(familias.findById(7)).thenReturn(Optional.of(familia));
        when(familias.findAll(any(Sort.class))).thenReturn(List.of(familia));
        var servico = new FamiliaService(enderecos, familias);
        assertEquals("Rua A", servico.buscarPorId(7).endereco().rua());
        assertEquals(servico.buscarPorId(7), servico.listar().getFirst());
    }

    @Test
    void cadastroAntigoContinuaVinculandoEnderecoExistente() {
        Endereco endereco = new Endereco();
        endereco.setId(8);
        when(enderecos.findById(8)).thenReturn(Optional.of(endereco));
        when(familias.save(any(Familia.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
        var servico = new FamiliaService(enderecos, familias);
        assertEquals(8, servico.cadastrar(new FamiliaRequest("Takara", 8)).endereco().id());
        assertThrows(EnderecoNaoExisteException.class, () -> servico.cadastrar(new FamiliaRequest("Takara", 99)));
        verify(enderecos, never()).save(any());
    }

    @Test
    void cadastroConjuntoNormalizaDadosEConfirmaTransacao() {
        when(enderecos.save(any(Endereco.class))).thenAnswer(invocacao -> {
            Endereco endereco = invocacao.getArgument(0);
            endereco.setId(8);
            return endereco;
        });
        when(familias.saveAndFlush(any(Familia.class))).thenAnswer(invocacao -> {
            Familia familia = invocacao.getArgument(0);
            familia.setId(7);
            return familia;
        });
        var transacoes = mock(PlatformTransactionManager.class);
        var status = mock(TransactionStatus.class);
        when(transacoes.getTransaction(any())).thenReturn(status);
        var servico = comTransacao(transacoes);
        var resposta = servico.cadastrarComEndereco(dados());
        assertEquals(7, resposta.id());
        assertEquals("Takara", resposta.nome());
        assertEquals(8, resposta.endereco().id());
        assertEquals("Rua A", resposta.endereco().rua());
        assertEquals("SP", resposta.endereco().uf());
        assertNull(resposta.endereco().complemento());
        var ordem = inOrder(enderecos, familias, transacoes);
        ordem.verify(enderecos).save(any());
        ordem.verify(familias).saveAndFlush(any());
        ordem.verify(transacoes).commit(status);
        verify(transacoes, never()).rollback(any());
    }

    @Test
    void falhaAoSalvarFamiliaDisparaRollbackDaTransacaoConjunta() {
        when(enderecos.save(any())).thenAnswer(invocacao -> invocacao.getArgument(0));
        when(familias.saveAndFlush(any())).thenThrow(new IllegalStateException("Falha de persistência"));
        var transacoes = mock(PlatformTransactionManager.class);
        var status = mock(TransactionStatus.class);
        when(transacoes.getTransaction(any())).thenReturn(status);
        assertThrows(IllegalStateException.class, () -> comTransacao(transacoes).cadastrarComEndereco(dados()));
        verify(transacoes).rollback(status);
        verify(transacoes, never()).commit(any());
    }

    private FamiliaService comTransacao(PlatformTransactionManager transacoes) {
        var interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(transacoes);
        interceptor.setTransactionAttributeSource(new AnnotationTransactionAttributeSource());
        var proxy = new ProxyFactory(new FamiliaService(enderecos, familias));
        proxy.setProxyTargetClass(true);
        proxy.addAdvice(interceptor);
        return (FamiliaService) proxy.getProxy();
    }

    private FamiliaComEnderecoRequest dados() {
        return new FamiliaComEnderecoRequest(" Takara ", new EnderecoRequest(" Rua A ", " Bairro ",
                " São Paulo ", " 10 ", "sp", "01504-001", " "));
    }
}
