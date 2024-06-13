package org.vaadin.marcus.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

@Disabled
class AssistantServiceTest {

    private AssistantService assistantService;

    @BeforeEach
    public void setup() {
        assistantService = new AssistantService("", "");
    }

    @Test
    @Disabled
    void shouldReturnResponse() {
        String prompt = "Hello, Assistant!";
        StepVerifier.create(assistantService.chat("", prompt))
                    .expectNextMatches(response -> !response.isBlank())
                    .expectComplete()
                    .verify();
    }

}