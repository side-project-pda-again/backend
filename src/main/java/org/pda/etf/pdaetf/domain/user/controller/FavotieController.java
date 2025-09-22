package org.pda.etf.pdaetf.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pda.etf.pdaetf.common.dto.ApiResponse;
import org.pda.etf.pdaetf.common.exception.ApiException;
import org.pda.etf.pdaetf.common.exception.ErrorCode;
import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.pda.etf.pdaetf.domain.etf.dto.ReturnEtfSearchDto;
import org.pda.etf.pdaetf.domain.user.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavotieController {

    private final FavoriteService favoriteService;
    private static final Set<String> ALLOWED_SORT_KEYS = Set.of(
            "ticker","krIsnm","market","stndDate","latestPrice","change","volume",
            "latestDividendDate","latestDividendAmount"
    );

    /**
     * 유저의 즐겨찾기한 종목 조회
     * @param userId
     * @param query ticker, 종목명 검색
     * @param pageable
     * @return 즐겨찾기한 종목 검색 결과
     * TODO: userId param이 아닌 인증 정보로 받기
     */
    @GetMapping("")
    public ResponseEntity<ApiResponse<ReturnEtfSearchDto>> getFavorites(
            @RequestParam Long userId,
            @RequestParam(required = false) String query,
            @PageableDefault(size=20, sort="ticker", direction = Sort.Direction.ASC) Pageable pageable
    ){
        if(pageable.getPageNumber() < 0){
            log.error("즐겨찾기 조회 실패 - page가 0 미만임, page: {}", pageable.getPageNumber());
            throw new ApiException(ErrorCode.INVALID_INPUT, "page는 0 이상이어야 합니다.");
        }
        for (Sort.Order o : pageable.getSort()) {
            String key = o.getProperty();
            if (!ALLOWED_SORT_KEYS.contains(key)) {
                log.error("즐겨찾기 조회 실패 - 지원하지 않는 , page: {}", pageable.getPageNumber());
                throw new ApiException(ErrorCode.INVALID_INPUT, "지원하지 않는 정렬 키: " + key);
            }
        }
        Page<EtfRowDto> page = favoriteService.findFavoriteEtfs(userId, query, pageable);

        ReturnEtfSearchDto body = ReturnEtfSearchDto.builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .sort(pageable.getSort().toString())
                .build();

        return ResponseEntity.ok(ApiResponse.ok(body));
    }


    /**
     * 종목 즐겨찾기 추가
     * @param userId
     * @param ticker
     * @return 추가 성공 여부
     */
    @PostMapping("/{userId}/{ticker}")
    public ResponseEntity<ApiResponse<Void>> add(@PathVariable Long userId, @PathVariable String ticker){
        favoriteService.addFavorite(userId, ticker);
        return ResponseEntity.ok(ApiResponse.ok(null, "즐겨찾기에 추가되었습니다."));
    }

    /**
     * 종목 즐겨찾기 삭제
     * @param userId
     * @param ticker
     * @return 삭제 성공 여부
     */
    @DeleteMapping("/{userId}/{ticker}")
    public ResponseEntity<ApiResponse<Void>> remove(@PathVariable Long userId, @PathVariable String ticker){
        favoriteService.removeFavorite(userId, ticker);
        return ResponseEntity.ok(ApiResponse.ok(null, "즐겨찾기에서 삭제되었습니다."));
    }
}
