package org.vaadin.marcus.client;


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
import open.ai.assistants.api.OpenAiEventStreamParser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@BrowserCallable
@AnonymousAllowed
@Service
public class AssistantService {

    private final String assistantId;
    private final String apiKey;
    private static final String ASSISTANT_API_URL = "https://api.openai.com/v1/threads/runs";


    public AssistantService(
            @Value("${open.api.api-key}") String apiKey,
            @Value("${open.api.assistant-id}") String assistantId
    ) {
        this.apiKey = apiKey;
        this.assistantId = assistantId;
    }

    public Flux<String> chat(String chatId, String userMessage) {
        //TODO pending to find a way to handle chatId or threadIds to maintain different contexts for different sessions
        return getResponseFromOpenAI(userMessage);
    }

    private Flux<String> getResponseFromOpenAI(String prompt) {
        return Flux.create(sink -> {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(createRequestBodyFor(prompt), MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(ASSISTANT_API_URL)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("OpenAI-Beta", "assistants=v2")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    sink.error(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
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
        return """
                {
                    "assistant_id": "{assistantId}",
                    "thread": {
                      "messages": [
                        {"role": "user", "content": "{prompt}"}
                      ]
                    },
                    "stream": true
                  }
                """.replace("{assistantId}", assistantId).replace("{prompt}", prompt);
    }
}
