package org.pda.etf.pdaetf.domain.etf.service;

import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.common.exception.ApiException;
import org.pda.etf.pdaetf.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.pda.etf.pdaetf.domain.etf.model.Etf;
import org.pda.etf.pdaetf.domain.etf.repository.EtfRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EtfService {

    private final EtfRepository etfRepository;

    public List<Etf> findAll() {
        return etfRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Etf findById(Long id) {
        return etfRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
    }

    @Transactional
    public Etf save(Etf etf) {
        return etfRepository.save(etf);
    }

    @Transactional
    public void delete(Long id) {
        etfRepository.deleteById(id);
    }
}