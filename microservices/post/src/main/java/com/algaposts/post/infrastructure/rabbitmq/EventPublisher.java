package com.algaposts.post.infrastructure.rabbitmq;

import com.algaposts.post.domain.model.Post;

public interface EventPublisher {
    void publishPostCreated(Post post);
}