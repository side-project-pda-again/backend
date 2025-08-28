package org.pda.etf.pdaetf.domain.etf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.pda.etf.pdaetf.domain.etf.model.Etf;

public interface EtfRepository extends JpaRepository<Etf, Long> {
    boolean existsByTicker(String ticker);
}