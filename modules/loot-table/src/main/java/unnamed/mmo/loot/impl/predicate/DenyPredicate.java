package unnamed.mmo.loot.impl.predicate;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import net.minestom.server.utils.NamespaceID;
import unnamed.mmo.loot.type.LootPredicate;

public class DenyPredicate implements LootPredicate {

    public static final Codec<DenyPredicate> CODEC = Codec.unit(new DenyPredicate());

    @Override
    public boolean test(Object entry) {
        return false;
    }


    @AutoService(LootPredicate.Factory.class)
    public static final class Factory extends LootPredicate.Factory {

        public Factory() {
            super(
                    NamespaceID.from("unnamed:deny"),
                    DenyPredicate.class,
                    DenyPredicate.CODEC
            );
        }
    }
}
