package org.pda.etf.pdaetf.domain.etf.model;
import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class EtfCategoryMapId implements java.io.Serializable {
    private String ticker;
    private Long categoryId;
}