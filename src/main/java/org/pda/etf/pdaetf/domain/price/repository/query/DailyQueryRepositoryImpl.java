package org.pda.etf.pdaetf.domain.price.repository.query;

import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.domain.price.model.QDailyPrice;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class DailyQueryRepositoryImpl implements DailyPriceQueryRepository {

    private static final QDailyPrice dp = QDailyPrice.dailyPrice;

    @Override
    public SubQueryExpression<LocalDate> latestDateSubQ(StringPath ticker) {
        return JPAExpressions.select(dp.stndDate.max())
                .from(dp)
                .where(dp.ticker.eq(ticker));
    }

    @Override
    public SubQueryExpression<LocalDate> prevDateSubQ(StringPath ticker, SubQueryExpression<LocalDate> latestDateSub) {
        return JPAExpressions.select(dp.stndDate.max())
                .from(dp)
                .where(dp.ticker.eq(ticker), dp.stndDate.lt(latestDateSub));
    }
}
