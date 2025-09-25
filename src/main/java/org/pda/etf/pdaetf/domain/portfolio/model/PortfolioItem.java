package org.pda.etf.pdaetf.domain.portfolio.model;

import jakarta.persistence.*;
import lombok.*;
import org.pda.etf.pdaetf.domain.etf.model.Etf;

import java.time.LocalDateTime;

@Entity
@Table(
        name="portfolio_items",
        uniqueConstraints = @UniqueConstraint(name = "uq_portfolio_item", columnNames = {"portfolio_id", "etf_ticker"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PortfolioItem {

    @EmbeddedId
    private PortfolioItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("portfolioId")
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ticker")
    @JoinColumn(name = "etf_ticker", nullable = false)
    private Etf etf;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate(){if(createdAt==null) createdAt=LocalDateTime.now();}
}
