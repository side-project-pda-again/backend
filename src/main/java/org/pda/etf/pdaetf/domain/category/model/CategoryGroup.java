package org.pda.etf.pdaetf.domain.category.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category_groups")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryGroup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Category> categories = new HashSet<>();
}