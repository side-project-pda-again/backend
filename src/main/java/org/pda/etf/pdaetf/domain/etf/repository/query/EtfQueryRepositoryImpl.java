// domain/etf/repository/query/EtfQueryRepositoryImpl.java
package org.pda.etf.pdaetf.domain.etf.repository.query;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
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

    // === Q타입 준비 (QueryDSL이 생성)
    private static final QEtf etf = QEtf.etf;
    private static final QEtfCategoryMap map = QEtfCategoryMap.etfCategoryMap;

    // 같은 테이블을 최신/전일 용도로 두 번 조인해야 하므로 별칭 사용
    private static final QDailyPrice dpLatest = new QDailyPrice("dpLatest");
    private static final QDailyPrice dpPrev   = new QDailyPrice("dpPrev");

    private static final QFavorite favorite = QFavorite.favorite;

    private static final QDividend div1 = QDividend.dividend;
    private static final QDividend div2 = new QDividend("div2"); //금액 조회용

    @Override
    public Page<EtfRowDto> searchEtfs(String query, Long categoryId, Long currentUserId, Pageable pageable) {

        // 1) 동적 WHERE
        BooleanExpression where = Expressions.TRUE.isTrue();
        if (query != null && !query.isBlank()) {
            String like = "%" + query.toLowerCase() + "%";
            where = where.and(
                    etf.ticker.lower().like(like)
                            .or(etf.kr_isnm.lower().like(like))
            );
        }
        if (categoryId != null) {
            where = where.and(
                    JPAExpressions.selectOne()
                            .from(map)
                            .where(
                                    map.etf.ticker.eq(etf.ticker),
                                    map.id.categoryId.eq(categoryId)
                            ).exists()
            );
        }

        // 2) “해당 티커의 최신 영업일” 서브쿼리
        SubQueryExpression<LocalDate> latestDateSub =
                JPAExpressions.select(dpLatest.stndDate.max())
                        .from(dpLatest)
                        .where(dpLatest.ticker.eq(etf.ticker));

        // 3) 최신가 조인: ticker 동일 + stndDate = 최신일자
        BooleanExpression joinLatest =
                dpLatest.ticker.eq(etf.ticker)
                        .and(dpLatest.stndDate.eq(latestDateSub));

        // 4) 전일가 서브쿼리 & 조인: 최신일자보다 작은 값 중 MAX
        SubQueryExpression<java.time.LocalDate> prevDateSub =
                JPAExpressions.select(dpPrev.stndDate.max())
                        .from(dpPrev)
                        .where(
                                dpPrev.ticker.eq(etf.ticker),
                                dpPrev.stndDate.lt(latestDateSub)
                        );

        BooleanExpression joinPrev =
                dpPrev.ticker.eq(etf.ticker)
                        .and(dpPrev.stndDate.eq(prevDateSub));

        // 5) 즐겨찾기 여부 EXISTS
        BooleanExpression likedExists =
                (currentUserId == null)
                        ? Expressions.FALSE.isTrue()
                        : JPAExpressions.selectOne()
                        .from(favorite)
                        .where(
                                favorite.id.userId.eq(currentUserId),
                                favorite.id.ticker.eq(etf.ticker)
                        ).exists();

        // 6) 최신 배당일자 + 금액
        SubQueryExpression<LocalDate> latestDivDateSub =
                JPAExpressions.select(div1.stndDate.max())
                        .from(div1)
                        .where(div1.ticker.eq(etf.ticker));
        SubQueryExpression<BigDecimal> latestDivAmountSub =
                JPAExpressions.select(div2.dividendAmount)
                        .from(div2)
                        .where(div2.ticker.eq(etf.ticker), div2.stndDate.eq(latestDivDateSub));

        // 6) 파생값(변동/변동률) 표현식
        NumberExpression<BigDecimal> latestPrice = dpLatest.close; // not null @Column(nullable=false)
        NumberExpression<BigDecimal> prevClose   = dpPrev.close;   // null 가능

        NumberExpression<BigDecimal> change =
                Expressions.numberTemplate(BigDecimal.class,
                        "CASE WHEN {0} IS NULL OR {1} IS NULL THEN NULL ELSE ({0} - {1}) END",
                        latestPrice, prevClose);

        NumberExpression<BigDecimal> changePct =
                Expressions.numberTemplate(BigDecimal.class,
                        "CASE WHEN {0} IS NULL OR {0} = 0 THEN NULL ELSE (({1} / {0}) * 100) END",
                        prevClose, change);

        // 7) 데이터 쿼리 (프로젝션: 인터페이스 매칭을 위해 alias 중요)
        var dataQuery = queryFactory
                .select(Projections.fields(
                        EtfRowDto.class,
                        etf.ticker.as("ticker"),
                        etf.kr_isnm.as("krIsnm"),
                        etf.market.as("market"),
                        dpLatest.stndDate.as("stndDate"),
                        latestPrice.as("latestPrice"),
                        dpPrev.close.as("prevClose"),
                        change.as("change"),
                        changePct.as("changePct"),
                        Expressions.booleanTemplate("{0}", likedExists).as("liked"),
                        ExpressionUtils.as(latestDivDateSub, "latestDividendDate"), //최신 배당일
                        ExpressionUtils.as(latestDivAmountSub, "latestDividendAmount") //최신 배당금
                ))
                .from(etf)
                .leftJoin(dpLatest).on(joinLatest)
                .leftJoin(dpPrev).on(joinPrev)
                .where(where);

        // 8) 정렬 적용 (화이트리스트)
        applySort(dataQuery, pageable, likedExists);

        // 9) 페이징+조회
        var content = dataQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 10) 카운트
        long total = queryFactory
                .select(etf.ticker.count())
                .from(etf)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private void applySort(com.querydsl.jpa.impl.JPAQuery<?> query,
                           Pageable pageable,
                           BooleanExpression likedExists) {

        // (옵션) 즐겨찾기 우선
        NumberExpression<Integer> likedScore =
                Expressions.numberTemplate(Integer.class, "CASE WHEN {0} THEN 1 ELSE 0 END", likedExists);
        query.orderBy(new OrderSpecifier<>(Order.DESC, likedScore));

        // Pageable sort → 화이트리스트 매핑
        for (Sort.Order o : pageable.getSort()) {
            Order dir = o.isAscending() ? Order.ASC : Order.DESC;
            switch (o.getProperty()) {
                case "ticker"      -> query.orderBy(new OrderSpecifier<>(dir, etf.ticker));
                case "krIsnm"      -> query.orderBy(new OrderSpecifier<>(dir, etf.kr_isnm));
                case "market"      -> query.orderBy(new OrderSpecifier<>(dir, etf.market));
                case "stndDate"    -> query.orderBy(new OrderSpecifier<>(dir, dpLatest.stndDate));
                case "latestPrice" -> query.orderBy(new OrderSpecifier<>(dir, dpLatest.close));
                case "change"      -> {
                    NumberExpression<BigDecimal> ch =
                            Expressions.numberTemplate(BigDecimal.class, "({0} - {1})", dpLatest.close, dpPrev.close);
                    query.orderBy(new OrderSpecifier<>(dir, ch));
                }
                default -> { /* 알 수 없는 속성은 무시 */ }
            }
        }

        // tie-breaker
        query.orderBy(new OrderSpecifier<>(Order.ASC, etf.ticker));
    }
}

