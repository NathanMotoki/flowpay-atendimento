package com.flowpay.atendimento.model.enums;

import lombok.Getter;

@Getter
public enum TipoTime {

    CARTOES("Time Cartões"),
    EMPRESTIMOS("Time Empréstimos"),
    OUTROS_ASSUNTOS("Time Outros Assuntos");

    private final String descricao;

    TipoTime(String descricao) {
        this.descricao = descricao;
    }

    public static TipoTime identificarPorAssunto(String assunto) {
        String assuntoLower = assunto.toLowerCase().trim();

        if (assuntoLower.contains("cartão") ||
                assuntoLower.contains("cartao") ||
                assuntoLower.contains("problemas com cartão")) {
            return CARTOES;
        }

        if (assuntoLower.contains("empréstimo") ||
                assuntoLower.contains("emprestimo") ||
                assuntoLower.contains("contratação de empréstimo") ||
                assuntoLower.contains("contratacao")) {
            return EMPRESTIMOS;
        }

        return OUTROS_ASSUNTOS;
    }

}
