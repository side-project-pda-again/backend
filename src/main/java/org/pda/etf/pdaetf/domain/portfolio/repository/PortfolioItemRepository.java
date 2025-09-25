package org.pda.etf.pdaetf.domain.portfolio.repository;

import org.pda.etf.pdaetf.domain.portfolio.model.PortfolioItem;
import org.pda.etf.pdaetf.domain.portfolio.model.PortfolioItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, PortfolioItemId> {
    boolean existsByPortfolio_PortfolioIdAndEtf_Ticker(Long portfolioId, String ticker);
}