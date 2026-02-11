package com.flowpay.atendimento.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NovoAtendimentoRequest(

	@NotBlank(message = "Nome do cliente é obrigatório") 
 	@Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
	@Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "Nome deve conter apenas letras e espaços")
        String clienteNome,

        @NotBlank(message = "Assunto é obrigatório") 
        @Size(min = 5, max = 200, message = "Assunto deve ter entre 5 e 200 caracteres") 
        String assunto

) {}