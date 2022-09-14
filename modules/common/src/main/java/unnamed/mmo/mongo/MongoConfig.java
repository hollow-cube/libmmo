package unnamed.mmo.mongo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record MongoConfig(
        @NotNull String uri,
        boolean useTransactions
) {

    public static final Codec<MongoConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("uri").forGetter(MongoConfig::uri),
            Codec.BOOL.optionalFieldOf("use_transactions", false).forGetter(MongoConfig::useTransactions)
    ).apply(i, MongoConfig::new));

}
