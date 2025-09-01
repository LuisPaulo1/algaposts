package com.algaposts.post.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
public class TextProcessorData {
    private UUID postId;
    private String postBody;
    private Integer wordCount;
    private BigDecimal calculatedValue;
}
