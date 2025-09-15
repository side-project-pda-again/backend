package org.pda.etf.pdaetf.domain.etf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnCalculationDto {
    private String ticker;  // 종목코드
    private LocalDate startDate;  // 시작일
    private LocalDate endDate;  // 종료일
    private BigDecimal initialInvestment;  // 초기 투자금액 (1,000,000원)
    private BigDecimal initialPrice;  // 시작일 종가
    private BigDecimal finalPrice;  // 종료일 종가
    private BigDecimal sharesPurchased;  // 구매한 주식 수
    private BigDecimal totalDividendIncome;  // 총 배당수익
    private BigDecimal finalValue;  // 최종 평가금액 (주식가치 + 배당수익)
    private BigDecimal totalReturn;  // 총 수익금 (최종평가금액 - 초기투자금액)
    private BigDecimal returnRate;  // 수익률 (%)
    private BigDecimal priceReturnRate;  // 주가 수익률 (%)
    private BigDecimal dividendYield;  // 배당수익률 (%)
}
