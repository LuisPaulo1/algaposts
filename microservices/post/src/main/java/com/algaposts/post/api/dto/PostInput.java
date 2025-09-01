package com.algaposts.post.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class PostInput {
    @NotBlank
    private String title;
    @NotBlank
    private String body;
    @NotBlank
    private String author;
}
