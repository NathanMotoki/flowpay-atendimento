package com.flowpay.atendimento.service;

import com.flowpay.atendimento.model.entity.Atendimento;
import com.flowpay.atendimento.model.enums.TipoTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class FilaService {

    private final Map<TipoTime, Queue<Atendimento>> filasPorTime = new ConcurrentHashMap<>();

    public FilaService() {
        for (TipoTime tipo : TipoTime.values()) {
            filasPorTime.put(tipo, new LinkedList<>());
        }
        log.info("FilaService inicializado com {} filas", TipoTime.values().length);
    }

    public void adicionarNaFila(Atendimento atendimento) {
        Queue<Atendimento> fila = filasPorTime.get(atendimento.getTipoTime());
        fila.offer(atendimento);
        log.info("Atendimento {} adicionado na fila do time {}. Tamanho da fila: {}",
                atendimento.getId(),
                atendimento.getTipoTime(),
                fila.size());
    }

    public Optional<Atendimento> proximoDaFila(TipoTime tipoTime) {
        Queue<Atendimento> fila = filasPorTime.get(tipoTime);
        Atendimento proximo = fila.poll();

        if (proximo != null) {
            log.info("Atendimento {} removido da fila do time {}. Tamanho da fila: {}",
                    proximo.getId(),
                    tipoTime,
                    fila.size());
        }

        return Optional.ofNullable(proximo);
    }

    public int tamanhoFila(TipoTime tipoTime) {
        return filasPorTime.get(tipoTime).size();
    }

    public List<Atendimento> listarFila(TipoTime tipoTime) {
        return new ArrayList<>(filasPorTime.get(tipoTime));
    }

    public int totalNasFilas() {
        return filasPorTime.values().stream()
                .mapToInt(Queue::size)
                .sum();
    }

    public boolean filaVazia(TipoTime tipoTime) {
        return filasPorTime.get(tipoTime).isEmpty();
    }
}