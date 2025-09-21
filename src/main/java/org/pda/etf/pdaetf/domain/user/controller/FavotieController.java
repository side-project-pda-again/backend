package org.pda.etf.pdaetf.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pda.etf.pdaetf.common.dto.ApiResponse;
import org.pda.etf.pdaetf.domain.user.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavotieController {

    private final FavoriteService favoriteService;

    @PostMapping("/{userId}/{ticker}")
    public ResponseEntity<ApiResponse<Void>> add(@PathVariable Long userId, @PathVariable String ticker){
        favoriteService.addFavorite(userId, ticker);
        return ResponseEntity.ok(ApiResponse.ok(null, "즐겨찾기에 추가되었습니다."));
    }

    @DeleteMapping("/{userId}/{ticker}")
    public ResponseEntity<ApiResponse<Void>> remove(@PathVariable Long userId, @PathVariable String ticker){
        favoriteService.removeFavorite(userId, ticker);
        return ResponseEntity.ok(ApiResponse.ok(null, "즐겨찾기에서 삭제되었습니다."));
    }
}
