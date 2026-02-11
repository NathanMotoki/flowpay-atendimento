package com.flowpay.atendimento.controller;

import com.flowpay.atendimento.model.dto.request.NovoAtendimentoRequest;
import com.flowpay.atendimento.model.dto.response.AtendimentoResponse;
import com.flowpay.atendimento.service.AtendimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atendimentos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AtendimentoController {

    private final AtendimentoService atendimentoService;

    /**
     * POST /api/atendimentos
     */
    @PostMapping
    public ResponseEntity<AtendimentoResponse> criar(@Valid @RequestBody NovoAtendimentoRequest request) {
        log.info("POST /api/atendimentos - Cliente: {}, Assunto: {}",
                request.clienteNome(), request.assunto());

        AtendimentoResponse response = atendimentoService.criarAtendimento(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/atendimentos
     */
    @GetMapping
    public ResponseEntity<List<AtendimentoResponse>> listarTodos() {
        log.debug("GET /api/atendimentos");

        List<AtendimentoResponse> atendimentos = atendimentoService.listarTodos();

        return ResponseEntity.ok(atendimentos);
    }

    /**
     * GET /api/atendimentos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AtendimentoResponse> buscarPorId(@PathVariable Long id) {
        log.debug("GET /api/atendimentos/{}", id);

        return atendimentoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/atendimentos/{id}/finalizar
     */
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<AtendimentoResponse> finalizar(@PathVariable Long id) {
        log.info("PUT /api/atendimentos/{}/finalizar", id);

        try {
            AtendimentoResponse response = atendimentoService.finalizarAtendimento(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Erro ao finalizar atendimento {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}