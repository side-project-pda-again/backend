package org.pda.etf.pdaetf.domain.etf.repository.query;

import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.pda.etf.pdaetf.domain.etf.model.QEtf;
import org.pda.etf.pdaetf.domain.etf.model.QEtfCategoryMap;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EtfQueryRepositoryImpl implements EtfQueryRepository {

    private final JPAQueryFactory qf;
    private final EtfRowQueryBuilder builder;
    private static final QEtf etf = QEtf.etf;
    private static final QEtfCategoryMap map = QEtfCategoryMap.etfCategoryMap;

    @Override
    public Page<EtfRowDto> searchEtfs(BooleanExpression baseFilter,
                                      Long currentUserId,
                                      Pageable pageable,
                                      Long categoryId) {

        BooleanExpression where = (baseFilter != null) ? baseFilter : Expressions.TRUE.isTrue();

        if (categoryId != null) {
            where = where.and(
                    JPAExpressions.selectOne().from(map)
                            .where(map.etf.ticker.eq(etf.ticker),
                                    map.id.categoryId.eq(categoryId))
                            .exists()
            );
        }

        var dataQ = builder.base(where, currentUserId, false);
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

