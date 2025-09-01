package com.algaposts.post.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class PostSummaryOutput {
    private UUID id;
    private String title;
    private String summary;
    private String author;
}
