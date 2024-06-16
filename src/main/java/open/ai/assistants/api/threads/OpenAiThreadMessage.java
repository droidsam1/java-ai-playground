package open.ai.assistants.api.threads;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAiThreadMessage(@JsonProperty("role") OpenAiRole role, @JsonProperty("content") String content) {

}
