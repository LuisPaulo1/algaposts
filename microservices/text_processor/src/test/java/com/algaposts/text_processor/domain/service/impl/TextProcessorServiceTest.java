package com.algaposts.text_processor.domain.service.impl;

import com.algaposts.text_processor.domain.service.PriceCalculator;
import com.algaposts.text_processor.domain.service.WordCountStrategy;
import com.algaposts.text_processor.infrastructure.messaging.dto.PostProcessingMessage;
import com.algaposts.text_processor.infrastructure.messaging.dto.PostProcessingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TextProcessorServiceTest {

    @Mock
    private WordCountStrategy wordCountStrategy;

    @Mock
    private PriceCalculator priceCalculator;

    private TextProcessorService textProcessorService;

    @BeforeEach
    void setUp() {
        textProcessorService = new TextProcessorService(wordCountStrategy, priceCalculator);
    }

    @Test
    void shouldProcessTextSuccessfully() {

        UUID postId = UUID.randomUUID();
        String postBody = "Hello world test";
        PostProcessingMessage message = PostProcessingMessage.builder()
                .postId(postId)
                .postBody(postBody)
                .build();

        int expectedWordCount = 3;
        BigDecimal expectedPrice = new BigDecimal("0.30");

        when(wordCountStrategy.countWords(postBody)).thenReturn(expectedWordCount);
        when(priceCalculator.calculatePrice(expectedWordCount)).thenReturn(expectedPrice);

        PostProcessingResult result = textProcessorService.processText(message);

        assertNotNull(result);
        assertEquals(postId, result.getPostId());
        assertEquals(expectedWordCount, result.getWordCount());
        assertEquals(expectedPrice, result.getCalculatedValue());

        verify(wordCountStrategy).countWords(postBody);
        verify(priceCalculator).calculatePrice(expectedWordCount);
    }

    @Test
    void shouldProcessEmptyText() {

        UUID postId = UUID.randomUUID();
        String postBody = "";
        PostProcessingMessage message = PostProcessingMessage.builder()
                .postId(postId)
                .postBody(postBody)
                .build();

        int expectedWordCount = 0;
        BigDecimal expectedPrice = BigDecimal.ZERO;

        when(wordCountStrategy.countWords(postBody)).thenReturn(expectedWordCount);
        when(priceCalculator.calculatePrice(expectedWordCount)).thenReturn(expectedPrice);

        PostProcessingResult result = textProcessorService.processText(message);

        assertNotNull(result);
        assertEquals(postId, result.getPostId());
        assertEquals(expectedWordCount, result.getWordCount());
        assertEquals(expectedPrice, result.getCalculatedValue());

        verify(wordCountStrategy).countWords(postBody);
        verify(priceCalculator).calculatePrice(expectedWordCount);
    }

    @Test
    void shouldProcessNullText() {

        UUID postId = UUID.randomUUID();
        String postBody = null;
        PostProcessingMessage message = PostProcessingMessage.builder()
                .postId(postId)
                .postBody(postBody)
                .build();

        int expectedWordCount = 0;
        BigDecimal expectedPrice = BigDecimal.ZERO;

        when(wordCountStrategy.countWords(postBody)).thenReturn(expectedWordCount);
        when(priceCalculator.calculatePrice(expectedWordCount)).thenReturn(expectedPrice);

        PostProcessingResult result = textProcessorService.processText(message);

        assertNotNull(result);
        assertEquals(postId, result.getPostId());
        assertEquals(expectedWordCount, result.getWordCount());
        assertEquals(expectedPrice, result.getCalculatedValue());

        verify(wordCountStrategy).countWords(postBody);
        verify(priceCalculator).calculatePrice(expectedWordCount);
    }

    @Test
    void shouldProcessLongText() {

        UUID postId = UUID.randomUUID();
        String postBody = "Lorem ipsum dolor sit amet consectetur adipiscing elit";
        PostProcessingMessage message = PostProcessingMessage.builder()
                .postId(postId)
                .postBody(postBody)
                .build();

        int expectedWordCount = 9;
        BigDecimal expectedPrice = new BigDecimal("0.90");

        when(wordCountStrategy.countWords(postBody)).thenReturn(expectedWordCount);
        when(priceCalculator.calculatePrice(expectedWordCount)).thenReturn(expectedPrice);

        PostProcessingResult result = textProcessorService.processText(message);

        assertNotNull(result);
        assertEquals(postId, result.getPostId());
        assertEquals(expectedWordCount, result.getWordCount());
        assertEquals(expectedPrice, result.getCalculatedValue());

        verify(wordCountStrategy).countWords(postBody);
        verify(priceCalculator).calculatePrice(expectedWordCount);
    }

    @Test
    void shouldHandleWordCountStrategyException() {

        UUID postId = UUID.randomUUID();
        String postBody = "Test text";
        PostProcessingMessage message = PostProcessingMessage.builder()
                .postId(postId)
                .postBody(postBody)
                .build();

        when(wordCountStrategy.countWords(anyString())).thenThrow(new RuntimeException("Word count error"));

        assertThrows(RuntimeException.class, () -> textProcessorService.processText(message));

        verify(wordCountStrategy).countWords(postBody);
        verify(priceCalculator, never()).calculatePrice(anyInt());
    }

    @Test
    void shouldHandlePriceCalculatorException() {

        UUID postId = UUID.randomUUID();
        String postBody = "Test text";
        PostProcessingMessage message = PostProcessingMessage.builder()
                .postId(postId)
                .postBody(postBody)
                .build();

        int wordCount = 2;
        when(wordCountStrategy.countWords(postBody)).thenReturn(wordCount);
        when(priceCalculator.calculatePrice(wordCount)).thenThrow(new RuntimeException("Price calculation error"));

        assertThrows(RuntimeException.class, () -> textProcessorService.processText(message));

        verify(wordCountStrategy).countWords(postBody);
        verify(priceCalculator).calculatePrice(wordCount);
    }

    @Test
    void shouldVerifyInteractionsBetweenServices() {

        UUID postId = UUID.randomUUID();
        String postBody = "Integration test";
        PostProcessingMessage message = PostProcessingMessage.builder()
                .postId(postId)
                .postBody(postBody)
                .build();

        int wordCount = 2;
        BigDecimal price = new BigDecimal("2.50");

        when(wordCountStrategy.countWords(postBody)).thenReturn(wordCount);
        when(priceCalculator.calculatePrice(wordCount)).thenReturn(price);

        PostProcessingResult result = textProcessorService.processText(message);

        assertNotNull(result);

        var inOrder = inOrder(wordCountStrategy, priceCalculator);
        inOrder.verify(wordCountStrategy).countWords(postBody);
        inOrder.verify(priceCalculator).calculatePrice(wordCount);

        verifyNoMoreInteractions(wordCountStrategy, priceCalculator);
    }
}
