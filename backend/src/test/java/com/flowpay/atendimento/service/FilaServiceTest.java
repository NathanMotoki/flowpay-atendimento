package com.flowpay.atendimento.service;

import com.flowpay.atendimento.model.entity.Atendimento;
import com.flowpay.atendimento.model.enums.StatusAtendimento;
import com.flowpay.atendimento.model.enums.TipoTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FilaService - Testes de Gerenciamento de Filas")
class FilaServiceTest {

    private FilaService filaService;

    @BeforeEach
    void setUp() {
        filaService = new FilaService();
    }

    @Test
    @DisplayName("Deve adicionar atendimento na fila correta")
    void deveAdicionarNaFilaCorreta() {
        Atendimento atendimento = criarAtendimento(1L, TipoTime.CARTOES);

        filaService.adicionarNaFila(atendimento);

        assertThat(filaService.tamanhoFila(TipoTime.CARTOES)).isEqualTo(1);
        assertThat(filaService.tamanhoFila(TipoTime.EMPRESTIMOS)).isZero();
        assertThat(filaService.tamanhoFila(TipoTime.OUTROS_ASSUNTOS)).isZero();
    }

    @Test
    @DisplayName("Deve respeitar ordem FIFO (First In First Out)")
    void deveRespeitarOrdemFIFO() {
        Atendimento primeiro = criarAtendimento(1L, TipoTime.CARTOES);
        Atendimento segundo = criarAtendimento(2L, TipoTime.CARTOES);
        Atendimento terceiro = criarAtendimento(3L, TipoTime.CARTOES);

        filaService.adicionarNaFila(primeiro);
        filaService.adicionarNaFila(segundo);
        filaService.adicionarNaFila(terceiro);

        Optional<Atendimento> proximoA = filaService.proximoDaFila(TipoTime.CARTOES);
        Optional<Atendimento> proximoB = filaService.proximoDaFila(TipoTime.CARTOES);
        Optional<Atendimento> proximoC = filaService.proximoDaFila(TipoTime.CARTOES);

        assertThat(proximoA).isPresent();
        assertThat(proximoA.get().getId()).isEqualTo(1L);

        assertThat(proximoB).isPresent();
        assertThat(proximoB.get().getId()).isEqualTo(2L);

        assertThat(proximoC).isPresent();
        assertThat(proximoC.get().getId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("Deve retornar vazio quando fila está vazia")
    void deveRetornarVazioQuandoFilaVazia() {
        Optional<Atendimento> proximo = filaService.proximoDaFila(TipoTime.CARTOES);

        assertThat(proximo).isEmpty();
        assertThat(filaService.filaVazia(TipoTime.CARTOES)).isTrue();
    }

    @Test
    @DisplayName("Deve verificar corretamente se fila está vazia")
    void deveVerificarSeFilaVazia() {
        assertThat(filaService.filaVazia(TipoTime.CARTOES)).isTrue();

        filaService.adicionarNaFila(criarAtendimento(1L, TipoTime.CARTOES));

        assertThat(filaService.filaVazia(TipoTime.CARTOES)).isFalse();
    }

    @Test
    @DisplayName("Deve retornar tamanho correto de cada fila")
    void deveRetornarTamanhoCorreto() {
        filaService.adicionarNaFila(criarAtendimento(1L, TipoTime.CARTOES));
        filaService.adicionarNaFila(criarAtendimento(2L, TipoTime.CARTOES));
        filaService.adicionarNaFila(criarAtendimento(3L, TipoTime.EMPRESTIMOS));

        assertThat(filaService.tamanhoFila(TipoTime.CARTOES)).isEqualTo(2);
        assertThat(filaService.tamanhoFila(TipoTime.EMPRESTIMOS)).isEqualTo(1);
        assertThat(filaService.tamanhoFila(TipoTime.OUTROS_ASSUNTOS)).isZero();
    }

    @Test
    @DisplayName("Deve calcular total correto em todas as filas")
    void deveCalcularTotalNasFilas() {
        filaService.adicionarNaFila(criarAtendimento(1L, TipoTime.CARTOES));
        filaService.adicionarNaFila(criarAtendimento(2L, TipoTime.CARTOES));
        filaService.adicionarNaFila(criarAtendimento(3L, TipoTime.EMPRESTIMOS));
        filaService.adicionarNaFila(criarAtendimento(4L, TipoTime.OUTROS_ASSUNTOS));

        assertThat(filaService.totalNasFilas()).isEqualTo(4);
    }

    @Test
    @DisplayName("Deve listar atendimentos da fila sem removê-los")
    void deveListarFilaSemRemover() {
        filaService.adicionarNaFila(criarAtendimento(1L, TipoTime.CARTOES));
        filaService.adicionarNaFila(criarAtendimento(2L, TipoTime.CARTOES));

        List<Atendimento> fila = filaService.listarFila(TipoTime.CARTOES);

        assertThat(fila).hasSize(2);
        assertThat(filaService.tamanhoFila(TipoTime.CARTOES)).isEqualTo(2); // Não removeu
    }

    private Atendimento criarAtendimento(Long id, TipoTime tipoTime) {
        return Atendimento.builder()
                .id(id)
                .clienteNome("Cliente " + id)
                .assunto("Assunto " + id)
                .tipoTime(tipoTime)
                .status(StatusAtendimento.AGUARDANDO)
                .build();
    }
}