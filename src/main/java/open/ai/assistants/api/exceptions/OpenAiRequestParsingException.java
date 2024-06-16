package open.ai.assistants.api.exceptions;

public class OpenAiRequestParsingException extends RuntimeException {

    public OpenAiRequestParsingException(Exception e) {
        super("Error while building the open ai request", e);
    }
}
