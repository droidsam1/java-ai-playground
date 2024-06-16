package org.vaadin.marcus.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import open.ai.assistants.api.events.OpenAiEventStreamParser;
import open.ai.assistants.api.exceptions.OpenAiRequestParsingException;
import open.ai.assistants.api.threads.OpenAiRole;
import open.ai.assistants.api.threads.OpenAiThread;
import open.ai.assistants.api.threads.OpenAiThreadMessage;
import open.ai.assistants.api.threads.OpenAiThreadRunRequest;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@BrowserCallable
@AnonymousAllowed
@Service
public class OpenAiAssistantService implements AssistantService {

    private static final String ASSISTANT_API_URL = "https://api.openai.com/v1/threads/runs";
    private final Logger logger = LoggerFactory.getLogger(OpenAiAssistantService.class);
    private final String assistantId;
    private final String apiKey;
    private final OkHttpClient client;

    @Autowired
    public OpenAiAssistantService(
            @Value("${open.api.api-key}") String apiKey,
            @Value("${open.api.assistant-id}") String assistantId
    ) {
        this(apiKey, assistantId, new OkHttpClient());
    }

    public OpenAiAssistantService(
            @Value("${open.api.api-key}") String apiKey,
            @Value("${open.api.assistant-id}") String assistantId,
            OkHttpClient client
    ) {
        this.apiKey = apiKey;
        this.assistantId = assistantId;
        this.client = client;
    }

    public Flux<String> chat(String chatId, String userMessage) {
        //TODO pending to find a way to handle chatId or threadIds to maintain different contexts for different sessions
        return getResponseFromOpenAI(userMessage);
    }

    private Flux<String> getResponseFromOpenAI(String prompt) {
        return Flux.create(sink -> {
            var body = createRequestBodyFor(prompt);
            Request request = new Request.Builder()
                    .url(ASSISTANT_API_URL)
                    .post(RequestBody.create(body, MediaType.parse("application/json")))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("OpenAI-Beta", "assistants=v2")
                    .build();

            logger.info("Sending request %s".formatted(body));
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    logger.error("Error while sending request to OpenAI", e);
                    sink.error(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    logger.debug("Received response from OpenAI");
                    if (response.body() != null) {
                        try (ResponseBody responseBody = response.body()) {
                            BufferedSource source = responseBody.source();
                            OpenAiEventStreamParser openAiEventStreamParser = new OpenAiEventStreamParser();
                            while (!source.exhausted()) {
                                openAiEventStreamParser.parseDeltaEvents(source, sink);
                            }
                        }
                    }
                    sink.complete();
                }
            });
        });
    }

    private @NotNull String createRequestBodyFor(String prompt) {
        var runRequest = new OpenAiThreadRunRequest(
                assistantId,
                new OpenAiThread(new OpenAiThreadMessage(OpenAiRole.USER, prompt)),
                true
        );

        try {
            return new ObjectMapper().writeValueAsString(runRequest);
        } catch (JsonProcessingException e) {
            throw new OpenAiRequestParsingException(e);
        }
    }

}
