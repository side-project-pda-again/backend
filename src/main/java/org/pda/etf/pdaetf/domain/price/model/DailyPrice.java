package org.pda.etf.pdaetf.domain.price.model;

import jakarta.persistence.*;
import lombok.*;
import org.pda.etf.pdaetf.domain.etf.model.Etf;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_prices", 
       indexes = @Index(name = "idx_daily_price_ticker_stnd_date", 
                       columnList = "ticker, stndDate"))
@IdClass(DailyPriceId.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DailyPrice {
    @Id
    @Column(name = "ticker", length = 20)
    private String ticker;  // 종목코드

    @Id
    @Column(name = "stnd_date")
    private LocalDate stndDate;  // 기준일자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker", insertable = false, updatable = false)
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
