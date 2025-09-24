package org.pda.etf.pdaetf.domain.Portfolio.repository.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.domain.Portfolio.model.QPortfolio;
import org.pda.etf.pdaetf.domain.Portfolio.model.QPortfolioItem;
import org.pda.etf.pdaetf.domain.etf.repository.query.EtfQueryRepository;
import org.pda.etf.pdaetf.domain.user.model.QFavorite;
import org.springframework.data.domain.*;
import org.pda.etf.pdaetf.domain.dividend.model.QDividend;
import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.pda.etf.pdaetf.domain.etf.model.QEtf;
import org.pda.etf.pdaetf.domain.price.model.QDailyPrice;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PortfolioQueryRepositoryImpl implements PortfolioQueryRepository{
    private final JPAQueryFactory queryFactory;
    private final EtfQueryRepository etfQueryRepository;

    private static final QEtf etf = QEtf.etf;
    private static final QFavorite favorite = QFavorite.favorite;
    private static final QDailyPrice dpLatest = new QDailyPrice("dpLatest");
    private static final QDailyPrice dpPrev   = new QDailyPrice("dpPrev");
    private static final QDividend div1 = QDividend.dividend;
    private static final QDividend div2 = new QDividend("div2");

    private static final QPortfolio pf = QPortfolio.portfolio;
    private static final QPortfolioItem pi = QPortfolioItem.portfolioItem;

    @Override
    public Page<EtfRowDto> findEtfsInPortfolio(Long userId, Long portfolioId, Pageable pageable) {
        BooleanExpression likedExpr = JPAExpressions.selectOne().from(favorite)
                .where(
                        favorite.id.userId.eq(userId),
                        favorite.id.ticker.eq(etf.ticker)
                ).exists();

        JPAQuery<EtfRowDto> dataQuery = etfQueryRepository.buildBaseEtfQuery(null, likedExpr, false)
                .join(pi).on(pi.etf.eq(etf))
                .join(pi.portfolio, pf)
                .where(
                        pf.portfolioId.eq(portfolioId),
                        pf.owner.userId.eq(userId)
                );

        List<EtfRowDto> content = dataQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(etf.ticker.countDistinct())
                .from(pi)
                .join(pi.portfolio, pf)
                .join(pi.etf, etf)
                .where(
                        pf.portfolioId.eq(portfolioId),
                        pf.owner.userId.eq(userId)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

}
