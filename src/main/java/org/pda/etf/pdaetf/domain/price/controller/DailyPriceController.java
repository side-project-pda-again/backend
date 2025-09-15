package org.pda.etf.pdaetf.domain.price.controller;

import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.common.dto.ApiResponse;
import org.pda.etf.pdaetf.domain.price.model.DailyPrice;
import org.pda.etf.pdaetf.domain.price.service.DailyPriceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/daily-prices")
@RequiredArgsConstructor
public class DailyPriceController {
    
    private final DailyPriceService dailyPriceService;
    
    /**
     * 모든 일봉 데이터 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DailyPrice>>> getAllDailyPrices() {
        List<DailyPrice> dailyPrices = dailyPriceService.getAllDailyPrices();
        return ResponseEntity.ok(ApiResponse.ok(dailyPrices));
    }
    
    /**
     * 종목코드로 일봉 데이터 조회
     */
    @GetMapping("/ticker/{ticker}")
    public ResponseEntity<ApiResponse<List<DailyPrice>>> getDailyPricesByTicker(@PathVariable String ticker) {
        List<DailyPrice> dailyPrices = dailyPriceService.getDailyPricesByTicker(ticker);
        return ResponseEntity.ok(ApiResponse.ok(dailyPrices));
    }
    
    /**
     * 종목코드와 기준일자로 일봉 데이터 조회
     */
    @GetMapping("/ticker/{ticker}/date/{stndDate}")
    public ResponseEntity<ApiResponse<DailyPrice>> getDailyPriceByTickerAndDate(
            @PathVariable String ticker,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate stndDate) {
        return dailyPriceService.getDailyPriceByTickerAndDate(ticker, stndDate)
                .map(dailyPrice -> ResponseEntity.ok(ApiResponse.ok(dailyPrice)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 기준일자 범위로 일봉 데이터 조회
     */
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<DailyPrice>>> getDailyPricesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DailyPrice> dailyPrices = dailyPriceService.getDailyPricesByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok(dailyPrices));
    }
    
    /**
     * 종목코드와 기준일자 범위로 일봉 데이터 조회
     */
    @GetMapping("/ticker/{ticker}/date-range")
    public ResponseEntity<ApiResponse<List<DailyPrice>>> getDailyPricesByTickerAndDateRange(
            @PathVariable String ticker,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DailyPrice> dailyPrices = dailyPriceService.getDailyPricesByTickerAndDateRange(ticker, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok(dailyPrices));
    }
    
    /**
     * 일봉 데이터 저장
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DailyPrice>> saveDailyPrice(@RequestBody DailyPrice dailyPrice) {
        DailyPrice savedDailyPrice = dailyPriceService.saveDailyPrice(dailyPrice);
        return ResponseEntity.ok(ApiResponse.ok(savedDailyPrice));
    }
    
    /**
     * 일봉 데이터 수정
     */
    @PutMapping
    public ResponseEntity<ApiResponse<DailyPrice>> updateDailyPrice(@RequestBody DailyPrice dailyPrice) {
        DailyPrice updatedDailyPrice = dailyPriceService.updateDailyPrice(dailyPrice);
        return ResponseEntity.ok(ApiResponse.ok(updatedDailyPrice));
    }
    
    /**
     * 일봉 데이터 삭제
     */
    @DeleteMapping("/ticker/{ticker}/date/{stndDate}")
    public ResponseEntity<ApiResponse<Void>> deleteDailyPrice(
            @PathVariable String ticker,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate stndDate) {
        dailyPriceService.deleteDailyPrice(ticker, stndDate);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
    
    /**
     * 종목코드의 모든 일봉 데이터 삭제
     */
    @DeleteMapping("/ticker/{ticker}")
    public ResponseEntity<ApiResponse<Void>> deleteDailyPricesByTicker(@PathVariable String ticker) {
        dailyPriceService.deleteDailyPricesByTicker(ticker);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
