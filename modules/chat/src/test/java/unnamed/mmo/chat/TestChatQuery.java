package unnamed.mmo.chat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

public class TestChatQuery {

    @Test
    public void testBuilderMethods() {
        UUID[] uuids = new UUID[]{ new UUID(0, 0), new UUID(0, 1), new UUID(0, 2), new UUID(0, 3) };
        ChatQuery query = ChatQuery.builder()
                .serverId("a", "b")
                .serverIds(List.of("c", "d"))
                .channelId("1", "2")
                .channelIds(List.of("3", "4"))
                .sender(uuids[0], uuids[1])
                .senders(List.of(uuids[2], uuids[3]))
                .message("test message")
                .build();

        assertThat(query.serverIds()).containsExactly("a", "b", "c", "d");
        assertThat(query.channelIds()).containsExactly("1", "2", "3", "4");
        assertThat(query.senders()).containsExactly((Object[]) uuids);
        assertThat(query.message()).isEqualTo("test message");
    }

}
