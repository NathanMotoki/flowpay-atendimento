package com.flowpay.atendimento.model.dto.response;

import com.flowpay.atendimento.model.enums.TipoTime;

import java.util.Map;

public record DashboardMetricsResponse(
        int totalAtendimentosEmAndamento,
        int totalNaFila,
        int atendentesDisponiveis,
        int atendentesOcupados,
        Map<TipoTime, TimeMetrics> metricasPorTime
) {}