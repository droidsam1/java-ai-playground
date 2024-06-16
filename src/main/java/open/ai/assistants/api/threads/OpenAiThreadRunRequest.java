package open.ai.assistants.api.threads;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAiThreadRunRequest(@JsonProperty("assistant_id") String assistantId,
                                     @JsonProperty("thread") OpenAiThread thread,
                                     @JsonProperty("stream") boolean stream) {

}
