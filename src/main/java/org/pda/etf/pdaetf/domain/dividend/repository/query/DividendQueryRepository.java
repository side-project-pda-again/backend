package org.pda.etf.pdaetf.domain.dividend.repository.query;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.StringPath;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DividendQueryRepository {
    //주어진 티커의 배당 데이터 중 '가장 최신(stnd_date MAX)' 일자 반환
    SubQueryExpression<LocalDate> latestDividendDateSubQ(StringPath ticker);
    //특정 일자(onDate)에 해당하는 배당금(dividend_amount)
    SubQueryExpression<BigDecimal> latestDividendAmountOnDateSubQ(StringPath ticker, SubQueryExpression<LocalDate> onDate);
    //정렬용 편의 Expression: 최신 배당일을 정렬 키로 쓰기 위한 표현식 반환
    Expression<LocalDate> latestDividendDateMax(StringPath ticker);
    //정렬/선택용 편의 Expression: 최신 배당금 금액을 표현식 반환
    Expression<BigDecimal> latestDividendAmount(StringPath ticker);
}
