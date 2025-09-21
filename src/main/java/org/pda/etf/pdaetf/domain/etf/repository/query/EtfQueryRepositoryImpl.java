// domain/etf/repository/query/EtfQueryRepositoryImpl.java
package org.pda.etf.pdaetf.domain.etf.repository.query;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.domain.dividend.model.QDividend;
import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.pda.etf.pdaetf.domain.etf.model.QEtf;
import org.pda.etf.pdaetf.domain.etf.model.QEtfCategoryMap;
import org.pda.etf.pdaetf.domain.price.model.QDailyPrice;
import org.pda.etf.pdaetf.domain.user.model.QFavorite;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class EtfQueryRepositoryImpl implements EtfQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final QEtf etf = QEtf.etf;
    private static final QFavorite favorite = QFavorite.favorite;
    private static final QDailyPrice dpLatest = new QDailyPrice("dpLatest");
    private static final QDailyPrice dpPrev   = new QDailyPrice("dpPrev");
    private static final QDividend div1 = QDividend.dividend;
    private static final QDividend div2 = new QDividend("div2");

    @Override
    public Page<EtfRowDto> searchEtfs(String query, Long categoryId, Long currentUserId, Pageable pageable) {
        // 베이스 where
        BooleanExpression where = Expressions.TRUE.isTrue();
        if (query != null && !query.isBlank()) {
            String like = "%" + query.toLowerCase() + "%";
            where = where.and(etf.ticker.lower().like(like).or(etf.kr_isnm.lower().like(like)));
        }
        if (categoryId != null) {
            where = where.and(
                    JPAExpressions.selectOne()
                            .from(QEtfCategoryMap.etfCategoryMap)
                            .where(
                                    QEtfCategoryMap.etfCategoryMap.etf.ticker.eq(etf.ticker),
                                    QEtfCategoryMap.etfCategoryMap.id.categoryId.eq(categoryId)
                            ).exists()
            );
        }

        // liked 표시 방식
        BooleanExpression likedExists = (currentUserId == null)
                ? Expressions.booleanTemplate("false")
                : JPAExpressions.selectOne().from(favorite)
                .where(favorite.id.userId.eq(currentUserId),
                        favorite.id.ticker.eq(etf.ticker))
                .exists();

        // 공통 쿼리 빌드 (onlyFavorites=false)
        var dataQuery = buildBaseEtfQuery(where, likedExists, false);

        applySort(dataQuery, pageable);

        var content = dataQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(etf.ticker.count())
                .from(etf)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

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
        var dataQuery = buildBaseEtfQuery(where, likedExpr, true);

        applySort(dataQuery, pageable);

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

    /** 공통 SELECT/서브쿼리/파생값/조인 전략 */
    private JPAQuery<EtfRowDto> buildBaseEtfQuery(BooleanExpression where,
                                                  BooleanExpression likedExpr,
                                                  boolean onlyFavorites) {
        // 최신/전일 일자
        SubQueryExpression<LocalDate> latestDateSub =
                JPAExpressions.select(dpLatest.stndDate.max())
                        .from(dpLatest)
                        .where(dpLatest.ticker.eq(etf.ticker));

        SubQueryExpression<LocalDate> prevDateSub =
                JPAExpressions.select(dpPrev.stndDate.max())
                        .from(dpPrev)
                        .where(dpPrev.ticker.eq(etf.ticker), dpPrev.stndDate.lt(latestDateSub));

        // 최신 배당
        SubQueryExpression<LocalDate> latestDivDateSub =
                JPAExpressions.select(div1.stndDate.max())
                        .from(div1)
                        .where(div1.ticker.eq(etf.ticker));

        SubQueryExpression<BigDecimal> latestDivAmountSub =
                JPAExpressions.select(div2.dividendAmount)
                        .from(div2)
                        .where(div2.ticker.eq(etf.ticker), div2.stndDate.eq(latestDivDateSub));

        // 조인 조건
        BooleanExpression joinLatest = dpLatest.ticker.eq(etf.ticker)
                .and(dpLatest.stndDate.eq(latestDateSub));
        BooleanExpression joinPrev = dpPrev.ticker.eq(etf.ticker)
                .and(dpPrev.stndDate.eq(prevDateSub));

        // 파생값
        NumberExpression<BigDecimal> change =
                Expressions.numberTemplate(BigDecimal.class,
                        "CASE WHEN {0} IS NULL OR {1} IS NULL THEN NULL ELSE ({0} - {1}) END",
                        dpLatest.close, dpPrev.close);
        NumberExpression<BigDecimal> changePct =
                Expressions.numberTemplate(BigDecimal.class,
                        "CASE WHEN {0} IS NULL OR {0} = 0 THEN NULL ELSE (({1} / {0}) * 100) END",
                        dpPrev.close, change);

        // FROM/JOIN 구성 (favorites만 조회 시에는 조인 강제)
        var from = queryFactory
                .select(Projections.fields(
                        EtfRowDto.class,
                        etf.ticker.as("ticker"),
                        etf.kr_isnm.as("krIsnm"),
                        etf.market.as("market"),
                        dpLatest.stndDate.as("stndDate"),
                        dpLatest.close.as("latestPrice"),
                        dpPrev.close.as("prevClose"),
                        change.as("change"),
                        changePct.as("changePct"),
                        dpLatest.volume.as("volume"),
                        ExpressionUtils.as(latestDivDateSub, "latestDividendDate"),
                        ExpressionUtils.as(latestDivAmountSub, "latestDividendAmount"),
                        Expressions.booleanTemplate("{0}", likedExpr).as("liked")
                ))
                .from(etf);

        if (onlyFavorites) {
            from.join(favorite).on(favorite.id.ticker.eq(etf.ticker));
        }

        return from
                .leftJoin(dpLatest).on(joinLatest)
                .leftJoin(dpPrev).on(joinPrev)
                .where(where);
    }

    /** 공통 정렬(배당 nullsLast 포함) + tie-breaker */
    private void applySort(JPAQuery<?> query, Pageable pageable) {
        for (Sort.Order o : pageable.getSort()) {
            Order dir = o.isAscending() ? Order.ASC : Order.DESC;
            switch (o.getProperty()) {
                case "ticker"               -> query.orderBy(new OrderSpecifier<>(dir, etf.ticker));
                case "krIsnm"               -> query.orderBy(new OrderSpecifier<>(dir, etf.kr_isnm));
                case "market"               -> query.orderBy(new OrderSpecifier<>(dir, etf.market));
                case "stndDate"             -> query.orderBy(new OrderSpecifier<>(dir, dpLatest.stndDate));
                case "latestPrice"          -> query.orderBy(new OrderSpecifier<>(dir, dpLatest.close));
                case "change"               -> {
                    NumberExpression<BigDecimal> ch =
                            Expressions.numberTemplate(BigDecimal.class, "({0} - {1})", dpLatest.close, dpPrev.close);
                    query.orderBy(new OrderSpecifier<>(dir, ch));
                }
                case "volume"               -> query.orderBy(new OrderSpecifier<>(dir, dpLatest.volume));
                case "latestDividendDate"   -> query.orderBy(new OrderSpecifier<>(dir,
                        JPAExpressions.select(div1.stndDate.max()).from(div1).where(div1.ticker.eq(etf.ticker))
                ).nullsLast());
                case "latestDividendAmount" -> query.orderBy(new OrderSpecifier<>(dir,
                        JPAExpressions.select(div2.dividendAmount)
                                .from(div2)
                                .where(div2.ticker.eq(etf.ticker),
                                        div2.stndDate.eq(
                                                JPAExpressions.select(div1.stndDate.max()).from(div1).where(div1.ticker.eq(etf.ticker))
                                        ))
                ).nullsLast());
                default -> { /* ignore */ }
            }
        }
        query.orderBy(new OrderSpecifier<>(Order.ASC, etf.ticker));
    }
}

