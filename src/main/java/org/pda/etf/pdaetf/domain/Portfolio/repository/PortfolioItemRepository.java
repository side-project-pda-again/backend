package org.pda.etf.pdaetf.domain.Portfolio.repository;

import org.pda.etf.pdaetf.domain.Portfolio.model.PortfolioItem;
import org.pda.etf.pdaetf.domain.Portfolio.model.PortfolioItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, PortfolioItemId> {
    boolean existsByPortfolio_PortfolioIdAndEtf_Ticker(Long portfolioId, String ticker);
}