package com.algaposts.text_processor.domain.service;

import java.math.BigDecimal;

public interface PriceCalculator {
    BigDecimal calculatePrice(int wordCount);
}