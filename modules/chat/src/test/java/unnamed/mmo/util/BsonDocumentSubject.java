package unnamed.mmo.util;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.MapSubject;
import org.bson.BsonDocument;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.jetbrains.annotations.Nullable;

import static com.google.common.truth.Truth.assertThat;

public final class BsonDocumentSubject extends MapSubject {
    private final BsonDocument actual;

    private BsonDocumentSubject(FailureMetadata metadata, @Nullable BsonDocument actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public BsonDocumentSubject containsDocument(String name) {
        return TruthBson.assertThat(getWithType(name, BsonType.DOCUMENT).asDocument());
    }

    public BsonArraySubject containsArray(String name) {
        return TruthBson.assertThat(getWithType(name, BsonType.ARRAY).asArray());
    }

    private BsonValue getWithType(String name, BsonType type) {
        isNotNull();
        containsKey(name);
        BsonValue value = actual.get(name);
        assertThat(value.getBsonType()).isEqualTo(type);
        return value;
    }

    public static Factory<BsonDocumentSubject, BsonDocument> bsonDocuments() {
        return BsonDocumentSubject::new;
    }
}
