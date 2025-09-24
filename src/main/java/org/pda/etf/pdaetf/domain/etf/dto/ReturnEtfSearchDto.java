package org.pda.etf.pdaetf.domain.etf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnEtfSearchDto {
    private List<EtfRowDto> content;

    private int page;           // 요청 page
    private int size;           // 요청 size
    private long totalElements; // 전체 개수
    private int totalPages;     // 총 페이지 수
    private String sort;        // "ticker,asc" 형태 그대로 echo
}
