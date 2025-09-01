package com.algaposts.text_processor.infrastructure.messaging.consumer;

import com.algaposts.text_processor.domain.service.TextProcessor;
import com.algaposts.text_processor.infrastructure.messaging.dto.PostProcessingMessage;
import com.algaposts.text_processor.infrastructure.messaging.dto.PostProcessingResult;
import com.algaposts.text_processor.infrastructure.messaging.publisher.ResultPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TextProcessorMessageConsumer {

    private final TextProcessor textProcessor;
    private final ResultPublisher resultPublisher;

    private static final String QUEUE_TEXT_PROCESSOR = "text-processor-service.post-processing.v1.q";

    @RabbitListener(queues = QUEUE_TEXT_PROCESSOR)
    public void processTextMessage(PostProcessingMessage message) {
        try {
            log.info("Mensagem recebida para processamento: Post ID {}", message.getPostId());

            PostProcessingResult result = textProcessor.processText(message);

            resultPublisher.publishResult(result);

            log.info("Processamento concluído com sucesso para Post ID: {}", result.getPostId());

        } catch (Exception e) {
            log.error("Erro ao processar mensagem para Post ID: {}", message.getPostId(), e);
            throw e;
        }
    }
}