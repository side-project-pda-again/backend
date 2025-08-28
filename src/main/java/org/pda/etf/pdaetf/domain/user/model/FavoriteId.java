package org.pda.etf.pdaetf.domain.user.model;
import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class FavoriteId implements java.io.Serializable {
    private Long userId;
    private Long etfId;
}