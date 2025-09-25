package org.pda.etf.pdaetf.domain.etf.repository.query;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions; // ✅ 추가
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.domain.dividend.repository.query.DividendQueryRepository;
import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.pda.etf.pdaetf.domain.etf.model.QEtf;
import org.pda.etf.pdaetf.domain.price.model.QDailyPrice;
import org.pda.etf.pdaetf.domain.price.repository.query.DailyPriceQueryRepository;
import org.pda.etf.pdaetf.domain.user.model.QFavorite;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class EtfRowQueryBuilder {

    private final JPAQueryFactory qf;
    private final DailyPriceQueryRepository dailyPriceQ;
    private final DividendQueryRepository dividendQ;

    private static final QEtf etf = QEtf.etf;
    private static final QFavorite fav = QFavorite.favorite;
    private static final QDailyPrice dpL = new QDailyPrice("dpL");
    private static final QDailyPrice dpP = new QDailyPrice("dpP");

    public JPAQuery<EtfRowDto> base(BooleanExpression where, Long userId, boolean onlyFavorites){

        // 최신/전일/배당 서브 쿼리
        var latestDateSub = dailyPriceQ.latestDateSubQ(etf.ticker);
        var prevDateSub   = dailyPriceQ.prevDateSubQ(etf.ticker, latestDateSub);
        var latestDivDate = dividendQ.latestDividendDateSubQ(etf.ticker);
        var latestDivAmt  = dividendQ.latestDividendAmountOnDateSubQ(etf.ticker, latestDivDate);

        Expression<Boolean> likedExpr = (userId == null)
                ? Expressions.FALSE
                : JPAExpressions.selectOne()
                .from(fav)
                .where(
                        fav.user.userId.eq(userId),
                        fav.etf.eq(etf)
                )
                .exists();

        var query = qf.select(Projections.fields(
                        EtfRowDto.class,
                        etf.ticker.as("ticker"),
                        etf.kr_isnm.as("krIsnm"),
                        etf.market.as("market"),
                        dpL.stndDate.as("stndDate"),
                        dpL.close.as("latestPrice"),
                        dpP.close.as("prevClose"),
                        // 파생값(변동, 변동률)
                        Expressions.numberTemplate(
                                BigDecimal.class,
                                "CASE WHEN {0} IS NULL OR {1} IS NULL THEN NULL ELSE ({0}-{1}) END",
                                dpL.close, dpP.close
                        ).as("change"),
                        Expressions.numberTemplate(
                                BigDecimal.class,
                                "CASE WHEN {0} IS NULL OR {0}=0 THEN NULL ELSE (({1}/{0})*100) END",
                                dpP.close,
                                Expressions.numberTemplate(BigDecimal.class, "({0}-{1})", dpL.close, dpP.close)
                        ).as("changePct"),
                        dpL.volume.as("volume"),
                        // ✅ 오타 수정: latestDividendDate
                        ExpressionUtils.as(latestDivDate, "latestDividendDate"),
                        ExpressionUtils.as(latestDivAmt,  "latestDividendAmount"),
                        ((BooleanExpression) likedExpr).as("liked")
                ))
                .from(etf)
                .leftJoin(dpL).on(dpL.ticker.eq(etf.ticker).and(dpL.stndDate.eq(latestDateSub)))
                .leftJoin(dpP).on(dpP.ticker.eq(etf.ticker).and(dpP.stndDate.eq(prevDateSub)))
                .where(where);

        // ✅ 즐겨찾기 전용(행 제한)일 때만 favorite JOIN
        if (Boolean.TRUE.equals(onlyFavorites) && userId != null) {
            query.join(fav).on(
                    fav.user.userId.eq(userId)
                            .and(fav.etf.eq(etf)) // ticker 비교 대신 엔티티 비교
            );
        }

        return query;
    }

    public void applySort(JPAQuery<?> q, Pageable pageable){
        for (Sort.Order o : pageable.getSort()) {
            var dir = o.isAscending() ? Order.ASC : Order.DESC;
            var mapped = EtfRowSort.from(o.getProperty());
            if (mapped.isEmpty()) continue;

            switch (mapped.get()) {
                case TICKER            -> q.orderBy(new OrderSpecifier<>(dir, etf.ticker));
                case KR_ISNM           -> q.orderBy(new OrderSpecifier<>(dir, etf.kr_isnm));
                case MARKET            -> q.orderBy(new OrderSpecifier<>(dir, etf.market));
                case STND_DATE         -> q.orderBy(new OrderSpecifier<>(dir, dpL.stndDate));
                case LATEST_PRICE      -> q.orderBy(new OrderSpecifier<>(dir, dpL.close));
                case CHANGE            -> q.orderBy(new OrderSpecifier<>(dir,
                        Expressions.numberTemplate(BigDecimal.class, "({0}-{1})", dpL.close, dpP.close)));
                case VOLUME            -> q.orderBy(new OrderSpecifier<>(dir, dpL.volume));
                case LATEST_DIV_DATE   -> q.orderBy(new OrderSpecifier<>(dir,
                        dividendQ.latestDividendDateMax(etf.ticker)).nullsLast());
                case LATEST_DIV_AMOUNT -> q.orderBy(new OrderSpecifier<>(dir,
                        dividendQ.latestDividendAmount(etf.ticker)).nullsLast());
            }
        }
        // tie-breaker
        q.orderBy(new OrderSpecifier<>(Order.ASC, etf.ticker));
    }
}
