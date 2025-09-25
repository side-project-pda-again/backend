package org.pda.etf.pdaetf.domain.portfolio.repository;

import org.pda.etf.pdaetf.domain.portfolio.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
