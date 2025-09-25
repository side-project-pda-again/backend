package org.pda.etf.pdaetf.domain.etf.repository.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EtfQueryRepository {
    Page<EtfRowDto> searchEtfs(BooleanExpression baseFilter,
                               Long currentUserId,
                               Pageable pageable,
                               Long categoryId);
}
