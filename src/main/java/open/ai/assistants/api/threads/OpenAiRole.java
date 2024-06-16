package open.ai.assistants.api.threads;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OpenAiRole {
    USER("user"), ASSISTANT("assistant");

    private final String value;

    OpenAiRole(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
