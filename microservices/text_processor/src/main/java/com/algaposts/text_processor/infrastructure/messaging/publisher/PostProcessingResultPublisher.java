
package com.algaposts.text_processor.infrastructure.messaging.publisher;

import com.algaposts.text_processor.infrastructure.messaging.dto.PostProcessingResult;
import com.algaposts.text_processor.infrastructure.messaging.exception.ResultPublishingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostProcessingResultPublisher implements ResultPublisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_POST_PROCESS = "post-processing-exchange.v1.e";
    private static final String ROUTING_KEY_POST_RESULTED = "post.resulted";

    @Override
    public void publishResult(PostProcessingResult result) {
        try {
            log.info("Enviando resultado do processamento para Post ID: {}", result.getPostId());

            rabbitTemplate.convertAndSend(EXCHANGE_POST_PROCESS, ROUTING_KEY_POST_RESULTED, result);

            log.info("Resultado enviado com sucesso para Post ID: {} - Palavras: {} - Valor: {}",
                    result.getPostId(), result.getWordCount(), result.getCalculatedValue());

        } catch (Exception e) {
            log.error("Erro ao enviar resultado para Post ID: {}", result.getPostId(), e);
            throw new ResultPublishingException("Falha ao enviar resultado do processamento", e);
        }
    }
}