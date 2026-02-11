package com.flowpay.atendimento.model.entity;

import com.flowpay.atendimento.model.enums.StatusAtendimento;
import com.flowpay.atendimento.model.enums.TipoTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "atendentes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Atendente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "tipo_time")
    private TipoTime tipoTime;

    @OneToMany(mappedBy = "atendente", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Atendimento> atendimentos = new ArrayList<>();

    public boolean isDisponivel() {
        return getQuantidadeAtendimentosAtivos() < 3;
    }

    public int getQuantidadeAtendimentosAtivos() {
        return (int) atendimentos.stream()
                .filter(a -> a.getStatus() == StatusAtendimento.EM_ATENDIMENTO)
                .count();
    }

    public List<Atendimento> getAtendimentosAtivos() {
        return atendimentos.stream()
                .filter(a -> a.getStatus() == StatusAtendimento.EM_ATENDIMENTO)
                .toList();
    }
}
