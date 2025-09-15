package org.pda.etf.pdaetf.domain.etf.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pda.etf.pdaetf.common.dto.ApiResponse;
import org.pda.etf.pdaetf.common.dto.ReturnCalculationDto;
import org.pda.etf.pdaetf.common.exception.ApiException;
import org.pda.etf.pdaetf.common.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.pda.etf.pdaetf.domain.etf.model.Etf;
import org.pda.etf.pdaetf.domain.etf.service.EtfService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/etfs")
public class EtfController {

    private final EtfService etfService;

    @GetMapping("/{ticker}")
    public ResponseEntity<ApiResponse<Etf>> getOne(@PathVariable String ticker) {
        Etf etf = etfService.findByTicker(ticker);
        return ResponseEntity.ok(ApiResponse.ok(etf));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Etf>> create(@RequestBody Etf etf) {
        Etf saved = etfService.save(etf);
        return ResponseEntity
                .status(201) // 201 Created
                .body(ApiResponse.ok(saved, "ETF가 생성되었습니다."));
    }

    @DeleteMapping("/{ticker}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String ticker) {
        etfService.delete(ticker);
        return ResponseEntity
                .noContent() // 204
                .build();
    }

    /**
     * 종목의 수익률 계산
     * @param ticker 종목코드
     * @param startDate 시작일 (YYYYMMDD)
     * @param endDate 종료일 (YYYYMMDD)
     * @return 수익률 계산 결과
     */
    @GetMapping("/{ticker}/return-calculation")
    public ResponseEntity<ApiResponse<ReturnCalculationDto>> calculateReturn(
            @PathVariable String ticker,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("API 호출 - GET /api/etfs/{}/return-calculation, params[ticker={}, startDate={}, endDate={}]",
                ticker, ticker, startDate, endDate);

        // 입력값 검증
        // ticker 검증
        if (ticker == null || ticker.trim().isEmpty()) {
            log.error("수익률 계산 실패 - ticker가 비어있음");
            throw new ApiException(ErrorCode.INVALID_INPUT, "종목코드(ticker)는 필수입니다.");
        }
        
        // startDate 검증
        if (startDate == null || startDate.trim().isEmpty()) {
            log.error("수익률 계산 실패 - startDate가 비어있음, ticker: {}", ticker);
            throw new ApiException(ErrorCode.INVALID_INPUT, "시작일(startDate)은 필수입니다.");
        }
        
        // endDate 검증
        if (endDate == null || endDate.trim().isEmpty()) {
            log.error("수익률 계산 실패 - endDate가 비어있음, ticker: {}", ticker);
            throw new ApiException(ErrorCode.INVALID_INPUT, "종료일(endDate)은 필수입니다.");
        }
        
        try {
            ReturnCalculationDto result = etfService.calculateReturn(ticker, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (Exception e) {
            StackTraceElement origin = e.getStackTrace().length > 0 ? e.getStackTrace()[0] : null;
            String location = origin == null ? "<unknown>" : String.format("%s.%s(%s:%d)",
                    origin.getClassName(), origin.getMethodName(), origin.getFileName(), origin.getLineNumber());
            log.error("수익률 계산 실패 - location={}, params[ticker={}, startDate={}, endDate={}], message={}",
                    location, ticker, startDate, endDate, e.getMessage(), e);
            throw e;
        } finally {
            log.info("API 완료 - GET /api/etfs/{}/return-calculation,", ticker);
        }
    }
}