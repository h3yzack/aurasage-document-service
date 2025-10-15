package io.aurasage.document.event;


import org.springframework.stereotype.Component;

import io.aurasage.document.service.DocumentService;
import io.aurasage.events.common.EventConsumer;
import io.aurasage.events.common.QueueName;
import io.aurasage.events.dto.StorageEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@QueueName("storage.queue")
public class DocumentUploadedEventListener implements EventConsumer<StorageEvent> {

    private static final String UPLOAD_EVENT = "s3:ObjectCreated:Put";

    private final DocumentService documentService;

    public DocumentUploadedEventListener(DocumentService documentService) {
        this.documentService = documentService;
    }

	@Override
	public void consume(StorageEvent event) {
		log.info("Received StorageEvent: {}", event);
        
        if (!UPLOAD_EVENT.equals(event.getEventName())) {
            log.warn("Unhandled event type: {}", event.getEventName());
            return;
        }

        documentService.processDocumentUploadedEvent(event)
            .subscribe(
                null, // onNext not needed for Mono<Void>
                error -> log.error("Event processing failed: {}", error.getMessage())
            );

	}

}
