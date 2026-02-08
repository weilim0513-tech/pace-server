package com.pace.server.domain.recap.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pace.server.domain.recap.cache.RecapCacheRepository;
import com.pace.server.domain.recap.dto.NewsSummary;
import com.pace.server.domain.recap.dto.RecapResponse;
import com.pace.server.domain.recap.dto.StockSummary;
import com.pace.server.domain.recap.dto.WeatherSummary;
import com.pace.server.domain.wake.entity.Todo;
import com.pace.server.domain.wake.repository.TodoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 모닝 리캡 서비스
 * Redis 캐시 조회 → Cache Miss 시 On-demand Fetch
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecapService {

    private final RecapCacheRepository cacheRepository;
    private final TodoRepository todoRepository;

    // TODO: 외부 API 서비스 연동 후 주입
    // private final WeatherService weatherService;
    // private final NewsService newsService;
    // private final FinanceService financeService;

    /**
     * 오늘의 모닝 리캡 조회
     */
    public RecapResponse getRecap(Long userId) {
        LocalDate today = LocalDate.now();

        // 1. 캐시 조회 (배치에서 미리 생성된 데이터)
        return cacheRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> {
                    // 2. Cache Miss → On-demand Fetch
                    log.info("Cache miss for user {}, fetching on-demand", userId);
                    RecapResponse response = fetchOnDemand(userId, today);

                    // 3. 캐시에 저장
                    cacheRepository.save(userId, today, response);
                    return response;
                });
    }

    /**
     * On-demand로 리캡 데이터 조합
     * TODO: 외부 API 연동 후 Structured Concurrency로 병렬 호출
     */
    private RecapResponse fetchOnDemand(Long userId, LocalDate today) {
        // 현재는 Todo만 조회, 나머지는 더미 데이터
        List<Todo> todos = todoRepository.findByUserIdAndTargetDateAndIsDoneFalse(userId, today);
        List<String> todoContents = todos.stream()
                .map(Todo::getContent)
                .toList();

        return RecapResponse.builder()
                .date(today)
                .weather(createPlaceholderWeather())
                .news(createPlaceholderNews())
                .finance(createPlaceholderFinance())
                .todos(todoContents)
                .motivationMessage(getMotivationMessage())
                .build();
    }

    private WeatherSummary createPlaceholderWeather() {
        return WeatherSummary.builder()
                .regionName("서울")
                .temperature(15.0)
                .condition("맑음")
                .precipitationProbability(10)
                .umbrellaNeeded(false)
                .isFallback(true)
                .fallbackMessage("외부 API 연동 후 실제 데이터가 표시됩니다.")
                .build();
    }

    private List<NewsSummary> createPlaceholderNews() {
        return List.of(
                NewsSummary.builder()
                        .title("뉴스 API 연동 예정")
                        .summary("네이버 뉴스 API 연동 후 실제 뉴스가 표시됩니다.")
                        .category("안내")
                        .build());
    }

    private List<StockSummary> createPlaceholderFinance() {
        return List.of(
                StockSummary.builder()
                        .code("005930")
                        .name("삼성전자")
                        .currentPrice(0)
                        .changeAmount(0)
                        .changePercent(0)
                        .changeSign("보합")
                        .build());
    }

    private String getMotivationMessage() {
        String[] messages = {
                "🌟 오늘도 한 걸음 더",
                "💪 시작이 반이다",
                "🔥 당신은 할 수 있다",
                "✨ 매일이 새로운 기회",
                "🚀 꾸준함이 재능을 이긴다"
        };
        return messages[(int) (Math.random() * messages.length)];
    }
}
