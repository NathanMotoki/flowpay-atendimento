package com.flowpay.atendimento.service;

import com.flowpay.atendimento.model.dto.request.NovoAtendenteRequest;
import com.flowpay.atendimento.model.dto.response.AtendenteResponse;
import com.flowpay.atendimento.model.entity.Atendente;
import com.flowpay.atendimento.model.enums.TipoTime;
import com.flowpay.atendimento.repository.AtendenteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtendenteService {

    private final AtendenteRepository atendenteRepository;

    @Transactional
    public AtendenteResponse criar(NovoAtendenteRequest request) {
        log.info("Criando novo atendente: {} - Time: {}", request.nome(), request.tipoTime());

        Atendente atendente = Atendente.builder()
                .nome(request.nome())
                .tipoTime(request.tipoTime())
                .build();

        Atendente salvo = atendenteRepository.save(atendente);
        log.info("Atendente criado com sucesso. ID: {}", salvo.getId());

        return AtendenteResponse.from(salvo);
    }

    @Transactional(readOnly = true)
    public List<AtendenteResponse> listarTodos() {
        log.debug("Listando todos os atendentes");
        return atendenteRepository.findAll()
                .stream()
                .map(AtendenteResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Atendente> buscarPorId(Long id) {
        return atendenteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Atendente> buscarPorTime(TipoTime tipoTime) {
        log.debug("Buscando atendentes do time: {}", tipoTime);
        return atendenteRepository.findByTipoTime(tipoTime);
    }

    @Transactional(readOnly = true)
    public Optional<Atendente> buscarAtendenteDisponivel(TipoTime tipoTime) {
        log.debug("Buscando atendente disponível do time: {}", tipoTime);

        List<Atendente> disponiveis = atendenteRepository.findDisponiveisByTipoTime(tipoTime);

        if (disponiveis.isEmpty()) {
            log.warn("Nenhum atendente disponível no time: {}", tipoTime);
            return Optional.empty();
        }

        Atendente escolhido = disponiveis.get(0);
        log.info("Atendente disponível encontrado: {} (ID: {}) - {} atendimentos ativos",
                escolhido.getNome(),
                escolhido.getId(),
                escolhido.getQuantidadeAtendimentosAtivos());

        return Optional.of(escolhido);
    }

    @Transactional(readOnly = true)
    public int contarDisponiveis(TipoTime tipoTime) {
        return atendenteRepository.findDisponiveisByTipoTime(tipoTime).size();
    }

    @Transactional(readOnly = true)
    public int contarOcupados(TipoTime tipoTime) {
        List<Atendente> todos = atendenteRepository.findByTipoTime(tipoTime);
        return (int) todos.stream()
                .filter(a -> !a.isDisponivel())
                .count();
    }
}