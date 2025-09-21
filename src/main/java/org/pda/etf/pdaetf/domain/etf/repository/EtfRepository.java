package org.pda.etf.pdaetf.domain.etf.repository;

import org.pda.etf.pdaetf.domain.etf.model.Etf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EtfRepository extends JpaRepository<Etf, String>, JpaSpecificationExecutor<Etf> {
    boolean existsByTicker(String ticker);
}