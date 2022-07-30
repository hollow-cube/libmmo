package unnamed.mmo.util;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import static com.google.common.truth.Truth.assertAbout;
import static unnamed.mmo.util.BsonArraySubject.bsonArrays;
import static unnamed.mmo.util.BsonDocumentSubject.bsonDocuments;

//todo this class doesnt seem much better than just parsing bson manually
//     ended up parsing json in tests which is way more readable and exact. probably will delete these later
public class TruthBson {

    public static BsonDocumentSubject assertThat(@Nullable BsonDocument actual) {
        return assertAbout(bsonDocuments()).that(actual);
    }

    public static BsonArraySubject assertThat(@Nullable BsonArray actual) {
        return assertAbout(bsonArrays()).that(actual);
    }


    static BsonValue toBson(Object obj) {
        //todo this is super cursed but i dont want to work out how to do this serialization myself
        return new Document()
                .append("conversion", obj)
                .toBsonDocument()
                .get("conversion");
    }

}
