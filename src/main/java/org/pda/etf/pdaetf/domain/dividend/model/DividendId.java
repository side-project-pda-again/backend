package org.pda.etf.pdaetf.domain.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DividendId implements Serializable {
    private String ticker;  // 종목코드
    private LocalDate stndDate;  // 기준일자
}
