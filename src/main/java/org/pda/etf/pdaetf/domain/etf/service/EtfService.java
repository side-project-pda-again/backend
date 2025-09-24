package org.pda.etf.pdaetf.domain.etf.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pda.etf.pdaetf.common.exception.ApiException;
import org.pda.etf.pdaetf.common.exception.ErrorCode;
import org.pda.etf.pdaetf.domain.dividend.model.Dividend;
import org.pda.etf.pdaetf.domain.dividend.repository.DividendRepository;
import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.pda.etf.pdaetf.domain.etf.dto.ReturnCalculationDto;
import org.pda.etf.pdaetf.domain.etf.model.Etf;
import org.pda.etf.pdaetf.domain.etf.repository.EtfRepository;
import org.pda.etf.pdaetf.domain.etf.repository.query.EtfQueryRepository;
import org.pda.etf.pdaetf.domain.price.model.DailyPrice;
import org.pda.etf.pdaetf.domain.price.repository.DailyPriceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EtfService {

    private final EtfRepository etfRepository;
    private final DailyPriceRepository dailyPriceRepository;
    private final DividendRepository dividendRepository;
    private final EtfQueryRepository etfQueryRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public List<Etf> findAll() {
        return etfRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Etf findByTicker(String ticker) {
        return etfRepository.findById(ticker)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
    }

    @Transactional
    public Etf save(Etf etf) {
        return etfRepository.save(etf);
    }

    @Transactional
    public void delete(String ticker) {
        etfRepository.deleteById(ticker);
    }

    /**
     * 종목의 수익률 계산
     */
    public ReturnCalculationDto calculateReturn(String ticker, String startDate, String endDate) {
        /*
         * 처리 순서
         * 1. 시작일과 종료일 종가 조회
         * 2. 초기 투자금액 (100만원)과 구매한 주식 수 계산
         * 3. 조회 기간 동안의 배당 수익 계산
         * 4. 최종 평가금액, 총 수익금, 수익률, 주가 수익률, 배당수익률 계산
         * 5. 결과 반환
         */
        try {
            LocalDate start = parseDate(startDate);
            LocalDate end = parseDate(endDate);
            findByTicker(ticker);
            
            //1. 시작일과 종료일 종가 조회
            BigDecimal initialPrice = dailyPriceRepository.findByTickerAndStndDate(ticker, start)
            .map(DailyPrice::getClose)
            .orElseThrow(() -> {
                log.error("시작일 가격 데이터 없음 - ticker: {}, startDate: {}", ticker, start);
                return new ApiException(ErrorCode.NOT_FOUND, "시작일 가격 데이터를 찾을 수 없습니다.");
            });
            BigDecimal finalPrice = dailyPriceRepository.findByTickerAndStndDate(ticker, end)
            .map(DailyPrice::getClose)
            .orElseThrow(() -> {
                log.error("종료일 가격 데이터 없음 - ticker: {}, endDate: {}", ticker, end);
                return new ApiException(ErrorCode.NOT_FOUND, "종료일 가격 데이터를 찾을 수 없습니다.");
            });
            
            // 2. 초기 투자금액 (100만원)과 구매한 주식 수 계산
            BigDecimal initialInvestment = new BigDecimal("1000000");
            BigDecimal sharesPurchased = initialInvestment.divide(initialPrice, 4, RoundingMode.HALF_UP);
            
            // 3. 조회 기간 동안의 배당 수익 계산
            List<Dividend> dividends = dividendRepository.findByTickerAndStndDateBetween(ticker, start, end);
            BigDecimal totalDividendIncome = dividends.stream()
                    .map(Dividend::getDividendAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(sharesPurchased);
            
            // 4. 최종 평가금액, 총 수익금, 수익률, 주가 수익률, 배당수익률 계산
            BigDecimal finalValue = finalPrice.multiply(sharesPurchased).add(totalDividendIncome); //최종 평가금액 = (주식수 * 종료일 종가) + 배당수익
            BigDecimal totalReturn = finalValue.subtract(initialInvestment);    //총 수익금 = 최종 평가금액 - 초기 투자금액
            BigDecimal returnRate = totalReturn.divide(initialInvestment, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));   //수익률 = (총 수익금 / 초기 투자금액) * 100
            BigDecimal priceReturnRate = finalPrice.subtract(initialPrice)
                    .divide(initialPrice, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));   //주가 수익률 = (종료일 종가 - 시작일 종가) / 시작일 종가 * 100
            BigDecimal dividendYield = totalDividendIncome.divide(initialInvestment, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));   //배당수익률 = (배당수익 / 초기 투자금액) * 100
            
            
            return ReturnCalculationDto.builder()
                    .ticker(ticker)
                    .startDate(start)
                    .endDate(end)
                    .initialInvestment(initialInvestment)
                    .initialPrice(initialPrice)
                    .finalPrice(finalPrice)
                    .sharesPurchased(sharesPurchased)
                    .totalDividendIncome(totalDividendIncome)
                    .finalValue(finalValue)
                    .totalReturn(totalReturn)
                    .returnRate(returnRate)
                    .priceReturnRate(priceReturnRate)
                    .dividendYield(dividendYield)
                    .build();
                    
        } catch (ApiException e) {
            log.error("수익률 계산 실패 (ApiException) - ticker: {}, startDate: {}, endDate: {}, error: {}", 
                     ticker, startDate, endDate, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("수익률 계산 실패 (Exception) - ticker: {}, startDate: {}, endDate: {}, error: {}", 
                     ticker, startDate, endDate, e.getMessage(), e);
            throw new ApiException(ErrorCode.INTERNAL_ERROR, "수익률 계산 중 오류가 발생했습니다.");
        }
    }


    /**
     * YYYYMMDD 형식의 문자열을 LocalDate로 변환
     */
    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("날짜 파싱 실패 - dateStr: {}, error: {}", dateStr, e.getMessage());
            throw new ApiException(
                ErrorCode.INVALID_INPUT,
                "날짜 형식이 올바르지 않습니다. YYYYMMDD 형식으로 입력해주세요."
            );
        }
    }


    /**
     * ETF 검색 (최신가/전일가/변동/liked 포함)
     */
    public Page<EtfRowDto> searchEtfs(String query, Long categoryId, Pageable pageable, Long currentUserId){
        return etfQueryRepository.searchEtfs(query, categoryId, currentUserId, pageable);
    }
}