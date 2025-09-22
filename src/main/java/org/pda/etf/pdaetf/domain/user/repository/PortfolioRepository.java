package org.pda.etf.pdaetf.domain.user.repository;

import org.pda.etf.pdaetf.domain.user.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByOwner_UserId(Long userId);
    boolean existsByPortfolioIdAndOwner_UserId(Long portfolioId, Long ownerId);
}
