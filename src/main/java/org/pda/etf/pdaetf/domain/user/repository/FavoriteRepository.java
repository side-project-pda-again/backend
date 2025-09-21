package org.pda.etf.pdaetf.domain.user.repository;

import org.pda.etf.pdaetf.domain.user.model.Favorite;
import org.pda.etf.pdaetf.domain.user.model.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    boolean existsById(FavoriteId id);
}
