package org.pda.etf.pdaetf.domain.dividend.model;

import jakarta.persistence.*;
import lombok.*;
import org.pda.etf.pdaetf.domain.etf.model.Etf;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "dividends", 
       indexes = @Index(name = "idx_dividend_ticker_stnd_date", 
                       columnList = "ticker, stndDate"))
@IdClass(DividendId.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Dividend {
    @Id
    @Column(name = "ticker", length = 20)
    private String ticker;  // 종목코드

    @Id
    @Column(name = "stnd_date")
    private LocalDate stndDate;  // 기준일자

    @Column(name = "dividend_amount", precision = 19, scale = 4)
    private BigDecimal dividendAmount;  // 배당금액

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker", insertable = false, updatable = false)
    private Etf etf;
}
