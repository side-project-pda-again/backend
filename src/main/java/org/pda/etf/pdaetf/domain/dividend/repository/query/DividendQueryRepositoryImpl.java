package org.pda.etf.pdaetf.domain.dividend.repository.query;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import org.pda.etf.pdaetf.domain.dividend.model.QDividend;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public class DividendQueryRepositoryImpl implements DividendQueryRepository{

    private static final QDividend d1 = QDividend.dividend;
    private static final QDividend d2 = new QDividend("d2");

    @Override
    public SubQueryExpression<LocalDate> latestDividendDateSubQ(StringPath ticker) {
        return JPAExpressions.select(d1.stndDate.max())
                .from(d1)
                .where(d1.ticker.eq(ticker));
    }

    @Override
    public SubQueryExpression<BigDecimal> latestDividendAmountOnDateSubQ(StringPath ticker, SubQueryExpression<LocalDate> onDate) {
        return JPAExpressions.select(d2.dividendAmount)
                .from(d2)
                .where(d2.ticker.eq(ticker), d2.stndDate.eq(onDate));
    }

    @Override
    public Expression<LocalDate> latestDividendDateMax(StringPath ticker) {
        return Expressions.dateTemplate(
                LocalDate.class,
                "({0})",
                JPAExpressions.select(d1.stndDate.max())
                        .from(d1)
                        .where(d1.ticker.eq(ticker))
        );
    }

    @Override
    public Expression<BigDecimal> latestDividendAmount(StringPath ticker) {
        var latest = latestDividendDateSubQ(ticker);
        return Expressions.numberTemplate(
                BigDecimal.class,
                "({0})",
                JPAExpressions.select(d2.dividendAmount)
                        .from(d2)
                        .where(d2.ticker.eq(ticker), d2.stndDate.eq(latest))
        );
    }
}
