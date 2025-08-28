package org.pda.etf.pdaetf.domain.etf.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "etf_prices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EtfPrice {
    @EmbeddedId
    private EtfPriceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("etfId")
    private Etf etf;

    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;

    @Column(nullable = false)
    private BigDecimal close;

    private BigDecimal adjClose;
    private Long volume;
    private BigDecimal nav;
    private BigDecimal premiumPct;
}