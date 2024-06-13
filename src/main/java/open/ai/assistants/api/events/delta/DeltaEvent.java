package open.ai.assistants.api.events.delta;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public record DeltaEvent(String id, String object, Delta delta) {

    public Optional<String> getDeltaText() {
        return Optional.ofNullable(delta)
                       .map(Delta::content)
                       .map(List::getFirst)
                       .filter(isTextType())
                       .map(Content::text)
                       .map(Text::value);
    }

    private @NotNull Predicate<Content> isTextType() {
        return content -> content.type().equalsIgnoreCase("text");
    }
}

record Delta(List<Content> content) {

}

record Content(int index, String type, Text text) {

}

record Text(String value, List<Object> annotations) {

}


