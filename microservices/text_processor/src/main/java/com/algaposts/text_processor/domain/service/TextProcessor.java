
package com.algaposts.text_processor.domain.service;

import com.algaposts.text_processor.infrastructure.messaging.dto.PostProcessingMessage;
import com.algaposts.text_processor.infrastructure.messaging.dto.PostProcessingResult;

public interface TextProcessor {
    PostProcessingResult processText(PostProcessingMessage message);
}