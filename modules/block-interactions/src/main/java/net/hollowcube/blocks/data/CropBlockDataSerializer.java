package net.hollowcube.blocks.data;

import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagSerializer;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CropBlockDataSerializer implements TagSerializer<CropBlockData> {

    private final Tag<Integer> seedMaterialTag = Tag.Integer("seedMaterial");
    private final Tag<Integer> cropGrownMaterialTag = Tag.Integer("cropGrownMaterial");
    private final Tag<Integer> cropBlockTag = Tag.Integer("cropBlock");
    private final Tag<Integer> maxAgeTag = Tag.Integer("maxAge");
    private final Tag<Boolean> createsOtherTag = Tag.Boolean("createsOther");

    @Override
    public @Nullable CropBlockData read(@NotNull TagReadable reader) {
        if (reader.hasTag(seedMaterialTag) && reader.hasTag(cropGrownMaterialTag) &&
                reader.hasTag(cropBlockTag) && reader.hasTag(maxAgeTag) && reader.hasTag(createsOtherTag)) {
            return new CropBlockData(
                    Objects.requireNonNullElse(Material.fromId(reader.getTag(seedMaterialTag)), Material.AIR),
                    Objects.requireNonNullElse(Material.fromId(reader.getTag(cropGrownMaterialTag)), Material.AIR),
                    Objects.requireNonNullElse(Block.fromBlockId(reader.getTag(cropBlockTag)), Block.AIR),
                    reader.getTag(maxAgeTag),
                    reader.getTag(createsOtherTag)
            );
        } else {
            return null;
        }
    }

    @Override
    public void write(@NotNull TagWritable writer, @NotNull CropBlockData value) {
        writer.setTag(seedMaterialTag, value.seedMaterial().id());
        writer.setTag(cropGrownMaterialTag, value.cropGrownMaterial().id());
        writer.setTag(cropBlockTag, value.cropBlock().id());
        writer.setTag(maxAgeTag, value.maximumAge());
        writer.setTag(createsOtherTag, value.createAnotherBlock());

    }
}
