package org.pda.etf.pdaetf.domain.price.service;

import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.domain.price.model.DailyPrice;
import org.pda.etf.pdaetf.domain.price.repository.DailyPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyPriceService {
    
    private final DailyPriceRepository dailyPriceRepository;
    
    /**
     * 모든 일봉 데이터 조회
     */
    public List<DailyPrice> getAllDailyPrices() {
        return dailyPriceRepository.findAll();
    }
    
    /**
     * 종목코드로 일봉 데이터 조회
     */
    public List<DailyPrice> getDailyPricesByTicker(String ticker) {
        return dailyPriceRepository.findByTickerOrderByStndDateDesc(ticker);
    }
    
    /**
     * 종목코드와 기준일자로 일봉 데이터 조회
     */
    public Optional<DailyPrice> getDailyPriceByTickerAndDate(String ticker, LocalDate stndDate) {
        return dailyPriceRepository.findByTickerAndStndDate(ticker, stndDate);
    }
    
    /**
     * 기준일자 범위로 일봉 데이터 조회
     */
    public List<DailyPrice> getDailyPricesByDateRange(LocalDate startDate, LocalDate endDate) {
        return dailyPriceRepository.findByStndDateBetween(startDate, endDate);
    }
    
    /**
     * 종목코드와 기준일자 범위로 일봉 데이터 조회
     */
    public List<DailyPrice> getDailyPricesByTickerAndDateRange(String ticker, LocalDate startDate, LocalDate endDate) {
        return dailyPriceRepository.findByTickerAndStndDateBetween(ticker, startDate, endDate);
    }
    
    /**
     * 일봉 데이터 저장
     */
    @Transactional
    public DailyPrice saveDailyPrice(DailyPrice dailyPrice) {
        return dailyPriceRepository.save(dailyPrice);
    }
    
    /**
     * 일봉 데이터 수정
     */
    @Transactional
    public DailyPrice updateDailyPrice(DailyPrice dailyPrice) {
        return dailyPriceRepository.save(dailyPrice);
    }
    
    /**
     * 일봉 데이터 삭제
     */
    @Transactional
    public void deleteDailyPrice(String ticker, LocalDate stndDate) {
        dailyPriceRepository.deleteByTickerAndStndDate(ticker, stndDate);
    }
    
    /**
     * 종목코드의 모든 일봉 데이터 삭제
     */
    @Transactional
    public void deleteDailyPricesByTicker(String ticker) {
        dailyPriceRepository.deleteByTicker(ticker);
    }
}
