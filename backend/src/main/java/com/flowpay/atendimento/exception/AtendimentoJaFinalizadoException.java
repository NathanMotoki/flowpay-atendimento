package com.flowpay.atendimento.exception;

public class AtendimentoJaFinalizadoException extends BusinessException {
    public AtendimentoJaFinalizadoException(Long atendimentoId) {
        super(String.format("Atendimento %d já foi finalizado", atendimentoId));
    }
}