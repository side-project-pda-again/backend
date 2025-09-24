package org.pda.etf.pdaetf.domain.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.pda.etf.pdaetf.domain.etf.model.Etf;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorites")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Favorite {
    @EmbeddedId
    private FavoriteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ticker")
    @JoinColumn(name = "etf_ticker", nullable = false, columnDefinition = "varchar(20)")
    private Etf etf;

    private LocalDateTime createdAt;
}