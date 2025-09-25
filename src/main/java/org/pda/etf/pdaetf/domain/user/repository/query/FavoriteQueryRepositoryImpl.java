package org.pda.etf.pdaetf.domain.user.repository.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.pda.etf.pdaetf.domain.etf.model.QEtf;
import org.pda.etf.pdaetf.domain.etf.model.QEtfCategoryMap;
import org.pda.etf.pdaetf.domain.etf.repository.query.EtfRowQueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FavoriteQueryRepositoryImpl implements FavoriteQueryRepository{

    private final JPAQueryFactory qf;
    private final EtfRowQueryBuilder builder;
    private static final QEtf etf = QEtf.etf;
    private static final QEtfCategoryMap map = QEtfCategoryMap.etfCategoryMap;

    @Override
    public Page<EtfRowDto> findFavoriteEtfs(BooleanExpression baseFilter,
                                            Long currentUserId,
                                            Pageable pageable) {

        BooleanExpression where = (baseFilter != null) ? baseFilter : Expressions.TRUE.isTrue();

        var dataQ = builder.base(where, currentUserId, true);
        builder.applySort(dataQ, pageable);

        var content = dataQ
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = qf.select(etf.ticker.count())
                .from(etf)
                .where(where)
                .fetchOne();
        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}
