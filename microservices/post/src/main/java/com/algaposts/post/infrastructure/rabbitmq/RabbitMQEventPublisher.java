package com.algaposts.post.infrastructure.rabbitmq;

import com.algaposts.post.api.dto.TextProcessorData;
import com.algaposts.post.domain.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.algaposts.post.infrastructure.rabbitmq.RabbitMQConfig.EXCHANGE_POST_PROCESS;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements EventPublisher {
    
    private final RabbitMQMessagePublisher rabbitMQMessagePublisher;
    
    @Override
    public void publishPostCreated(Post post) {
        log.info("Publicando evento de post criado: {}", post);
        Object payload = TextProcessorData.builder()
                .postId(post.getId())
                .postBody(post.getBody())
                .build();
        rabbitMQMessagePublisher.sendMessage(EXCHANGE_POST_PROCESS, "post.created", payload);
    }
}