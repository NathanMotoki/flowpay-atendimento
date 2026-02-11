package com.flowpay.atendimento.controller;

import com.flowpay.atendimento.model.dto.response.DashboardMetricsResponse;
import com.flowpay.atendimento.service.AtendimentoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DashboardController {

    private final AtendimentoService atendimentoService;

    /**
     * GET /api/dashboard/metricas
     */
    @GetMapping("/metricas")
    public ResponseEntity<DashboardMetricsResponse> getMetricas() {
        log.debug("GET /api/dashboard/metricas");

        DashboardMetricsResponse metricas = atendimentoService.gerarMetricasDashboard();

        return ResponseEntity.ok(metricas);
    }
}