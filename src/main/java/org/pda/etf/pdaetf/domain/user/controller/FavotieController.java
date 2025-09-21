package org.pda.etf.pdaetf.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pda.etf.pdaetf.common.dto.ApiResponse;
import org.pda.etf.pdaetf.domain.user.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
