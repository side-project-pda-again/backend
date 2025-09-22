package org.pda.etf.pdaetf.domain.user.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pda.etf.pdaetf.common.exception.ApiException;
import org.pda.etf.pdaetf.common.exception.ErrorCode;
import org.pda.etf.pdaetf.domain.etf.model.Etf;
import org.pda.etf.pdaetf.domain.etf.repository.EtfRepository;
import org.pda.etf.pdaetf.domain.user.model.Portfolio;
import org.pda.etf.pdaetf.domain.user.model.PortfolioItem;
import org.pda.etf.pdaetf.domain.user.model.PortfolioItemId;
import org.pda.etf.pdaetf.domain.user.repository.PortfolioItemRepository;
import org.pda.etf.pdaetf.domain.user.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final EtfRepository etfRepository;
    private final EntityManager em;

    /**
     * 그룹에 ETF 추가
     */
    @Transactional
    public void addEtf(Long userId, Long portfolioId, String ticker){
        Portfolio p = portfolioRepository.findById(portfolioId)
                .orElseThrow(()->new ApiException(ErrorCode.NOT_FOUND, "포트폴리오를 찾을 수 없습니다."));
        if(!p.getOwner().getUserId().equals(userId)){
            throw new ApiException(ErrorCode.UNAUTHORIZED, "해당 포트폴리오의 소유자가 아닙니다.");
        }
        if(!etfRepository.existsByTicker(ticker)){
            throw new ApiException(ErrorCode.NOT_FOUND, "종목을 찾을 수 없습니다.");
        }
        if(portfolioItemRepository.existsByPortfolio_PortfolioIdAndEtf_Ticker(portfolioId, ticker)){
            log.info("포트폴리오에 종목 추가 실패 - 이미 추가된 포트폴리오, portfolioId={}, ticker={}", portfolioId, ticker);
            return;
        }

        PortfolioItem item = new PortfolioItem();
        item.setId(new PortfolioItemId());
        item.setPortfolio(em.getReference(Portfolio.class, portfolioId));
        item.setEtf(em.getReference(Etf.class, ticker));
        portfolioItemRepository.save(item);

    }
}
