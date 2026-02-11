package com.flowpay.atendimento.model.dto.response;

import com.flowpay.atendimento.model.entity.Atendente;
import com.flowpay.atendimento.model.enums.TipoTime;


public record AtendenteResponse(
        Long id,
        String nome,
        TipoTime tipoTime,
        int atendimentosAtuais,
        boolean disponivel
) {

    public static AtendenteResponse from(Atendente entity) {
        int qtdAtendimentos = entity.getQuantidadeAtendimentosAtivos();
        return new AtendenteResponse(
                entity.getId(),
                entity.getNome(),
                entity.getTipoTime(),
                qtdAtendimentos,
                entity.isDisponivel()
        );
    }
}