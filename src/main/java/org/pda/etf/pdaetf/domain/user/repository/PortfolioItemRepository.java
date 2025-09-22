package org.pda.etf.pdaetf.domain.user.repository;

import org.pda.etf.pdaetf.domain.user.model.PortfolioItem;
import org.pda.etf.pdaetf.domain.user.model.PortfolioItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, PortfolioItemId> {
    List<PortfolioItem> findByPortfolio_PortfolioId(Long portfolioId);
    boolean existsByPortfolio_PortfolioIdAndEtf_Ticker(Long portfolioId, String ticker);
}