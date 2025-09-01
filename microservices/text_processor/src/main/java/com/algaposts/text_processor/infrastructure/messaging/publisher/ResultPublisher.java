package com.algaposts.text_processor.infrastructure.messaging.publisher;

import com.algaposts.text_processor.infrastructure.messaging.dto.PostProcessingResult;

public interface ResultPublisher {
    void publishResult(PostProcessingResult result);
}