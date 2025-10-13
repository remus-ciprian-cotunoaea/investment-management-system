package com.investment.positions.configuration.kafka;

import com.investment.positions.service.RecalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PositionsKafkaConsumer {

    private final TopicsProperties topicsProperties;
    private final RecalculationService recalculationService;

    @KafkaListener(
            topics = "#{@topicsProperties.positionsRecalculateRequested()}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleRecalculateRequest(String message) {
        log.info("Received recalculate request on topic '{}': {}",
                topicsProperties.positionsRecalculateRequested(), message);
        recalculationService.processRecalculation(message); // ✅ ahora sí se usa
    }
}