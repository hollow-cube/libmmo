package unnamed.mmo.chat.storage;

import com.google.common.truth.Correspondence;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import unnamed.mmo.chat.ChatQuery;
import unnamed.mmo.test.TestUtil;
import unnamed.mmo.chat.ChatMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class TestMongoChatStorageIntegration {
    //todo generally could be a little more explicit in these tests

    @Container
    public static MongoDBContainer mongodb = new MongoDBContainer(DockerImageName.parse("mongo:5.0"));

    private static MongoClient mongoClient;
    private static MongoCollection<ChatMessage> chatCollection;
    private static MongoChatStorage storage;

    @BeforeAll
    public static void setUp() {
        mongoClient = MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(String.format("mongodb://%s:%d", mongodb.getHost(), mongodb.getFirstMappedPort())))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build());
        chatCollection = mongoClient.getDatabase("mmo").getCollection("chat", ChatMessage.class);
        storage = new MongoChatStorage(mongoClient);
    }

    @AfterAll
    public static void tearDown() {
        mongoClient.close();
    }

    @AfterEach
    public void cleanup() {
        // Wipe the chat collection
        chatCollection.deleteMany(new BsonDocument());
    }

    @Test
    public void testRecordMessage() {
        ChatMessage message = new ChatMessage(TestUtil.instantNow(), "test-abcd",
                "global", UUID.randomUUID(), "test message");

        storage.recordChatMessage(message).join();

        ChatMessage actual = chatCollection.find().first();
        assertEquals(message, actual);
    }

    @Test
    public void testEmptyQuery() {
        loadFixture("chat_messages");

        ChatQuery query = ChatQuery.builder().build();
        List<ChatMessage> results = storage.queryChatMessages(query).join();

        // Should reach max result window
        assertThat(results).hasSize(15);
        // Sorted by recent, so they should be in decreasing time order
        Instant last = results.get(0).timestamp();
        for (int i = 1; i < results.size(); i++) {
            Instant instant = results.get(i).timestamp();
            assertThat(last).isGreaterThan(instant);

            last = instant;
        }
    }

    @Test
    public void testQueryServerId() {
        loadFixture("chat_messages");

        ChatQuery query = ChatQuery.builder()
                .serverId("build")
                .build();
        List<ChatMessage> results = storage.queryChatMessages(query).join();

        // There is one message on build_1 and one on build_2, we should match both of them.
        assertThat(results).containsExactly(
                new ChatMessage(
                        Instant.parse("2022-07-30T13:36:49.473Z"),
                        "build_2",
                        "test_channel",
                        UUID.fromString("251dcab9-9309-4ee5-a611-0f36cac73230"),
                        "Hello, world"
                ),
                new ChatMessage(
                        Instant.parse("2022-07-30T13:37:49.473Z"),
                        "build_1",
                        "test_channel_1",
                        UUID.fromString("251dcab9-9309-4ee5-a611-0f36cac73230"),
                        "Hello, world"
                )
        );
    }

    @Test
    public void testQueryContext() {
        loadFixture("chat_messages");

        ChatQuery query = ChatQuery.builder()
                .context("test_channel")
                .build();
        List<ChatMessage> results = storage.queryChatMessages(query).join();

        // There is one message in test_channel and another in test_channel_1
        // We should only match the one in test_channel
        assertThat(results).containsExactly(new ChatMessage(
                Instant.parse("2022-07-30T13:36:49.473Z"),
                "build_2",
                "test_channel",
                UUID.fromString("251dcab9-9309-4ee5-a611-0f36cac73230"),
                "Hello, world"
        ));
    }

    @Test
    public void testQuerySender() {
        loadFixture("chat_messages");

        ChatQuery query = ChatQuery.builder()
                .sender(UUID.fromString("5c44635e-3fa6-40c0-b752-e8cb00aa8c4d"))
                .build();
        List<ChatMessage> results = storage.queryChatMessages(query).join();

        // Sender has sent two messages
        assertThat(results).hasSize(2);
        assertThat(results)
                .comparingElementsUsing(Correspondence.transforming(ChatMessage::message, "message()"))
                .containsExactly("global message", "Local chat nice");
    }

    @Test
    public void testQueryMultipleSenders() {
        loadFixture("chat_messages");

        ChatQuery query = ChatQuery.builder()
                .sender(UUID.fromString("5c44635e-3fa6-40c0-b752-e8cb00aa8c4d"))
                .sender(UUID.fromString("38c46b86-a34d-441f-b61d-61cb056f7c01"))
                .build();
        List<ChatMessage> results = storage.queryChatMessages(query).join();

        assertThat(results).hasSize(3);
    }

    private void loadFixture(@NotNull String name) {
        //todo generalize me
        InputStream content = getClass().getClassLoader().getResourceAsStream("fixtures/" + name + ".json");
        assertThat(content).isNotNull();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(content))) {
            String fileContent = reader.lines().collect(Collectors.joining("\n"));

            mongoClient.getDatabase("mmo").getCollection("chat", BsonDocument.class)
                    .insertMany(BsonArray.parse(fileContent).stream().map(BsonValue::asDocument).toList());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
