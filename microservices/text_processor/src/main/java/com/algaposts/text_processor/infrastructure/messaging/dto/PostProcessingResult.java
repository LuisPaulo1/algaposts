package com.algaposts.text_processor.infrastructure.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostProcessingResult {
    private UUID postId;
    private Integer wordCount;
    private BigDecimal calculatedValue;
}
