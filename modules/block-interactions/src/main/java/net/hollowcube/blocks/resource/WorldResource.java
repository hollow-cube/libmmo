package net.hollowcube.blocks.resource;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

public record WorldResource(
        int id,
        @NotNull Resource resource,
        @NotNull Instance instance,
        @NotNull Point pos
) {
    public static final Tag<Integer> ID_TAG = Tag.Integer("resource_id");

}
