package com.flowpay.atendimento.service;

import com.flowpay.atendimento.exception.AtendimentoInvalidoException;
import com.flowpay.atendimento.exception.AtendimentoJaFinalizadoException;
import com.flowpay.atendimento.exception.ResourceNotFoundException;
import com.flowpay.atendimento.model.dto.request.NovoAtendimentoRequest;
import com.flowpay.atendimento.model.dto.response.AtendimentoResponse;
import com.flowpay.atendimento.model.dto.response.DashboardMetricsResponse;
import com.flowpay.atendimento.model.dto.response.TimeMetrics;
import com.flowpay.atendimento.model.entity.Atendente;
import com.flowpay.atendimento.model.entity.Atendimento;
import com.flowpay.atendimento.model.enums.StatusAtendimento;
import com.flowpay.atendimento.model.enums.TipoTime;
import com.flowpay.atendimento.repository.AtendimentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service responsável pela lógica de distribuição de atendimentos
 * CORE DO DESAFIO!
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AtendimentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final AtendenteService atendenteService;
    private final FilaService filaService;

    @Transactional
    public AtendimentoResponse criarAtendimento(NovoAtendimentoRequest request) {

	validarAtendimento(request);

        TipoTime tipoTime = TipoTime.identificarPorAssunto(request.assunto());

        Atendimento atendimento = Atendimento.builder()
                .clienteNome(request.clienteNome())
                .assunto(request.assunto())
                .tipoTime(tipoTime)
                .status(StatusAtendimento.AGUARDANDO)
                .build();

        Atendimento salvo = atendimentoRepository.save(atendimento);

        boolean distribuido = tentarDistribuir(salvo);

        if (!distribuido) {
            filaService.adicionarNaFila(salvo);
            log.info("Atendimento {} adicionado na fila do time {}", salvo.getId(), tipoTime);
        }

        return AtendimentoResponse.from(atendimentoRepository.findById(salvo.getId()).orElseThrow());
    }

    private boolean tentarDistribuir(Atendimento atendimento) {
        Optional<Atendente> atendenteDisponivel =
                atendenteService.buscarAtendenteDisponivel(atendimento.getTipoTime());

        if (atendenteDisponivel.isPresent()) {
            atribuirAtendimento(atendimento, atendenteDisponivel.get());
            return true;
        }

        log.warn("Nenhum atendente disponível no time {}. Atendimento será enfileirado.",
                atendimento.getTipoTime());
        return false;
    }

    private void atribuirAtendimento(Atendimento atendimento, Atendente atendente) {
        atendimento.iniciar(atendente);
        atendimentoRepository.save(atendimento);

        log.info("✅ Atendimento {} atribuído para {} (ID: {})",
                atendimento.getId(),
                atendente.getNome(),
                atendente.getId());
        log.info("Atendente agora tem {} atendimentos ativos",
                atendente.getQuantidadeAtendimentosAtivos() + 1);
    }

    @Transactional
    public AtendimentoResponse finalizarAtendimento(Long atendimentoId) {

        Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Atendimento", atendimentoId));

        if (atendimento.getStatus() == StatusAtendimento.FINALIZADO) {
            throw new AtendimentoJaFinalizadoException(atendimentoId);
        }

        TipoTime tipoTime = atendimento.getTipoTime();
        Atendente atendente = atendimento.getAtendente();

        atendimento.finalizar();
        atendimentoRepository.save(atendimento);

        if (atendente != null && !filaService.filaVazia(tipoTime)) {

            Optional<Atendimento> proximoDaFila = filaService.proximoDaFila(tipoTime);

            if (proximoDaFila.isPresent()) {
                Atendimento proximo = proximoDaFila.get();

                atribuirAtendimento(proximo, atendente);
            }
        }

        return AtendimentoResponse.from(atendimento);
    }

    @Transactional(readOnly = true)
    public List<AtendimentoResponse> listarTodos() {
        return atendimentoRepository.findAll()
                .stream()
                .map(AtendimentoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<AtendimentoResponse> buscarPorId(Long id) {
        return atendimentoRepository.findById(id)
                .map(AtendimentoResponse::from);
    }

    @Transactional(readOnly = true)
    public DashboardMetricsResponse gerarMetricasDashboard() {
        log.debug("Gerando métricas do dashboard");

        int totalEmAndamento = (int) atendimentoRepository.countByStatus(StatusAtendimento.EM_ATENDIMENTO);
        int totalNaFila = filaService.totalNasFilas();

        int atendentesDisponiveis = 0;
        int atendentesOcupados = 0;

        for (TipoTime tipo : TipoTime.values()) {
            atendentesDisponiveis += atendenteService.contarDisponiveis(tipo);
            atendentesOcupados += atendenteService.contarOcupados(tipo);
        }

        Map<TipoTime, TimeMetrics> metricasPorTime = new HashMap<>();

        for (TipoTime tipo : TipoTime.values()) {
            int atendimentosAtivos = (int) atendimentoRepository.countByTipoTimeAndStatus(tipo, StatusAtendimento.EM_ATENDIMENTO);
            int tamanhoFila = filaService.tamanhoFila(tipo);
            int disponiveisTime = atendenteService.contarDisponiveis(tipo);
            int ocupadosTime = atendenteService.contarOcupados(tipo);

            TimeMetrics metrics = new TimeMetrics(
                    tipo,
                    atendimentosAtivos,
                    tamanhoFila,
                    disponiveisTime,
                    ocupadosTime
            );

            metricasPorTime.put(tipo, metrics);
        }

        return new DashboardMetricsResponse(
                totalEmAndamento,
                totalNaFila,
                atendentesDisponiveis,
                atendentesOcupados,
                metricasPorTime
        );
    }

    private void validarAtendimento(NovoAtendimentoRequest request) {
        if (request.clienteNome().matches(".*\\d.*")) {
       		throw new AtendimentoInvalidoException("Nome do cliente não pode conter números");
    	}
    }
}