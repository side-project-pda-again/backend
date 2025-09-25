package org.pda.etf.pdaetf.domain.price.repository.query;

import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.StringPath;

import java.time.LocalDate;

public interface DailyPriceQueryRepository {
    //주어진 티커에 대해 일자(stnd_date) 기준으로 '가장 최신' 일자
    SubQueryExpression<LocalDate> latestDateSubQ(StringPath ticker);
    //주어진 티커에 대해 '최신 일자 이전'의 최대 일자(= 전일) 반환
    SubQueryExpression<LocalDate> prevDateSubQ(StringPath ticker, SubQueryExpression<LocalDate> latestDateSub);
}
