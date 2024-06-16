package open.ai.assistants.api.threads;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public record OpenAiThread(@JsonProperty("id") String id,
                           @JsonProperty("messages") Collection<OpenAiThreadMessage> messages) {

    public OpenAiThread(OpenAiThreadMessage message) {
        this(UUID.randomUUID().toString(), List.of(message));
    }
}
