package org.pda.etf.pdaetf.domain.price.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyPriceId implements Serializable {
    private String ticker;  // 종목코드
    private LocalDate stndDate;  // 기준일자
}
