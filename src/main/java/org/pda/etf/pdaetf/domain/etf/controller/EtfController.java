package org.pda.etf.pdaetf.domain.etf.controller;

import lombok.RequiredArgsConstructor;
import org.pda.etf.pdaetf.common.dto.ApiResponse;
import org.pda.etf.pdaetf.common.exception.ApiException;
import org.pda.etf.pdaetf.common.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.pda.etf.pdaetf.domain.etf.model.Etf;
import org.pda.etf.pdaetf.domain.etf.service.EtfService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/etfs")
public class EtfController {

    private final EtfService etfService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Etf>> getOne(@PathVariable Long id) {
        Etf etf = etfService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(etf));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Etf>> create(@RequestBody Etf etf) {
        Etf saved = etfService.save(etf);
        return ResponseEntity
                .status(201) // 201 Created
                .body(ApiResponse.ok(saved, "ETF가 생성되었습니다."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        etfService.delete(id);
        return ResponseEntity
                .noContent() // 204
                .build();
    }
}