package org.pda.etf.pdaetf.domain.Portfolio.model;

import jakarta.persistence.*;
import lombok.*;
import org.pda.etf.pdaetf.domain.user.model.User;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "portfolios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Portfolio {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long portfolioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User owner;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    //그룹 내 종목들
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PortfolioItem> items = new LinkedHashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
    }
}
