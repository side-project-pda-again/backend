package org.pda.etf.pdaetf.domain.etf.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "etfs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Etf {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long etfId;

    @Column(nullable = false, length = 20)
    private String ticker;

    private String managerName;
    private String exchange;
    private String baseCurrency;

    private LocalDate listingDate;
    private LocalDate delistingDate;

    private BigDecimal expenseRatio;
    private Integer dividendPayoutsPerYear;

    @OneToMany(mappedBy = "etf", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EtfPrice> prices = new HashSet<>();

    @OneToMany(mappedBy = "etf", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EtfDividend> dividends = new HashSet<>();

    @OneToMany(mappedBy = "etf", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EtfCategoryMap> categories = new HashSet<>();
}