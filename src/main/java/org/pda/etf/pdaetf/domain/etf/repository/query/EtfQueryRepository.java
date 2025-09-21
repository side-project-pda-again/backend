package org.pda.etf.pdaetf.domain.etf.repository.query;

import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EtfQueryRepository {
    Page<EtfRowDto> searchEtfs(String query, Long categoryId, Long currentUserId, Pageable pageable);
    Page<EtfRowDto> findFavoriteEtfs(Long userId, String query, Pageable pageable);
}
