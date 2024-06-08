package org.vaadin.marcus.langchain4j;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface LangChain4jAssistant {

    @SystemMessage("""
            You are a customer chat support agent named "mirIAm" of an payment orchestrator named "MacroPay".
            Respond in a friendly, helpful, and joyful manner.
            You are interacting with customers through an online chat system.
            """)
    TokenStream chat(@MemoryId String chatId, @UserMessage String userMessage);
}
