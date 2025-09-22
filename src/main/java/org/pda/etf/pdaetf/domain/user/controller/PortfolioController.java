package org.pda.etf.pdaetf.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.common.dto.ApiResponse;
import org.pda.etf.pdaetf.common.exception.ApiException;
import org.pda.etf.pdaetf.common.exception.ErrorCode;
import org.pda.etf.pdaetf.domain.user.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    /**
     * 특정 그룹에 종목 추가
     * @param portfolioId
     * @param userId
     * @param ticker
     * @return 추가 성공 여부
     */
    @PostMapping("/{portfolioId}/items")
    public ResponseEntity<ApiResponse<Void>> addItem(
            @PathVariable Long portfolioId,
            @RequestParam Long userId,
            @RequestParam String ticker
    ){
        if(ticker == null || ticker.isBlank()){
            throw new ApiException(ErrorCode.INVALID_INPUT, "ticker는 필수입니다.");
        }
        if(userId == null){
            throw new ApiException(ErrorCode.INVALID_INPUT, "userId는 필수입니다.");
        }

        portfolioService.addEtf(userId, portfolioId, ticker);
        return ResponseEntity.ok(ApiResponse.ok(null, "포트폴리오에 종목이 추가되었습니다."));
    }


    @DeleteMapping("/{portfolioId}/items/{ticker}")
    public ResponseEntity<ApiResponse<Void>> removeItem(
            @PathVariable Long portfolioId,
            @PathVariable String ticker,
            @RequestParam Long userId
    ){
        portfolioService.removeEtf(userId, portfolioId, ticker);
        return ResponseEntity.ok(ApiResponse.ok(null, "포트폴리오에서 종목이 삭제되었습니다."));
    }
}
