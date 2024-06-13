package open.ai.assistants.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import okio.BufferedSource;
import open.ai.assistants.api.events.delta.DeltaEvent;
import reactor.core.publisher.FluxSink;

public class OpenAiEventStreamParser {

    public void parseDeltaEvents(BufferedSource source, FluxSink<String> sink) {
        try {
            parseDeltaEvents(source).flatMap(DeltaEvent::getDeltaText).ifPresent(sink::next);
        } catch (IOException exception) {
            //TODO proper logging, revisit to do something here
        }
    }

    public Optional<DeltaEvent> parseDeltaEvents(BufferedSource source) throws IOException {
        var line = source.readUtf8Line();
        if (Objects.requireNonNull(line).startsWith("event: thread.message.delta")) {
            String dataLine = source.readUtf8Line();
            String jsonData = Objects.requireNonNull(dataLine).substring(6); // Remove "data: " prefix
            return Optional.ofNullable(new ObjectMapper().readValue(jsonData, DeltaEvent.class));
        }
        return Optional.empty();
    }

}
