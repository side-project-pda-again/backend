package org.pda.etf.pdaetf.domain.etf.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.pda.etf.pdaetf.domain.dividend.model.Dividend;
import org.pda.etf.pdaetf.domain.price.model.DailyPrice;
import java.util.HashSet;
import java.util.Set;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "ticker"
)
@Entity
@Table(name = "etfs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Etf {
    @Id
    @Column(name = "ticker", length = 20)
    private String ticker;
    private String kr_isnm; //한국 상장명
    private String market; //시장 구분

    @OneToMany(mappedBy = "etf", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<DailyPrice> dailyPrices = new HashSet<>();

    @OneToMany(mappedBy = "etf", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Dividend> dividends = new HashSet<>();

    @OneToMany(mappedBy = "etf", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<EtfCategoryMap> categories = new HashSet<>();
}