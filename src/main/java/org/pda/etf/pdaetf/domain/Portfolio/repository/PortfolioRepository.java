package org.pda.etf.pdaetf.domain.Portfolio.repository;

import org.pda.etf.pdaetf.domain.Portfolio.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
