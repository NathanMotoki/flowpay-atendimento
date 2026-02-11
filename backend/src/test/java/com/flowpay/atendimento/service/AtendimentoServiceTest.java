package com.flowpay.atendimento.service;

import com.flowpay.atendimento.exception.AtendimentoJaFinalizadoException;
import com.flowpay.atendimento.exception.ResourceNotFoundException;
import com.flowpay.atendimento.model.dto.request.NovoAtendimentoRequest;
import com.flowpay.atendimento.model.dto.response.AtendimentoResponse;
import com.flowpay.atendimento.model.entity.Atendente;
import com.flowpay.atendimento.model.entity.Atendimento;
import com.flowpay.atendimento.model.enums.StatusAtendimento;
import com.flowpay.atendimento.model.enums.TipoTime;
import com.flowpay.atendimento.repository.AtendimentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AtendimentoService - Testes com Mockito")
class AtendimentoServiceTest {

    @Mock
    private AtendimentoRepository atendimentoRepository;

    @Mock
    private AtendenteService atendenteService;

    @Mock
    private FilaService filaService;

    @InjectMocks
    private AtendimentoService atendimentoService;

    @Test
    @DisplayName("Deve criar atendimento e distribuir quando há atendente disponível")
    void deveCriarEDistribuirQuandoHaAtendente() {
        NovoAtendimentoRequest request = new NovoAtendimentoRequest(
                "João Silva",
                "Problemas com cartão bloqueado"
        );

        Atendente atendente = criarAtendente(1L, "Maria", TipoTime.CARTOES);

        Atendimento atendimentoCriado = Atendimento.builder()
                .id(1L)
                .clienteNome("João Silva")
                .assunto("Problemas com cartão bloqueado")
                .tipoTime(TipoTime.CARTOES)
                .status(StatusAtendimento.AGUARDANDO)
                .build();

        Atendimento atendimentoDistribuido = Atendimento.builder()
                .id(1L)
                .clienteNome("João Silva")
                .assunto("Problemas com cartão bloqueado")
                .tipoTime(TipoTime.CARTOES)
                .status(StatusAtendimento.EM_ATENDIMENTO)
                .atendente(atendente)
                .build();

        when(atendimentoRepository.save(any(Atendimento.class)))
                .thenReturn(atendimentoCriado)
                .thenReturn(atendimentoDistribuido);

        when(atendenteService.buscarAtendenteDisponivel(TipoTime.CARTOES))
                .thenReturn(Optional.of(atendente));

        when(atendimentoRepository.findById(1L))
                .thenReturn(Optional.of(atendimentoDistribuido));

        AtendimentoResponse response = atendimentoService.criarAtendimento(request);

        assertThat(response).isNotNull();
        assertThat(response.tipoTime()).isEqualTo(TipoTime.CARTOES);
        assertThat(response.status()).isEqualTo(StatusAtendimento.EM_ATENDIMENTO);
        assertThat(response.atendenteNome()).isEqualTo("Maria");

        verify(atendimentoRepository, times(2)).save(any(Atendimento.class));
        verify(atendenteService, times(1)).buscarAtendenteDisponivel(TipoTime.CARTOES);
        verify(filaService, never()).adicionarNaFila(any());
    }

    @Test
    @DisplayName("Deve enfileirar quando não há atendente disponível")
    void deveEnfileirarQuandoNaoHaAtendente() {
        NovoAtendimentoRequest request = new NovoAtendimentoRequest(
                "João Silva",
                "Problemas com cartão"
        );

        Atendimento atendimentoCriado = Atendimento.builder()
                .id(1L)
                .clienteNome("João Silva")
                .assunto("Problemas com cartão")
                .tipoTime(TipoTime.CARTOES)
                .status(StatusAtendimento.AGUARDANDO)
                .build();

        when(atendimentoRepository.save(any(Atendimento.class)))
                .thenReturn(atendimentoCriado);

        when(atendenteService.buscarAtendenteDisponivel(TipoTime.CARTOES))
                .thenReturn(Optional.empty());

        when(atendimentoRepository.findById(1L))
                .thenReturn(Optional.of(atendimentoCriado));

        AtendimentoResponse response = atendimentoService.criarAtendimento(request);

        assertThat(response.status()).isEqualTo(StatusAtendimento.AGUARDANDO);
        assertThat(response.atendenteId()).isNull();

        verify(filaService, times(1)).adicionarNaFila(any(Atendimento.class));
    }

    @Test
    @DisplayName("Deve listar todos os atendimentos")
    void deveListarTodosAtendimentos() {
        List<Atendimento> atendimentos = Arrays.asList(
                criarAtendimento(1L, StatusAtendimento.EM_ATENDIMENTO),
                criarAtendimento(2L, StatusAtendimento.AGUARDANDO)
        );

        when(atendimentoRepository.findAll()).thenReturn(atendimentos);

        List<AtendimentoResponse> responses = atendimentoService.listarTodos();

        assertThat(responses).hasSize(2);
        verify(atendimentoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar exceção ao finalizar atendimento inexistente")
    void deveLancarExcecaoAoFinalizarInexistente() {
        when(atendimentoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> atendimentoService.finalizarAtendimento(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Atendimento com ID 999 não encontrado");

        verify(atendimentoRepository, times(1)).findById(999L);
        verify(atendimentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao finalizar atendimento já finalizado")
    void deveLancarExcecaoAoFinalizarJaFinalizado() {
        Atendimento atendimento = Atendimento.builder()
                .id(1L)
                .status(StatusAtendimento.FINALIZADO)
                .build();

        when(atendimentoRepository.findById(1L))
                .thenReturn(Optional.of(atendimento));

        assertThatThrownBy(() -> atendimentoService.finalizarAtendimento(1L))
                .isInstanceOf(AtendimentoJaFinalizadoException.class)
                .hasMessageContaining("Atendimento 1 já foi finalizado");

        verify(atendimentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve finalizar atendimento com sucesso")
    void deveFinalizarAtendimentoComSucesso() {
        Atendente atendente = criarAtendente(1L, "Maria", TipoTime.CARTOES);

        Atendimento atendimento = Atendimento.builder()
                .id(1L)
                .status(StatusAtendimento.EM_ATENDIMENTO)
                .tipoTime(TipoTime.CARTOES)
                .atendente(atendente)
                .build();

        when(atendimentoRepository.findById(1L))
                .thenReturn(Optional.of(atendimento));

        when(filaService.filaVazia(TipoTime.CARTOES))
                .thenReturn(true);

        AtendimentoResponse response = atendimentoService.finalizarAtendimento(1L);

        assertThat(response.status()).isEqualTo(StatusAtendimento.FINALIZADO);

        verify(atendimentoRepository, times(1)).save(atendimento);
        verify(filaService, times(1)).filaVazia(TipoTime.CARTOES);
    }

    private Atendente criarAtendente(Long id, String nome, TipoTime tipoTime) {
        return Atendente.builder()
                .id(id)
                .nome(nome)
                .tipoTime(tipoTime)
                .build();
    }

    private Atendimento criarAtendimento(Long id, StatusAtendimento status) {
        return Atendimento.builder()
                .id(id)
                .clienteNome("Cliente " + id)
                .assunto("Assunto " + id)
                .tipoTime(TipoTime.CARTOES)
                .status(status)
                .build();
    }
}