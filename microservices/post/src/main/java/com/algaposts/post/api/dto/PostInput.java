package com.algaposts.post.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostInput {
    @NotBlank
    private String title;
    @NotBlank
    private String body;
    @NotBlank
    private String author;
}
