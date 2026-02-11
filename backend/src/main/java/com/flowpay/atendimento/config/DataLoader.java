package com.flowpay.atendimento.config;

import com.flowpay.atendimento.model.entity.Atendente;
import com.flowpay.atendimento.model.enums.TipoTime;
import com.flowpay.atendimento.repository.AtendenteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final AtendenteRepository atendenteRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== Carregando dados iniciais ===");

        if (atendenteRepository.count() > 0) {
            log.info("Dados já existem. Pulando inicialização.");
            return;
        }

        criarAtendente("Maria Silva", TipoTime.CARTOES);
        criarAtendente("Pedro Santos", TipoTime.CARTOES);
        criarAtendente("Ana Costa", TipoTime.CARTOES);

        criarAtendente("Lucas Mendes", TipoTime.EMPRESTIMOS);
        criarAtendente("Julia Rocha", TipoTime.EMPRESTIMOS);
        criarAtendente("Carlos Oliveira", TipoTime.EMPRESTIMOS);

        criarAtendente("Beatriz Lima", TipoTime.OUTROS_ASSUNTOS);
        criarAtendente("Rafael Souza", TipoTime.OUTROS_ASSUNTOS);

        log.info("=== {} atendentes criados com sucesso ===", atendenteRepository.count());
    }

    private void criarAtendente(String nome, TipoTime tipoTime) {
        Atendente atendente = Atendente.builder()
                .nome(nome)
                .tipoTime(tipoTime)
                .build();

        atendenteRepository.save(atendente);
        log.info("✅ Atendente criado: {} - {}", nome, tipoTime);
    }
}