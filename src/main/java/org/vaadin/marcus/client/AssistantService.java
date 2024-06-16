package org.vaadin.marcus.client;

import reactor.core.publisher.Flux;

public interface AssistantService {

    Flux<String> chat(String chatId, String userMessage);
}
