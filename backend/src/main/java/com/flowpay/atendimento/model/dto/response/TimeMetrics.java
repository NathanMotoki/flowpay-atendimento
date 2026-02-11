package com.flowpay.atendimento.model.dto.response;

import com.flowpay.atendimento.model.enums.TipoTime;

public record TimeMetrics(
        TipoTime tipo,
        int atendimentosAtivos,
        int tamanhoFila,
        int atendentesDisponiveis,
        int atendentesOcupados
) {}
