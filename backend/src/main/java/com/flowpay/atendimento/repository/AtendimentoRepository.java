package com.flowpay.atendimento.repository;

import com.flowpay.atendimento.model.entity.Atendimento;
import com.flowpay.atendimento.model.enums.StatusAtendimento;
import com.flowpay.atendimento.model.enums.TipoTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    List<Atendimento> findByStatus(StatusAtendimento status);

    List<Atendimento> findByTipoTimeAndStatus(TipoTime tipoTime, StatusAtendimento status);

    List<Atendimento> findByAtendenteId(Long atendenteId);

    long countByStatus(StatusAtendimento status);

    long countByTipoTimeAndStatus(TipoTime tipoTime, StatusAtendimento status);

    List<Atendimento> findByStatusOrderByDataHoraCriacaoAsc(StatusAtendimento status);

    List<Atendimento> findByTipoTimeAndStatusOrderByDataHoraCriacaoAsc(TipoTime tipoTime, StatusAtendimento status);
}