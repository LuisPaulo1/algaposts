package com.algaposts.post.mapper;

import com.algaposts.post.api.dto.PostInput;
import com.algaposts.post.api.dto.PostOutput;
import com.algaposts.post.api.dto.PostSummaryOutput;
import com.algaposts.post.domain.model.Post;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PostMapper {
    
    public Post toEntity(PostInput postInput) {
        return Post.builder()
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

        String[] lines = body.split("\n");
        StringBuilder summary = new StringBuilder();

        for (int i = 0; i < Math.min(3, lines.length); i++) {
            if (i > 0) {
                summary.append("\n");
            }
            summary.append(lines[i]);
        }

        return summary.toString();
    }
}