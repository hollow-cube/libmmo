package unnamed.mmo.util;

import com.google.common.truth.*;
import org.bson.*;
import org.bson.codecs.BsonCodec;
import org.bson.codecs.BsonDocumentCodec;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public final class BsonArraySubject extends IterableSubject {
    private final BsonArray actual;

    private BsonArraySubject(FailureMetadata metadata, @Nullable BsonArray actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public BsonDocumentSubject firstDocument() {
        return TruthBson.assertThat(getIndexAsType(0, BsonType.DOCUMENT).asDocument());
    }

    public Ordered containsExactlyBson(Object... varargs) {
        return containsExactly(Arrays.stream(varargs).map(TruthBson::toBson).toArray());
    }

    private BsonValue getIndexAsType(int index, BsonType type) {
        isNotNull();
        assertThat(actual.size()).isGreaterThan(index);
        BsonValue bson = actual.get(index);
        assertThat(bson.getBsonType()).isEqualTo(type);
        return bson;
    }

    public static Factory<BsonArraySubject, BsonArray> bsonArrays() {
        return BsonArraySubject::new;
    }
}
