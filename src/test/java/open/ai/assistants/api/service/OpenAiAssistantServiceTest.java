package open.ai.assistants.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.vaadin.marcus.client.OpenAiAssistantService;
import reactor.test.StepVerifier;

class OpenAiAssistantServiceTest {

    private OkHttpClient client;
    private Call call;

    //SUT
    private OpenAiAssistantService assistantService;

    @BeforeEach
    public void setup() {
        this.call = mock(Call.class);
        this.client = mock(OkHttpClient.class);
        when(client.newCall(any(Request.class))).thenReturn(call);
        assistantService = new OpenAiAssistantService("apiKey", "assistantId", client);
    }

    private String getFileContent(String filePath) {
        var inputStream = Objects.requireNonNull(this.getClass()
                                                     .getResourceAsStream(filePath));
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

    }

    @Test
    void shouldReturnResponseWhenOpenAISuccessful() {
        var expectedResponse = getFileContent("/response/example_response_event_stream.txt");
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            Response response = new Response.Builder()
                    .code(200)
                    .message(expectedResponse)
                    .protocol(Protocol.HTTP_2)
                    .request(new Request.Builder().url("http://localhost").build())
                    .body(ResponseBody.create(expectedResponse, MediaType.parse("application/json")))
                    .build();
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any(Callback.class));

        StepVerifier.create(assistantService.chat("", "prompt"))
                    .expectNext("Hi")
                    .expectNext("I am your assistant")
                    .verifyComplete();
    }

    @Test
    void shouldCompleteWithErrorWhenOpenAIFails() {
        IOException expectedException = new IOException("Expected exception");
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onFailure(call, expectedException);
            return null;
        }).when(call).enqueue(any(Callback.class));

        StepVerifier.create(assistantService.chat("", "prompt"))
                    .verifyErrorMatches(throwable -> throwable == expectedException);
    }

    @Test
    void shouldSendCorrectRequestToOpenAI() {
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        when(client.newCall(requestCaptor.capture())).thenReturn(call);

        assistantService.chat("", "prompt").subscribe();

        verify(client).newCall(any(Request.class));

        Request sentRequest = requestCaptor.getValue();
        Assertions.assertEquals("application/json", sentRequest.header("Content-Type"));
        Assertions.assertEquals("Bearer apiKey", sentRequest.header("Authorization"));
        Assertions.assertEquals("assistants=v2", sentRequest.header("OpenAI-Beta"));
        Assertions.assertEquals("json", sentRequest.body().contentType().subtype());
    }

    @Test
    @Disabled
    void testRealSystem() throws InterruptedException {
        assistantService = new OpenAiAssistantService("", "", new OkHttpClient());
        assistantService.chat("", "prompt").subscribe(System.out::println);
        Thread.sleep(10000);
    }
}