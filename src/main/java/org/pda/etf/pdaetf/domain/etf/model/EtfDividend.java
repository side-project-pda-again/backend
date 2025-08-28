package org.pda.etf.pdaetf.domain.etf.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "etf_dividends")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EtfDividend {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dividendId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etf_id", nullable = false)
    private Etf etf;

    private LocalDate exDate;
    private LocalDate payDate;
    private BigDecimal amount;
}