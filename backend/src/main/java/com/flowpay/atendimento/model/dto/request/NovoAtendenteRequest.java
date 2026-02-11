package com.flowpay.atendimento.model.dto.request;

import com.flowpay.atendimento.model.enums.TipoTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NovoAtendenteRequest(

        @NotBlank(message = "Nome do atendente é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String nome,

        @NotNull(message = "Tipo de time é obrigatório")
        TipoTime tipoTime

) {}