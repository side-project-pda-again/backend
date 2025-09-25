package org.pda.etf.pdaetf.domain.etf.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pda.etf.pdaetf.common.dto.ApiResponse;
import org.pda.etf.pdaetf.common.exception.ApiException;
import org.pda.etf.pdaetf.common.exception.ErrorCode;
import org.pda.etf.pdaetf.domain.etf.dto.EtfRowDto;
import org.pda.etf.pdaetf.domain.etf.dto.ReturnEtfSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.pda.etf.pdaetf.domain.etf.dto.ReturnCalculationDto;
import org.pda.etf.pdaetf.domain.etf.model.Etf;
import org.pda.etf.pdaetf.domain.etf.service.EtfService;

import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/etfs")
public class EtfController {

	private final EtfService etfService;
	private static final Set<String> ALLOWED_SORT_KEYS = Set.of(
			"ticker","krIsnm","market","stndDate","latestPrice","change","volume",
			"latestDividendDate","latestDividendAmount"
	);

	@GetMapping("/{ticker}")
	public ResponseEntity<ApiResponse<Etf>> getOne(@PathVariable String ticker) {
		Etf etf = etfService.findByTicker(ticker);
		return ResponseEntity.ok(ApiResponse.ok(etf));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Etf>> create(@RequestBody Etf etf) {
		Etf saved = etfService.save(etf);
		return ResponseEntity
				.status(201) // 201 Created
				.body(ApiResponse.ok(saved, "ETF가 생성되었습니다."));
	}

	@DeleteMapping("/{ticker}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String ticker) {
		etfService.delete(ticker);
		return ResponseEntity
				.noContent() // 204
				.build();
	}

	/**
	 * 종목의 수익률 계산
	 * @param ticker 종목코드
	 * @param startDate 시작일 (YYYYMMDD)
	 * @param endDate 종료일 (YYYYMMDD)
	 * @return 수익률 계산 결과
	 */
	@GetMapping("/{ticker}/return-calculation")
	public ResponseEntity<ApiResponse<ReturnCalculationDto>> calculateReturn(
			@PathVariable String ticker,
			@RequestParam String startDate,
			@RequestParam String endDate) {
		log.info("API 호출 - GET /api/etfs/{}/return-calculation, params[ticker={}, startDate={}, endDate={}]",
				ticker, ticker, startDate, endDate);

		// 입력값 검증
		// ticker 검증
		if (ticker == null || ticker.trim().isEmpty()) {
			log.error("수익률 계산 실패 - ticker가 비어있음");
			throw new ApiException(ErrorCode.INVALID_INPUT, "종목코드(ticker)는 필수입니다.");
		}
		
		// startDate 검증
		if (startDate == null || startDate.trim().isEmpty()) {
			log.error("수익률 계산 실패 - startDate가 비어있음, ticker: {}", ticker);
			throw new ApiException(ErrorCode.INVALID_INPUT, "시작일(startDate)은 필수입니다.");
		}
		
		// endDate 검증
		if (endDate == null || endDate.trim().isEmpty()) {
			log.error("수익률 계산 실패 - endDate가 비어있음, ticker: {}", ticker);
			throw new ApiException(ErrorCode.INVALID_INPUT, "종료일(endDate)은 필수입니다.");
		}
		
		try {
			ReturnCalculationDto result = etfService.calculateReturn(ticker, startDate, endDate);
			return ResponseEntity.ok(ApiResponse.ok(result));
		} catch (Exception e) {
			StackTraceElement origin = e.getStackTrace().length > 0 ? e.getStackTrace()[0] : null;
			String location = origin == null ? "<unknown>" : String.format("%s.%s(%s:%d)",
					origin.getClassName(), origin.getMethodName(), origin.getFileName(), origin.getLineNumber());
			log.error("수익률 계산 실패 - location={}, params[ticker={}, startDate={}, endDate={}], message={}",
					location, ticker, startDate, endDate, e.getMessage(), e);
			throw e;
		} finally {
			log.info("API 완료 - GET /api/etfs/{}/return-calculation,", ticker);
		}
	}


	/**
	 * 종목 검색
	 * @param query ticker, 종목명 검색
	 * @param categoryId
	 * @param pageable
	 * @param sortParam ex)volume, desc
	 * @return 종목 검색 결과
	 */
	@GetMapping({"/", ""})
	public ResponseEntity<ApiResponse<ReturnEtfSearchDto>> search(
			@RequestParam(required = false) String query,
			@RequestParam(required = false) Long categoryId,
			@PageableDefault(size=20, sort="ticker", direction = Sort.Direction.ASC) Pageable pageable,
			@RequestParam(name="sort", required = false, defaultValue = "ticker,asc") String sortParam
	){
		Long currentUserId = null; // TODO: 유저 정보 가져오기

		for (Sort.Order o : pageable.getSort()) {
			String key = o.getProperty();
			if (!ALLOWED_SORT_KEYS.contains(key)) {
				log.error("즐겨찾기 조회 실패 - 지원하지 않는 정렬 키, key: {}", key);
				throw new ApiException(ErrorCode.INVALID_INPUT, key+"는 지원하지 않는 정렬 키입니다.");
			}
		}

		Page<EtfRowDto> page = etfService.search(query, categoryId, pageable, currentUserId);

		ReturnEtfSearchDto body = ReturnEtfSearchDto.builder()
				.content(page.getContent())   // ← EtfRowDto 목록
				.page(page.getNumber())
				.size(page.getSize())
				.totalElements(page.getTotalElements())
				.totalPages(page.getTotalPages())
				.sort(sortParam)
				.build();

		return ResponseEntity.ok(ApiResponse.ok(body));
	}

}