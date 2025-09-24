package org.pda.etf.pdaetf.domain.user.repository.query;

import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteQueryRepository {
    Page<EtfRowDto> findFavoriteEtfs(Long userId, String query, Pageable pageable);
}
