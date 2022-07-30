package unnamed.mmo.chat.storage;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import net.minestom.server.instance.Instance;
import org.bson.*;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import unnamed.mmo.chat.TestUtil;
import unnamed.mmo.chat.ChatMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class TestMongoChatStorageIntegration {

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



    }

    private void loadFixture(String name) {
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
