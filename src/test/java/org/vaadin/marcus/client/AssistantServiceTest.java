package org.vaadin.marcus.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssistantServiceTest {

    private AssistantService assistantService;

    @BeforeEach
    public void setup() {
        assistantService = new AssistantService(null,null);
    }

    @Test
    void shouldReturnResponse() throws InterruptedException {

        var response = assistantService.chat("",
                                             "can you show me an example of token payment api request and its response?");

        response.subscribe(System.out::println);

    }

}