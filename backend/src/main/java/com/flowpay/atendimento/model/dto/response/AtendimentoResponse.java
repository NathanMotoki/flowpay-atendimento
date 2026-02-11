package com.flowpay.atendimento.model.dto.response;

import com.flowpay.atendimento.model.entity.Atendimento;
import com.flowpay.atendimento.model.enums.StatusAtendimento;
import com.flowpay.atendimento.model.enums.TipoTime;

import java.time.LocalDateTime;

public record AtendimentoResponse(
        Long id,
        String clienteNome,
        String assunto,
        TipoTime tipoTime,
        StatusAtendimento status,
        Long atendenteId,
        String atendenteNome,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        LocalDateTime dataHoraCriacao
) {

    public static AtendimentoResponse from(Atendimento entity) {
        return new AtendimentoResponse(
                entity.getId(),
                entity.getClienteNome(),
                entity.getAssunto(),
                entity.getTipoTime(),
                entity.getStatus(),
                entity.getAtendente() != null ? entity.getAtendente().getId() : null,
                entity.getAtendente() != null ? entity.getAtendente().getNome() : null,
                entity.getDataHoraInicio(),
                entity.getDataHoraFim(),
                entity.getDataHoraCriacao()
        );
    }
}