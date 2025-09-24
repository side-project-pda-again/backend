package org.pda.etf.pdaetf.domain.user.model;
import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class FavoriteId implements java.io.Serializable {
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "etf_ticker", length = 20)
    private String ticker;
}