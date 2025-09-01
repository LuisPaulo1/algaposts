package com.algaposts.post.mapper;

import com.algaposts.post.api.dto.PostInput;
import com.algaposts.post.api.dto.PostOutput;
import com.algaposts.post.api.dto.PostSummaryOutput;
import com.algaposts.post.domain.model.Post;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Component
public class PostMapper {
    
    public Post toEntity(PostInput postInput) {
        return Post.builder()
                .id(UUID.randomUUID())
                .title(postInput.getTitle())
                .body(postInput.getBody())
                .author(postInput.getAuthor())
                .build();
    }
    
    public PostOutput toOutput(Post post) {
        return PostOutput.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .author(post.getAuthor())
                .wordCount(post.getWordCount())
                .calculatedValue(post.getCalculatedValue())
                .build();
    }

    public PostSummaryOutput toSummaryOutput(Post post) {
        String summary = extractFirstThreeLines(post.getBody());
        return PostSummaryOutput.builder()
                .id(post.getId())
                .title(post.getTitle())
                .summary(summary)
                .author(post.getAuthor())
                .build();
    }

    private String extractFirstThreeLines(String body) {
        if (!StringUtils.hasText(body)) {
            return "";
        }

        final int MAX_CHARS = 350;
        if (body.length() <= MAX_CHARS) {
            return body;
        }

        int lastSpace = body.lastIndexOf(' ', MAX_CHARS);
        if (lastSpace > 0) {
            return body.substring(0, lastSpace) + "...";
        }

        return body.substring(0, MAX_CHARS) + "...";
    }
}