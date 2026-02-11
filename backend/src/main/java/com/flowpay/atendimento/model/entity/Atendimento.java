package com.flowpay.atendimento.model.entity;

import com.flowpay.atendimento.model.enums.StatusAtendimento;
import com.flowpay.atendimento.model.enums.TipoTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "atendimentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "cliente_nome", length = 100)
    private String clienteNome;

    @Column(nullable = false, length = 200)
    private String assunto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "tipo_time")
    private TipoTime tipoTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusAtendimento status = StatusAtendimento.AGUARDANDO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendente_id")
    private Atendente atendente;

    @Column(name = "data_hora_inicio")
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim")
    private LocalDateTime dataHoraFim;

    @Column(name = "data_hora_criacao", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dataHoraCriacao = LocalDateTime.now();

    public void iniciar(Atendente atendente) {
        this.atendente = atendente;
        this.status = StatusAtendimento.EM_ATENDIMENTO;
        this.dataHoraInicio = LocalDateTime.now();
    }

    public void finalizar() {
        this.status = StatusAtendimento.FINALIZADO;
        this.dataHoraFim = LocalDateTime.now();
    }
}
