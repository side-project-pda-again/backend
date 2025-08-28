package org.pda.etf.pdaetf.domain.etf.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class EtfPriceId implements java.io.Serializable {
    private Long etfId;
    private LocalDate priceDate;
}