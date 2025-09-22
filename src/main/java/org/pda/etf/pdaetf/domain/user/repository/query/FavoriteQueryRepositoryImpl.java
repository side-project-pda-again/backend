package org.pda.etf.pdaetf.domain.user.repository.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.domain.dividend.model.QDividend;
import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.pda.etf.pdaetf.domain.etf.model.QEtf;
import org.pda.etf.pdaetf.domain.etf.repository.query.EtfQueryRepository;
import org.pda.etf.pdaetf.domain.price.model.QDailyPrice;
import org.pda.etf.pdaetf.domain.user.model.QFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FavoriteQueryRepositoryImpl implements FavoriteQueryRepository{

    private final JPAQueryFactory queryFactory;
    private final EtfQueryRepository etfQueryRepository;

    private static final QEtf etf = QEtf.etf;
    private static final QFavorite favorite = QFavorite.favorite;
    private static final QDailyPrice dpLatest = new QDailyPrice("dpLatest");
    private static final QDailyPrice dpPrev   = new QDailyPrice("dpPrev");
    private static final QDividend div1 = QDividend.dividend;
    private static final QDividend div2 = new QDividend("div2");

    @Override
    public Page<EtfRowDto> findFavoriteEtfs(Long userId, String query, Pageable pageable) {
        if (userId == null) return Page.empty(pageable);

        // 즐겨찾기 where
        BooleanExpression where = favorite.id.userId.eq(userId)
                .and(favorite.id.ticker.eq(etf.ticker));

        // (옵션) 키워드
        if (query != null && !query.isBlank()) {
            String like = "%" + query.toLowerCase() + "%";
            where = where.and(etf.ticker.lower().like(like).or(etf.kr_isnm.lower().like(like)));
        }

        // liked는 true 고정
        BooleanExpression likedExpr = Expressions.booleanTemplate("true");

        // 공통 쿼리 빌드 (onlyFavorites=true → favorite 조인)
        var dataQuery = etfQueryRepository.buildBaseEtfQuery(where, likedExpr, true);

        etfQueryRepository.applySort(dataQuery, pageable);

        var content = dataQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(etf.ticker.count())
                .from(etf)
                .join(favorite).on(favorite.id.ticker.eq(etf.ticker))
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
