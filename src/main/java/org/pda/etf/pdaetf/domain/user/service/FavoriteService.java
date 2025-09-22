package org.pda.etf.pdaetf.domain.user.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.common.exception.ApiException;
import org.pda.etf.pdaetf.common.exception.ErrorCode;
import org.pda.etf.pdaetf.domain.etf.model.Etf;
import org.pda.etf.pdaetf.domain.etf.repository.EtfRepository;
import org.pda.etf.pdaetf.domain.user.model.Favorite;
import org.pda.etf.pdaetf.domain.user.model.FavoriteId;
import org.pda.etf.pdaetf.domain.user.model.User;
import org.pda.etf.pdaetf.domain.user.repository.FavoriteRepository;
import org.pda.etf.pdaetf.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final EtfRepository etfRepository;
    private final EntityManager em;

    @Transactional
    public void addFavorite(Long userId, String ticker){
        if(userId == null || ticker == null || ticker.isBlank()){
            throw new ApiException(ErrorCode.INVALID_INPUT, "userId와 ticker는 필수입니다.");
        }
        if(!userRepository.existsById(userId)){
            throw new ApiException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        if(!etfRepository.existsByTicker(ticker)){
            throw new ApiException(ErrorCode.NOT_FOUND, "종목을 찾을 수 없습니다.");
        }

        FavoriteId id = new FavoriteId(userId, ticker);
        if(favoriteRepository.existsById(id)){ //이미 즐겨찾기한 경우 조용히 성공 처리
            return;
        }

        Favorite fav = new Favorite();
        fav.setId(new FavoriteId());
        fav.setUser(em.getReference(User.class, userId));
        fav.setEtf(em.getReference(Etf.class, ticker));
        fav.setCreatedAt(LocalDateTime.now());
        favoriteRepository.save(fav);
    }

    @Transactional
    public void removeFavorite(Long userId, String ticker){
        if(userId == null || ticker == null || ticker.isBlank()){
            throw new ApiException(ErrorCode.INVALID_INPUT, "userId와 ticker는 필수입니다.");
        }
        if(!userRepository.existsById(userId)){
            throw new ApiException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        if(!etfRepository.existsByTicker(ticker)){
            throw new ApiException(ErrorCode.NOT_FOUND, "종목을 찾을 수 없습니다.");
        }

        FavoriteId id = new FavoriteId(userId, ticker);
        if(!favoriteRepository.existsById(id)){
            return;
        }
        favoriteRepository.deleteById(id);
    }
}
