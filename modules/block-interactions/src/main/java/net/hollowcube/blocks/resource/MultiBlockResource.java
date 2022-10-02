package net.hollowcube.blocks.resource;

//{
//  "namespace": "starlight:oak_tree",
//  "loot_table": "starlight:oak_tree",
//  "type": "schematic",
//  // size: {x: 3, y: 3, z: 3}, // Would prevent a mistake of accidentally adding a big one, im a fan of this
//  "schematics": [
//    "tree/foresttree1",
//    "tree/foresttree2",
//    "tree/foresttree3",
//    "tree/foresttree4",
//    "tree/foresttree5"
//  ]
//}

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hollowcube.dfu.ExtraCodecs;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.utils.NamespaceID;

import java.util.List;

public record MultiBlockResource(
        NamespaceID namespace,
        NamespaceID lootTable,
        Vec size,
        List<String> schematics
) implements Resource {

    public static final Codec<MultiBlockResource> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.NAMESPACE_ID.fieldOf("namespace").forGetter(MultiBlockResource::namespace),
            ExtraCodecs.NAMESPACE_ID.optionalFieldOf("loot_table", NamespaceID.from("starlight:empty")).forGetter(MultiBlockResource::lootTable),
            ExtraCodecs.VEC.fieldOf("size").forGetter(MultiBlockResource::size),
            Codec.STRING.listOf().fieldOf("schematics").forGetter(MultiBlockResource::schematics)
    ).apply(i, MultiBlockResource::new));


    @AutoService(Resource.Factory.class)
    public static class Factory extends Resource.Factory {
        public Factory() {
            super("multiblock", MultiBlockResource.class, MultiBlockResource.CODEC);
        }
    }

}
