package org.pda.etf.pdaetf.domain.portfolio.repository.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.pda.etf.pdaetf.domain.etf.model.QEtf;
import org.pda.etf.pdaetf.domain.etf.repository.query.EtfRowQueryBuilder;
import org.pda.etf.pdaetf.domain.portfolio.model.QPortfolio;
import org.pda.etf.pdaetf.domain.portfolio.model.QPortfolioItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PortfolioQueryRepositoryImpl implements PortfolioQueryRepository {

    private final JPAQueryFactory qf;
    private final EtfRowQueryBuilder builder;

    private static final QEtf etf = QEtf.etf;
    private static final QPortfolio pf = QPortfolio.portfolio;
    private static final QPortfolioItem pi = QPortfolioItem.portfolioItem;

    @Override
    public Page<EtfRowDto> findEtfsInPortfolio(Long userId, Long portfolioId, Pageable pageable) {

        // 1) 포트폴리오에 담긴 etf만 통과시키는 EXISTS 필터
        BooleanExpression inPortfolio = JPAExpressions.selectOne()
                .from(pi)
                .join(pi.portfolio, pf)
                .where(
                        pi.etf.ticker.eq(etf.ticker),
                        pf.portfolioId.eq(portfolioId),
                        pf.owner.userId.eq(userId)
                )
                .exists();

        // 2) 베이스 쿼리 구성 (liked 플래그는 builder가 currentUserId로 처리)
        var where = Expressions.TRUE.isTrue().and(inPortfolio);

        var dataQ = builder.base(where, userId, /*onlyFavorites*/ false);
        builder.applySort(dataQ, pageable); // 내부에 tie-breaker가 있으면 더 좋음

        var content = dataQ
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 3) total - 중복행 우려가 없으므로 count()로 OK
        Long total = qf.select(etf.ticker.count())
                .from(etf)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }
}
