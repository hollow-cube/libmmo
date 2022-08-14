package unnamed.mmo.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.loot.context.LootContext;
import unnamed.mmo.registry.Resource;
import unnamed.mmo.util.ExtraCodecs;

import java.util.List;
import java.util.stream.Collectors;

public record LootTable(
        @NotNull NamespaceID namespace,
        @NotNull List<LootModifier> modifiers,
        @NotNull List<LootPool> pools
) implements Resource {

    //Loot application Notes
    // this is a little bit challenging, we want to apply the loot entries based on
    // a registered handler for the given type, except in this case we want to
    // override this to handle items explicitly.

    // The item override is just to give the items some velocity on a block face
    // this could be handled within the item drop handler, using some context
    // information.

    // Context in this case
    // this: entity breaking the block
    // pos: the position of the block
    // direction: Direction to send rewards

    // Final ask:
    // - Apply based on handlers registered for a particular type. Two handlers for the same type is an error
    // - Possible to override a handler to handle those loot entries explicitly.
    // - LootResult is the new return type from generation of a table
    //   - LootResult#override(Type.class, BiConsumer<Context, Type>)
    //   - LootResult#apply(Context)
    //   - LootResult#size()

    public static final Codec<LootTable> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.NAMESPACE_ID.fieldOf("namespace").forGetter(LootTable::namespace),
            LootModifier.CODEC.listOf().fieldOf("modifiers").forGetter(LootTable::modifiers),
            LootPool.CODEC.listOf().fieldOf("pools").forGetter(LootTable::pools)
    ).apply(i, LootTable::new));

    public LootTable {
        modifiers = List.copyOf(modifiers);
        pools = List.copyOf(pools);
    }

    public @NotNull List<@NotNull Object> generate(@NotNull LootContext context) {
        return pools.stream()
                .map(pool -> pool.generate(context))
                .flatMap(List::stream)
                .map(this::applyModifiers)
                .collect(Collectors.toList());
    }

    private @NotNull Object applyModifiers(@NotNull Object input) {
        for (LootModifier modifier : modifiers())
            input = modifier.apply(input);
        return input;
    }

}
