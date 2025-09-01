package com.algaposts.text_processor.domain.service.impl;

import com.algaposts.text_processor.domain.service.WordCountStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SimpleWordCountStrategy implements WordCountStrategy {
    
    @Override
    public int countWords(String text) {
        if (!StringUtils.hasText(text)) {
            return 0;
        }

        String cleanText = text.strip().replaceAll("\\s+", " ");
        if (cleanText.isEmpty()) {
            return 0;
        }

        return cleanText.split(" ").length;
    }
}