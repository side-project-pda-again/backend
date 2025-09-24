package org.pda.etf.pdaetf.domain.Portfolio.controller;

import lombok.RequiredArgsConstructor;

import org.pda.etf.pdaetf.common.dto.ApiResponse;
import org.pda.etf.pdaetf.common.exception.ApiException;
import org.pda.etf.pdaetf.common.exception.ErrorCode;
import org.pda.etf.pdaetf.domain.Portfolio.service.PortfolioService;
import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.pda.etf.pdaetf.domain.etf.dto.ReturnEtfSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    /**
     * 특정 포트폴리오에 종목 추가
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


    /**
     * 특정 포트폴리오의 종목 삭제
     * @param portfolioId
     * @param ticker
     * @param userId
     * @return 삭제 성공 여부
     */
    @DeleteMapping("/{portfolioId}/items")
    public ResponseEntity<ApiResponse<Void>> removeItem(
            @PathVariable Long portfolioId,
            @RequestParam String ticker,
            @RequestParam Long userId
    ){
        if(ticker == null || ticker.isBlank()){
            throw new ApiException(ErrorCode.INVALID_INPUT, "ticker는 필수입니다.");
        }
        if(userId == null){
            throw new ApiException(ErrorCode.INVALID_INPUT, "userId는 필수입니다.");
        }
        portfolioService.removeEtf(userId, portfolioId, ticker);
        return ResponseEntity.ok(ApiResponse.ok(null, "포트폴리오에서 종목이 삭제되었습니다."));
    }


    /**
     * 특정 포트폴리오의 종목 조회
     * @param portfolioId
     * @param userId
     * @param pageable
     * @param sortParam
     * @return 포트폴리오 내 종목 결과
     */
    @GetMapping("/{portfolioId}/items")
    public ResponseEntity<ApiResponse<ReturnEtfSearchDto>> listsEtfs(
            @PathVariable Long portfolioId,
            @RequestParam Long userId,
            @PageableDefault(size=20, sort="ticker", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(name="sort", required = false, defaultValue = "ticker,asc") String sortParam
    ){
        Page<EtfRowDto> page = portfolioService.findEtfsInPortfolio(userId, portfolioId, pageable);

        var body = ReturnEtfSearchDto.builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .sort(sortParam)
                .build();

        return ResponseEntity.ok(ApiResponse.ok(body));
    }
}
