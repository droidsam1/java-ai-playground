package org.vaadin.marcus.client;


import com.azure.ai.openai.assistants.AssistantsClient;
import com.azure.ai.openai.assistants.AssistantsClientBuilder;
import com.azure.ai.openai.assistants.models.AssistantThread;
import com.azure.ai.openai.assistants.models.AssistantThreadCreationOptions;
import com.azure.ai.openai.assistants.models.CreateRunOptions;
import com.azure.ai.openai.assistants.models.MessageContent;
import com.azure.ai.openai.assistants.models.MessageRole;
import com.azure.ai.openai.assistants.models.MessageTextContent;
import com.azure.ai.openai.assistants.models.PageableList;
import com.azure.ai.openai.assistants.models.RunStatus;
import com.azure.ai.openai.assistants.models.ThreadMessage;
import com.azure.ai.openai.assistants.models.ThreadRun;
import com.azure.core.credential.KeyCredential;
import com.azure.core.util.ClientOptions;
import com.azure.core.util.Header;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@BrowserCallable
@AnonymousAllowed
@Service
public class AssistantService {

    private final String assistantId;
    private final String apiKey;

    private final AssistantsClient client;
    private final AssistantThread thread;

    public AssistantService(
            @Value("${open.api.api-key}") String apiKey,
            @Value("${open.api.assistant-id}") String assistantId
    ) {
        this.assistantId = assistantId;
        this.apiKey = apiKey;
        client = new AssistantsClientBuilder()
                .credential(new KeyCredential(Objects.requireNonNull(apiKey)))
                .clientOptions(new ClientOptions().setHeaders(List.of(new Header("OpenAI-Beta", "assistants=v2"))))
                .buildClient();
        //TODO staboas: this should not be here, to support multiple clients
        thread = client.createThread(new AssistantThreadCreationOptions());
    }

    public Flux<String> chat(String chatId, String userMessage) throws InterruptedException {

        ThreadMessage threadMessage = client.createMessage(thread.getId(), MessageRole.USER, userMessage);
        ThreadRun run = client.createRun(thread.getId(), new CreateRunOptions(Objects.requireNonNull(assistantId)));

        do {
            run = client.getRun(run.getThreadId(), run.getId());
            Thread.sleep(100);
        } while (run.getStatus() == RunStatus.QUEUED || run.getStatus() == RunStatus.IN_PROGRESS);

        PageableList<ThreadMessage> messages = client.listMessages(run.getThreadId());
        List<ThreadMessage> data = messages.getData();
        for (int i = 0; i < data.size(); i++) {
            ThreadMessage dataMessage = data.get(i);
            MessageRole role = dataMessage.getRole();
            for (MessageContent messageContent : dataMessage.getContent()) {
                MessageTextContent messageTextContent = (MessageTextContent) messageContent;
                //TODO change it for with a proper logging mechanism
                System.out.println(i + ": Role = " + role + ", content = " + messageTextContent.getText().getValue());
            }
        }

        return Flux.fromIterable(data)
                   .flatMap(dataMessage -> Flux.fromIterable(dataMessage.getContent()).map(messageContent -> {
                       MessageTextContent messageTextContent = (MessageTextContent) messageContent;
                       return messageTextContent.getText().getValue();
                   }));
    }
}
