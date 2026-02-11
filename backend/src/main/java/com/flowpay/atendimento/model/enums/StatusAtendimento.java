package com.flowpay.atendimento.model.enums;

import lombok.Getter;

@Getter
public enum StatusAtendimento {
    AGUARDANDO("Aguardando na fila"),
    EM_ATENDIMENTO("Em atendimento"),
    FINALIZADO("Finalizado");

    private final String descricao;

    StatusAtendimento(String descricao) {
        this.descricao = descricao;
    }

}