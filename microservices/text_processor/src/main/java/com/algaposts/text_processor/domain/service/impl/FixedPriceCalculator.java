
package com.algaposts.text_processor.domain.service.impl;

import com.algaposts.text_processor.domain.service.PriceCalculator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FixedPriceCalculator implements PriceCalculator {
    
    @Value("${text-processor.price-per-word:0.10}")
    private BigDecimal pricePerWord;
    
    @Override
    public BigDecimal calculatePrice(int wordCount) {
        return pricePerWord.multiply(BigDecimal.valueOf(wordCount))
                .setScale(2, RoundingMode.HALF_UP);
    }
}