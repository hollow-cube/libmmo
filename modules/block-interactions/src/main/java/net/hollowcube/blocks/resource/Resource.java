package net.hollowcube.blocks.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hollowcube.dfu.ExtraCodecs;
import net.hollowcube.lang.LanguageProvider;
import net.hollowcube.registry.Registry;
import net.hollowcube.registry.ResourceFactory;
import net.kyori.adventure.text.Component;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/*

{
  "namespace": "starlight:oak_tree",
  "loot_table": "starlight:oak_tree",
  "type": "schematic",
  // size: {x: 3, y: 3, z: 3}, // Would prevent a mistake of accidentally adding a big one, im a fan of this
  "schematics": [
    "tree/foresttree1",
    "tree/foresttree2",
    "tree/foresttree3",
    "tree/foresttree4",
    "tree/foresttree5"
  ]
}

{
  "namespace": "starlight:gold_ore",
  "loot_table": "starlight:gold_ore",
  "type": "block",
  "block": "minecraft:gold_ore",
  "replacement": "minecraft:bedrock"
}

 */

public interface Resource extends net.hollowcube.registry.Resource {

    Tag<String> TAG = Tag.String("resource");

    /**
     * @return The translation key for this item
     * @see LanguageProvider#get(Component)
     */
    @Contract(pure = true)
    default @NotNull String translationKey() {
        return String.format("resource.%s.%s.name", namespace().namespace(), namespace().path());
    }

    Codec<Resource> CODEC = ExtraCodecs.lazy(() -> Factory.CODEC).dispatch(Factory::from, Factory::codec);

    Registry<Resource> REGISTRY = Registry.lazy(() -> Registry.codec("resource", CODEC));

    static @UnknownNullability Resource fromNamespaceId(@NotNull NamespaceID namespace) {
        return REGISTRY.get(namespace);
    }

    static @UnknownNullability Resource fromNamespaceId(@NotNull String namespace) {
        return REGISTRY.get(namespace);
    }


    class Factory extends ResourceFactory<Resource> {
        static Registry<Factory> REGISTRY = Registry.service("resource_type", Resource.Factory.class);
        static Registry.Index<Class<?>, Factory> TYPE_REGISTRY = REGISTRY.index(Factory::type);

        static Codec<Factory> CODEC = Codec.STRING.xmap(ns -> REGISTRY.required(ns), Factory::name);

        public Factory(String id, Class<? extends Resource> type, Codec<? extends Resource> codec) {
            super(NamespaceID.from("starlight", id), type, codec);
        }

        public static @NotNull Factory from(@NotNull Resource resource) {
            return TYPE_REGISTRY.get(resource.getClass());
        }
    }

}
