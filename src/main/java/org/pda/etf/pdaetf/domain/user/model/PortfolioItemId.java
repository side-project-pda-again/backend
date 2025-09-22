package org.pda.etf.pdaetf.domain.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class PortfolioItemId {

    @Column(name = "portfolio_id")
    private Long portfolioId;

    @Column(name="etf_ticker", length = 20)
    private String ticker;
}
