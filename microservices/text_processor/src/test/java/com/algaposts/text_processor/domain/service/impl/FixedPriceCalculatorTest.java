package com.algaposts.text_processor.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FixedPriceCalculatorTest {

    private FixedPriceCalculator fixedPriceCalculator;

    @BeforeEach
    void setUp() {
        fixedPriceCalculator = new FixedPriceCalculator();
    }

    @Test
    void shouldCalculatePriceWithDefaultValue() {

        ReflectionTestUtils.setField(fixedPriceCalculator, "pricePerWord", new BigDecimal("0.10"));
        int wordCount = 100;

        BigDecimal result = fixedPriceCalculator.calculatePrice(wordCount);

        assertEquals(new BigDecimal("10.00"), result);
    }

    @Test
    void shouldCalculatePriceWithZeroWords() {

        ReflectionTestUtils.setField(fixedPriceCalculator, "pricePerWord", new BigDecimal("0.10"));
        int wordCount = 0;

        BigDecimal result = fixedPriceCalculator.calculatePrice(wordCount);

        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    void shouldCalculatePriceWithCustomPricePerWord() {

        ReflectionTestUtils.setField(fixedPriceCalculator, "pricePerWord", new BigDecimal("0.25"));
        int wordCount = 50;

        BigDecimal result = fixedPriceCalculator.calculatePrice(wordCount);

        assertEquals(new BigDecimal("12.50"), result);
    }

    @Test
    void shouldRoundToTwoDecimalPlaces() {

        ReflectionTestUtils.setField(fixedPriceCalculator, "pricePerWord", new BigDecimal("0.333"));
        int wordCount = 3;

        BigDecimal result = fixedPriceCalculator.calculatePrice(wordCount);

        assertEquals(new BigDecimal("1.00"), result);
    }

    @Test
    void shouldCalculatePriceWithLargeWordCount() {

        ReflectionTestUtils.setField(fixedPriceCalculator, "pricePerWord", new BigDecimal("0.10"));
        int wordCount = 10000;

        BigDecimal result = fixedPriceCalculator.calculatePrice(wordCount);

        assertEquals(new BigDecimal("1000.00"), result);
    }
}
