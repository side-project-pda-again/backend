package org.pda.etf.pdaetf.domain.Portfolio.repository.query;

import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PortfolioQueryRepository {
    Page<EtfRowDto> findEtfsInPortfolio(Long userId, Long portfolioId, Pageable pageable);
}
