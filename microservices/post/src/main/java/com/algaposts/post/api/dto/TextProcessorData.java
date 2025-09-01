package com.algaposts.post.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class TextProcessorData {
    private UUID postId;
    private String postBody;
    private long wordCount;
    private double calculatedValue;
}
