package open.ai.assistants.api.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import okio.BufferedSource;
import open.ai.assistants.api.events.delta.DeltaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.FluxSink;

public class OpenAiEventStreamParser {

    private final Logger logger = LoggerFactory.getLogger(OpenAiEventStreamParser.class);

    public void parseDeltaEvents(BufferedSource source, FluxSink<String> sink) {
        try {
            parseDeltaEvents(source).flatMap(DeltaEvent::getDeltaText).ifPresent(sink::next);
        } catch (IOException exception) {
            logger.error("Error while parsing delta events", exception);
            sink.error(exception);
        }
    }

    protected Optional<DeltaEvent> parseDeltaEvents(BufferedSource source) throws IOException {
        var line = source.readUtf8Line();
        if (Objects.requireNonNull(line).startsWith("event: thread.message.delta")) {
            String dataLine = source.readUtf8Line();
            String jsonData = Objects.requireNonNull(dataLine).substring(6); // Remove "data: " prefix
            return Optional.ofNullable(new ObjectMapper().readValue(jsonData, DeltaEvent.class));
        }
        return Optional.empty();
    }

}
