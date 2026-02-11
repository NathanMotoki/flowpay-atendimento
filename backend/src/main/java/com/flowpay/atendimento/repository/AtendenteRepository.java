package com.flowpay.atendimento.repository;

import com.flowpay.atendimento.model.entity.Atendente;
import com.flowpay.atendimento.model.enums.TipoTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtendenteRepository extends JpaRepository<Atendente, Long> {

    List<Atendente> findByTipoTime(TipoTime tipoTime);

    @Query("""
        SELECT a FROM Atendente a
        WHERE a.tipoTime = :tipoTime
        AND (
            SELECT COUNT(at) FROM Atendimento at
            WHERE at.atendente.id = a.id
            AND at.status = 'EM_ATENDIMENTO'
        ) < 3
        ORDER BY (
            SELECT COUNT(at2) FROM Atendimento at2
            WHERE at2.atendente.id = a.id
            AND at2.status = 'EM_ATENDIMENTO'
        ) ASC
        """)
    List<Atendente> findDisponiveisByTipoTime(TipoTime tipoTime);
}