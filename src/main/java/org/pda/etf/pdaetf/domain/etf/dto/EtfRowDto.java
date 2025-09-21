package org.pda.etf.pdaetf.domain.etf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EtfRowDto {
    private String ticker;
    private String krIsnm;
    private String market;

    private LocalDate stndDate;
    private BigDecimal latestPrice;
    private BigDecimal prevClose;
    private BigDecimal change;
    private BigDecimal changePct;

    private LocalDate latestDividendDate;
    private BigDecimal latestDividendAmount;

    private Boolean liked;
}