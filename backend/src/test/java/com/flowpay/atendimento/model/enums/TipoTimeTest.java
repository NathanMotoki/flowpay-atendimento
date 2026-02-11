package com.flowpay.atendimento.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TipoTime - Testes de Identificação de Time por Assunto")
class TipoTimeTest {

    @Test
    @DisplayName("Deve identificar Time Cartões quando assunto contém 'cartão'")
    void deveIdentificarTimeCartoes() {
        String assunto1 = "Problemas com cartão";
        String assunto2 = "Cartão bloqueado";
        String assunto3 = "Problemas com cartao";

        TipoTime tipo1 = TipoTime.identificarPorAssunto(assunto1);
        TipoTime tipo2 = TipoTime.identificarPorAssunto(assunto2);
        TipoTime tipo3 = TipoTime.identificarPorAssunto(assunto3);

        assertThat(tipo1).isEqualTo(TipoTime.CARTOES);
        assertThat(tipo2).isEqualTo(TipoTime.CARTOES);
        assertThat(tipo3).isEqualTo(TipoTime.CARTOES);
    }

    @Test
    @DisplayName("Deve identificar Time Empréstimos quando assunto contém 'empréstimo'")
    void deveIdentificarTimeEmprestimos() {
        String assunto1 = "Contratação de empréstimo";
        String assunto2 = "Empréstimo pessoal";
        String assunto3 = "Contratacao de emprestimo"; // sem acento

        TipoTime tipo1 = TipoTime.identificarPorAssunto(assunto1);
        TipoTime tipo2 = TipoTime.identificarPorAssunto(assunto2);
        TipoTime tipo3 = TipoTime.identificarPorAssunto(assunto3);

        assertThat(tipo1).isEqualTo(TipoTime.EMPRESTIMOS);
        assertThat(tipo2).isEqualTo(TipoTime.EMPRESTIMOS);
        assertThat(tipo3).isEqualTo(TipoTime.EMPRESTIMOS);
    }

    @Test
    @DisplayName("Deve identificar Time Outros Assuntos para assuntos genéricos")
    void deveIdentificarTimeOutrosAssuntos() {
        String assunto1 = "Dúvida sobre investimentos";
        String assunto2 = "Atualização cadastral";
        String assunto3 = "Consulta de saldo";

        TipoTime tipo1 = TipoTime.identificarPorAssunto(assunto1);
        TipoTime tipo2 = TipoTime.identificarPorAssunto(assunto2);
        TipoTime tipo3 = TipoTime.identificarPorAssunto(assunto3);

        assertThat(tipo1).isEqualTo(TipoTime.OUTROS_ASSUNTOS);
        assertThat(tipo2).isEqualTo(TipoTime.OUTROS_ASSUNTOS);
        assertThat(tipo3).isEqualTo(TipoTime.OUTROS_ASSUNTOS);
    }

    @Test
    @DisplayName("Deve ser case-insensitive (maiúsculas/minúsculas)")
    void deveSerCaseInsensitive() {
        TipoTime tipo1 = TipoTime.identificarPorAssunto("PROBLEMAS COM CARTÃO");
        TipoTime tipo2 = TipoTime.identificarPorAssunto("problemas com cartão");
        TipoTime tipo3 = TipoTime.identificarPorAssunto("PrObLeMaS cOm CaRtÃo");

        assertThat(tipo1).isEqualTo(TipoTime.CARTOES);
        assertThat(tipo2).isEqualTo(TipoTime.CARTOES);
        assertThat(tipo3).isEqualTo(TipoTime.CARTOES);
    }
}