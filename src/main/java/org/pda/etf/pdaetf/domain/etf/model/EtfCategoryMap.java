package org.pda.etf.pdaetf.domain.etf.model;
import jakarta.persistence.*;
import lombok.*;
import org.pda.etf.pdaetf.domain.category.model.Category;

@Entity
@Table(name = "etf_category_map")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EtfCategoryMap {
    @EmbeddedId
    private EtfCategoryMapId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ticker")
    private Etf etf;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    private Category category;
}