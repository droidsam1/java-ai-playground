package open.ai.assistants.api.threads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public record OpenAiThread(@JsonProperty("id") String id,
                           @JsonProperty("messages") Collection<OpenAiThreadMessage> messages) {

    public OpenAiThread(OpenAiThreadMessage message) {
        this(null, List.of(message));
    }
}
