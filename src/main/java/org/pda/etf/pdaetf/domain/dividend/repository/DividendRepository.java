package org.pda.etf.pdaetf.domain.dividend.repository;

import org.pda.etf.pdaetf.domain.dividend.model.Dividend;
import org.pda.etf.pdaetf.domain.dividend.model.DividendId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DividendRepository extends JpaRepository<Dividend, DividendId> {
    
    /**
     * 종목코드로 배당 정보 조회
     */
    List<Dividend> findByTickerOrderByStndDateDesc(String ticker);
    
    /**
     * 종목코드와 기준일자로 배당 정보 조회
     */
    Optional<Dividend> findByTickerAndStndDate(String ticker, LocalDate stndDate);
    
    /**
     * 기준일자 범위로 배당 정보 조회
     */
    @Query("SELECT d FROM Dividend d WHERE d.stndDate BETWEEN :startDate AND :endDate ORDER BY d.ticker, d.stndDate DESC")
    List<Dividend> findByStndDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 종목코드와 기준일자 범위로 배당 정보 조회
     */
    @Query("SELECT d FROM Dividend d WHERE d.ticker = :ticker AND d.stndDate BETWEEN :startDate AND :endDate ORDER BY d.stndDate DESC")
    List<Dividend> findByTickerAndStndDateBetween(@Param("ticker") String ticker, 
                                                    @Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);
    
    /**
     * 종목코드와 기준일자로 배당 정보 삭제
     */
    void deleteByTickerAndStndDate(String ticker, LocalDate stndDate);
    
    /**
     * 종목코드의 모든 배당 정보 삭제
     */
    void deleteByTicker(String ticker);
}
