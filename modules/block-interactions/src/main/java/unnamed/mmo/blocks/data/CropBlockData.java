package unnamed.mmo.blocks.data;

import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

// createAnotherBlock boolean is a flag set true for pumpkins and melons, means this seed creates another block when fully grown
public record CropBlockData(@NotNull Material seedMaterial, @NotNull Material cropGrownMaterial, @NotNull Block cropBlock, int maximumAge, boolean createAnotherBlock) {}
