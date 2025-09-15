package org.pda.etf.pdaetf.domain.dividend.controller;

import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.common.dto.ApiResponse;
import org.pda.etf.pdaetf.domain.dividend.model.Dividend;
import org.pda.etf.pdaetf.domain.dividend.service.DividendService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dividends")
@RequiredArgsConstructor
public class DividendController {
    
    private final DividendService dividendService;
    
    /**
     * 모든 배당 정보 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Dividend>>> getAllDividends() {
        List<Dividend> dividends = dividendService.getAllDividends();
        return ResponseEntity.ok(ApiResponse.ok(dividends));
    }
    
    /**
     * 종목코드로 배당 정보 조회
     */
    @GetMapping("/ticker/{ticker}")
    public ResponseEntity<ApiResponse<List<Dividend>>> getDividendsByTicker(@PathVariable String ticker) {
        List<Dividend> dividends = dividendService.getDividendsByTicker(ticker);
        return ResponseEntity.ok(ApiResponse.ok(dividends));
    }
    
    /**
     * 종목코드와 기준일자로 배당 정보 조회
     */
    @GetMapping("/ticker/{ticker}/date/{stndDate}")
    public ResponseEntity<ApiResponse<Dividend>> getDividendByTickerAndDate(
            @PathVariable String ticker,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate stndDate) {
        return dividendService.getDividendByTickerAndDate(ticker, stndDate)
                .map(dividend -> ResponseEntity.ok(ApiResponse.ok(dividend)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 기준일자 범위로 배당 정보 조회
     */
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<Dividend>>> getDividendsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Dividend> dividends = dividendService.getDividendsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok(dividends));
    }
    
    /**
     * 종목코드와 기준일자 범위로 배당 정보 조회
     */
    @GetMapping("/ticker/{ticker}/date-range")
    public ResponseEntity<ApiResponse<List<Dividend>>> getDividendsByTickerAndDateRange(
            @PathVariable String ticker,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Dividend> dividends = dividendService.getDividendsByTickerAndDateRange(ticker, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok(dividends));
    }
    
    /**
     * 배당 정보 저장
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Dividend>> saveDividend(@RequestBody Dividend dividend) {
        Dividend savedDividend = dividendService.saveDividend(dividend);
        return ResponseEntity.ok(ApiResponse.ok(savedDividend));
    }
    
    /**
     * 배당 정보 수정
     */
    @PutMapping
    public ResponseEntity<ApiResponse<Dividend>> updateDividend(@RequestBody Dividend dividend) {
        Dividend updatedDividend = dividendService.updateDividend(dividend);
        return ResponseEntity.ok(ApiResponse.ok(updatedDividend));
    }
    
    /**
     * 배당 정보 삭제
     */
    @DeleteMapping("/ticker/{ticker}/date/{stndDate}")
    public ResponseEntity<ApiResponse<Void>> deleteDividend(
            @PathVariable String ticker,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate stndDate) {
        dividendService.deleteDividend(ticker, stndDate);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
    
    /**
     * 종목코드의 모든 배당 정보 삭제
     */
    @DeleteMapping("/ticker/{ticker}")
    public ResponseEntity<ApiResponse<Void>> deleteDividendsByTicker(@PathVariable String ticker) {
        dividendService.deleteDividendsByTicker(ticker);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
