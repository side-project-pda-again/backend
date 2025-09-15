package org.pda.etf.pdaetf.domain.price.repository;

import org.pda.etf.pdaetf.domain.price.model.DailyPrice;
import org.pda.etf.pdaetf.domain.price.model.DailyPriceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyPriceRepository extends JpaRepository<DailyPrice, DailyPriceId> {
    
    /**
     * 종목코드로 일봉 데이터 조회
     */
    List<DailyPrice> findByTickerOrderByStndDateDesc(String ticker);
    
    /**
     * 종목코드와 기준일자로 일봉 데이터 조회
     */
    Optional<DailyPrice> findByTickerAndStndDate(String ticker, LocalDate stndDate);
    
    /**
     * 기준일자 범위로 일봉 데이터 조회
     */
    @Query("SELECT d FROM DailyPrice d WHERE d.stndDate BETWEEN :startDate AND :endDate ORDER BY d.ticker, d.stndDate DESC")
    List<DailyPrice> findByStndDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 종목코드와 기준일자 범위로 일봉 데이터 조회
     */
    @Query("SELECT d FROM DailyPrice d WHERE d.ticker = :ticker AND d.stndDate BETWEEN :startDate AND :endDate ORDER BY d.stndDate DESC")
    List<DailyPrice> findByTickerAndStndDateBetween(@Param("ticker") String ticker, 
                                                    @Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);
    
    /**
     * 종목코드와 기준일자로 일봉 데이터 삭제
     */
    void deleteByTickerAndStndDate(String ticker, LocalDate stndDate);
    
    /**
     * 종목코드의 모든 일봉 데이터 삭제
     */
    void deleteByTicker(String ticker);

}
