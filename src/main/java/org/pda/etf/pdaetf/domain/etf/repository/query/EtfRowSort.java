package org.pda.etf.pdaetf.domain.etf.repository.query;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;

@Getter
public enum EtfRowSort {
    TICKER("ticker"),
    KR_ISNM("krIsnm"),
    MARKET("market"),
    STND_DATE("stndDate"),
    LATEST_PRICE("latestPrice"),
    CHANGE("change"),
    VOLUME("volume"),
    LATEST_DIV_DATE("latestDividendDate"),
    LATEST_DIV_AMOUNT("latestDividendAmount");

    private final String key;
    EtfRowSort(String key) { this.key = key; }

    public static Optional<EtfRowSort> from(String key) {
        return Arrays.stream(values()).filter(v -> v.key.equals(key)).findFirst();
    }
}
