package net.hollowcube.chat.storage;

import net.hollowcube.chat.ChatQuery;
import org.bson.BsonDocument;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

public class TestMongoChatStorage {


    public static class TestQueryConversion {

        // Breaks contract, but we are only calling chatQueryToBson
        private final MongoChatStorage storage = new MongoChatStorage(null);

        @Test
        public void testEmptyQuery() {
            ChatQuery chatQuery = ChatQuery.builder().build();
            BsonDocument query = storage.chatQueryToBson(chatQuery);

            assertThat(query).isEmpty();
        }

        @Test
        public void testFilterServerIds() {
            ChatQuery chatQuery = ChatQuery.builder()
                    .serverId("a", "b")
                    .build();
            BsonDocument query = storage.chatQueryToBson(chatQuery);

            BsonDocument expected = BsonDocument.parse("""
                    {
                        "$and": [{
                            $or: [
                                {
                                    "serverId": {
                                        "$regularExpression": {"pattern": "a*", "options": ""}
                                    }
                                },
                                {
                                    "serverId": {
                                        "$regularExpression": {"pattern": "b*", "options": ""}
                                    }
                                }
                            ]
                        }]
                    }
                    """);
            assertThat(query).isEqualTo(expected);
        }

        @Test
        public void testFilterChannelIds() {
            ChatQuery chatQuery = ChatQuery.builder()
                    .context("1", "2")
                    .build();
            BsonDocument query = storage.chatQueryToBson(chatQuery);

            BsonDocument expected = BsonDocument.parse("""
                    {
                        "$and": [{
                            "context": {
                                "$in": ["1", "2"]
                            }
                        }]
                    }
                    """);
            assertThat(query).isEqualTo(expected);
        }

        @Test
        public void testFilterSenders() {
            ChatQuery chatQuery = ChatQuery.builder()
                    .sender(UUID.fromString("19aa5eff-0f80-464d-b7c1-330b729571c8"))
                    .sender(UUID.fromString("c2ead875-fd86-45bd-991a-24563d1defd2"))
                    .build();
            BsonDocument query = storage.chatQueryToBson(chatQuery);

            BsonDocument expected = BsonDocument.parse("""
                    {
                        "$and": [{
                            "sender": {
                                "$in": [
                                    {"$binary": {"base64": "Gape/w+ARk23wTMLcpVxyA==", "subType": "04"}},
                                    {"$binary": {"base64": "wurYdf2GRb2ZGiRWPR3v0g==", "subType": "04"}}
                                ]
                            }
                        }]
                    }
                    """);
            assertThat(query).isEqualTo(expected);
        }

        @Test
        public void testFilterMessage() {
            ChatQuery chatQuery = ChatQuery.builder()
                    .message("Hello, world")
                    .build();
            BsonDocument query = storage.chatQueryToBson(chatQuery);

            BsonDocument expected = BsonDocument.parse("""
                    {
                        "$and": [{
                            "message": "Hello, world"
                        }]
                    }
                    """);
            assertThat(query).isEqualTo(expected);
        }
    }
}
