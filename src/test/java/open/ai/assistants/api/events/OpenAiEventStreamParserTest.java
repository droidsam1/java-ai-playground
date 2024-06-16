package open.ai.assistants.api.events;

import java.io.IOException;
import java.util.ArrayList;
import open.ai.assistants.api.events.delta.DeltaEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vaadin.marcus.client.FakeBufferedSource;

class OpenAiEventStreamParserTest {


    private final OpenAiEventStreamParser parser = new OpenAiEventStreamParser();

    private final static String EXAMPLE_INPUT = """
            event: thread.created
            data: {"id": "thread_123", "object": "thread", ...}

            event: thread.created
            data: {"id":"thread_Rp97bqtfrBshBw4lAB3owTys","object":"thread","created_at":1718277419,"metadata":{},"tool_resources":{"code_interpreter":{"file_ids":[]}}}

            event: thread.run.step.created
            data: {"id":"step_oVIStelltob6YWaVrDRuqub3","object":"thread.run.step","created_at":1718277420,"run_id":"run_ggX5lejkm30Rw9iRNlzhtQ7D","assistant_id":"asst_BGpoEsdURVbaF99QL9WQO3He","thread_id":"thread_Rp97bqtfrBshBw4lAB3owTys","type":"message_creation","status":"in_progress","cancelled_at":null,"completed_at":null,"expires_at":1718278019,"failed_at":null,"last_error":null,"step_details":{"type":"message_creation","message_creation":{"message_id":"msg_mvUi6UHywjsDMHMPjUh99rr2"}},"usage":null}

            event: thread.message.delta
            data: {"id":"msg_mvUi6UHywjsDMHMPjUh99rr2","object":"thread.message.delta","delta":{"content":[{"index":0,"type":"text","text":{"value":"Hi","annotations":[]}}]}}

            event: thread.message.delta
            data: {"id":"msg_mvUi6UHywjsDMHMPjUh99rr2","object":"thread.message.delta","delta":{"content":[{"index":0,"type":"text","text":{"value":"I am your assistant","annotations":[]}}]}}
            """;

    @Test
    void shouldParseDeltaEvents() throws IOException {

        var source = new FakeBufferedSource(EXAMPLE_INPUT);
        var deltaEvents = new ArrayList<DeltaEvent>();
        while (!source.exhausted()) {
            parser.parseDeltaEvents(source).ifPresent(deltaEvents::add);
        }

        Assertions.assertFalse(deltaEvents.isEmpty());
        Assertions.assertEquals("Hi", deltaEvents.getFirst().getDeltaText().orElseThrow());
        Assertions.assertEquals("I am your assistant", deltaEvents.get(1).getDeltaText().orElseThrow());
    }

}