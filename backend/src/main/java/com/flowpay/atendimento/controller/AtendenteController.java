package com.flowpay.atendimento.controller;

import com.flowpay.atendimento.model.dto.request.NovoAtendenteRequest;
import com.flowpay.atendimento.model.dto.response.AtendenteResponse;
import com.flowpay.atendimento.service.AtendenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atendentes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AtendenteController {

    private final AtendenteService atendenteService;

    /**
     * POST /api/atendentes
     */
    @PostMapping
    public ResponseEntity<AtendenteResponse> criar(@Valid @RequestBody NovoAtendenteRequest request) {
        log.info("POST /api/atendentes - Nome: {}, Time: {}",
                request.nome(), request.tipoTime());

        AtendenteResponse response = atendenteService.criar(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/atendentes
     */
    @GetMapping
    public ResponseEntity<List<AtendenteResponse>> listarTodos() {
        log.debug("GET /api/atendentes");

        List<AtendenteResponse> atendentes = atendenteService.listarTodos();

        return ResponseEntity.ok(atendentes);
    }
}