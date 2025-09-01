package com.algaposts.text_processor.domain.service;

import com.algaposts.text_processor.infrastructure.messaging.dto.PostProcessingMessage;
import com.algaposts.text_processor.infrastructure.messaging.dto.PostProcessingResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextProcessorService implements TextProcessor {

    private final WordCountStrategy wordCountStrategy;
    private final PriceCalculator priceCalculator;

    @Override
    public PostProcessingResult processText(PostProcessingMessage message) {
        log.info("Processando texto para post ID: {}", message.getPostId());

        int wordCount = wordCountStrategy.countWords(message.getPostBody());
        BigDecimal calculatedValue = priceCalculator.calculatePrice(wordCount);

        log.info("Post ID: {} - Palavras: {} - Valor: {}", message.getPostId(), wordCount, calculatedValue);

        return PostProcessingResult.builder()
                .postId(message.getPostId())
                .wordCount(wordCount)
                .calculatedValue(calculatedValue)
                .build();
    }
}