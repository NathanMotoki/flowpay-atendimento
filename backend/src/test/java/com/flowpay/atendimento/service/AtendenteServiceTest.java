package com.flowpay.atendimento.service;

import com.flowpay.atendimento.model.dto.request.NovoAtendenteRequest;
import com.flowpay.atendimento.model.dto.response.AtendenteResponse;
import com.flowpay.atendimento.model.entity.Atendente;
import com.flowpay.atendimento.model.enums.TipoTime;
import com.flowpay.atendimento.repository.AtendenteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AtendenteService - Testes com Mockito")
class AtendenteServiceTest {

    @Mock
    private AtendenteRepository atendenteRepository;

    @InjectMocks
    private AtendenteService atendenteService;

    @Test
    @DisplayName("Deve criar atendente com sucesso")
    void deveCriarAtendenteComSucesso() {
        NovoAtendenteRequest request = new NovoAtendenteRequest(
                "Maria Silva",
                TipoTime.CARTOES
        );

        Atendente atendenteSalvo = Atendente.builder()
                .id(1L)
                .nome("Maria Silva")
                .tipoTime(TipoTime.CARTOES)
                .build();

        when(atendenteRepository.save(any(Atendente.class)))
                .thenReturn(atendenteSalvo);

        AtendenteResponse response = atendenteService.criar(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Maria Silva");
        assertThat(response.tipoTime()).isEqualTo(TipoTime.CARTOES);

        verify(atendenteRepository, times(1)).save(any(Atendente.class));
    }

    @Test
    @DisplayName("Deve listar todos os atendentes")
    void deveListarTodosOsAtendentes() {
        List<Atendente> atendentes = Arrays.asList(
                criarAtendente(1L, "Maria Silva", TipoTime.CARTOES),
                criarAtendente(2L, "Pedro Santos", TipoTime.EMPRESTIMOS),
                criarAtendente(3L, "Ana Costa", TipoTime.OUTROS_ASSUNTOS)
        );

        when(atendenteRepository.findAll()).thenReturn(atendentes);

        List<AtendenteResponse> responses = atendenteService.listarTodos();

        assertThat(responses).hasSize(3);
        assertThat(responses).extracting(AtendenteResponse::nome)
                .containsExactly("Maria Silva", "Pedro Santos", "Ana Costa");

        verify(atendenteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar atendente por ID quando existir")
    void deveBuscarAtendentePorIdQuandoExistir() {
        Atendente atendente = criarAtendente(1L, "Maria Silva", TipoTime.CARTOES);
        when(atendenteRepository.findById(1L)).thenReturn(Optional.of(atendente));

        Optional<Atendente> resultado = atendenteService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Maria Silva");
        assertThat(resultado.get().getTipoTime()).isEqualTo(TipoTime.CARTOES);

        verify(atendenteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar vazio quando atendente não existir")
    void deveRetornarVazioQuandoAtendenteNaoExistir() {
        when(atendenteRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Atendente> resultado = atendenteService.buscarPorId(999L);

        assertThat(resultado).isEmpty();

        verify(atendenteRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve buscar atendentes por time")
    void deveBuscarAtendentesPorTime() {
        List<Atendente> atendentesCartoes = Arrays.asList(
                criarAtendente(1L, "Maria Silva", TipoTime.CARTOES),
                criarAtendente(2L, "Pedro Santos", TipoTime.CARTOES),
                criarAtendente(3L, "Ana Costa", TipoTime.CARTOES)
        );

        when(atendenteRepository.findByTipoTime(TipoTime.CARTOES))
                .thenReturn(atendentesCartoes);

        List<Atendente> resultado = atendenteService.buscarPorTime(TipoTime.CARTOES);

        assertThat(resultado).hasSize(3);
        assertThat(resultado).allMatch(a -> a.getTipoTime() == TipoTime.CARTOES);

        verify(atendenteRepository, times(1)).findByTipoTime(TipoTime.CARTOES);
    }

    @Test
    @DisplayName("Deve buscar atendente disponível quando existir")
    void deveBuscarAtendenteDisponivelQuandoExistir() {
        List<Atendente> disponiveis = Collections.singletonList(
                criarAtendente(1L, "Maria Silva", TipoTime.CARTOES)
        );

        when(atendenteRepository.findDisponiveisByTipoTime(TipoTime.CARTOES))
                .thenReturn(disponiveis);

        Optional<Atendente> resultado = atendenteService.buscarAtendenteDisponivel(TipoTime.CARTOES);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Maria Silva");

        verify(atendenteRepository, times(1)).findDisponiveisByTipoTime(TipoTime.CARTOES);
    }

    @Test
    @DisplayName("Deve retornar vazio quando não há atendente disponível")
    void deveRetornarVazioQuandoNaoHaDisponiveis() {
        when(atendenteRepository.findDisponiveisByTipoTime(TipoTime.CARTOES))
                .thenReturn(Collections.emptyList());

        Optional<Atendente> resultado = atendenteService.buscarAtendenteDisponivel(TipoTime.CARTOES);

        assertThat(resultado).isEmpty();

        verify(atendenteRepository, times(1)).findDisponiveisByTipoTime(TipoTime.CARTOES);
    }

    @Test
    @DisplayName("Deve contar atendentes disponíveis corretamente")
    void deveContarAtendentesDisponiveisCorretamente() {
        List<Atendente> disponiveis = Arrays.asList(
                criarAtendente(1L, "Maria", TipoTime.CARTOES),
                criarAtendente(2L, "Pedro", TipoTime.CARTOES)
        );

        when(atendenteRepository.findDisponiveisByTipoTime(TipoTime.CARTOES))
                .thenReturn(disponiveis);

        int quantidade = atendenteService.contarDisponiveis(TipoTime.CARTOES);

        assertThat(quantidade).isEqualTo(2);

        verify(atendenteRepository, times(1)).findDisponiveisByTipoTime(TipoTime.CARTOES);
    }

    private Atendente criarAtendente(Long id, String nome, TipoTime tipoTime) {
        return Atendente.builder()
                .id(id)
                .nome(nome)
                .tipoTime(tipoTime)
                .build();
    }
}