package org.pda.etf.pdaetf.domain.user.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.common.exception.ApiException;
import org.pda.etf.pdaetf.common.exception.ErrorCode;
import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.pda.etf.pdaetf.domain.etf.model.Etf;
import org.pda.etf.pdaetf.domain.etf.model.QEtf;
import org.pda.etf.pdaetf.domain.etf.repository.EtfRepository;
import org.pda.etf.pdaetf.domain.user.model.Favorite;
import org.pda.etf.pdaetf.domain.user.model.FavoriteId;
import org.pda.etf.pdaetf.domain.user.model.User;
import org.pda.etf.pdaetf.domain.user.repository.FavoriteRepository;
import org.pda.etf.pdaetf.domain.user.repository.UserRepository;
import org.pda.etf.pdaetf.domain.user.repository.query.FavoriteQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteQueryRepository favoriteQueryRepository;
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


    /**
     * 즐겨찾기한 ETF 조회
     */
    @Transactional
    public Page<EtfRowDto> findFavoriteEtfs(Long userId, String query, Pageable pageable) {
        BooleanExpression where = Expressions.TRUE.isTrue();

        if(query != null && !query.isBlank()){
            String like = "%" + query.trim().toLowerCase() + "%";
            where = where.and(
                    QEtf.etf.ticker.lower().like(like)
                            .or(QEtf.etf.kr_isnm.lower().like(like))
            );
        }

        return favoriteQueryRepository.findFavoriteEtfs(where, userId, pageable);
    }
}
