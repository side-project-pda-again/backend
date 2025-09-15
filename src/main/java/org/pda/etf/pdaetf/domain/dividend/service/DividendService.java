package org.pda.etf.pdaetf.domain.dividend.service;

import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.domain.dividend.model.Dividend;
import org.pda.etf.pdaetf.domain.dividend.repository.DividendRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DividendService {
    
    private final DividendRepository dividendRepository;
    
    /**
     * 모든 배당 정보 조회
     */
    public List<Dividend> getAllDividends() {
        return dividendRepository.findAll();
    }
    
    /**
     * 종목코드로 배당 정보 조회
     */
    public List<Dividend> getDividendsByTicker(String ticker) {
        return dividendRepository.findByTickerOrderByStndDateDesc(ticker);
    }
    
    /**
     * 종목코드와 기준일자로 배당 정보 조회
     */
    public Optional<Dividend> getDividendByTickerAndDate(String ticker, LocalDate stndDate) {
        return dividendRepository.findByTickerAndStndDate(ticker, stndDate);
    }
    
    /**
     * 기준일자 범위로 배당 정보 조회
     */
    public List<Dividend> getDividendsByDateRange(LocalDate startDate, LocalDate endDate) {
        return dividendRepository.findByStndDateBetween(startDate, endDate);
    }
    
    /**
     * 종목코드와 기준일자 범위로 배당 정보 조회
     */
    public List<Dividend> getDividendsByTickerAndDateRange(String ticker, LocalDate startDate, LocalDate endDate) {
        return dividendRepository.findByTickerAndStndDateBetween(ticker, startDate, endDate);
    }
    
    /**
     * 배당 정보 저장
     */
    @Transactional
    public Dividend saveDividend(Dividend dividend) {
        return dividendRepository.save(dividend);
    }
    
    /**
     * 배당 정보 수정
     */
    @Transactional
    public Dividend updateDividend(Dividend dividend) {
        return dividendRepository.save(dividend);
    }
    
    /**
     * 배당 정보 삭제
     */
    @Transactional
    public void deleteDividend(String ticker, LocalDate stndDate) {
        dividendRepository.deleteByTickerAndStndDate(ticker, stndDate);
    }
    
    /**
     * 종목코드의 모든 배당 정보 삭제
     */
    @Transactional
    public void deleteDividendsByTicker(String ticker) {
        dividendRepository.deleteByTicker(ticker);
    }
}
