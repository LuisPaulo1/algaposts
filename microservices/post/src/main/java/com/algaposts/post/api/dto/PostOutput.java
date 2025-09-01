package com.algaposts.post.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class PostOutput {
    private UUID id;
    private String title;
    private String body;
    private String author;
    private long wordCount;
    private double calculatedValue;
}
