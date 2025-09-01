package com.algaposts.post.infrastructure.rabbitmq;

import com.algaposts.post.api.dto.TextProcessorData;
import com.algaposts.post.domain.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.algaposts.post.infrastructure.rabbitmq.RabbitMQConfig.QUEUE_POST_SERVICE;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostMessageConsumer {

    private final PostService postService;

    @RabbitListener(queues = QUEUE_POST_SERVICE)
    public void receiveMessage(@Payload TextProcessorData textProcessorData) {
        log.info("Recebendo mensagem: {}", textProcessorData);
        postService.updatePostWithProcessedData(textProcessorData);
    }
}
