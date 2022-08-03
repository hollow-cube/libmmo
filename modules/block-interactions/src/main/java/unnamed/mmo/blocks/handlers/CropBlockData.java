package unnamed.mmo.blocks.handlers;

import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

// createAnotherBlock boolean is a flag set true for pumpkins and melons, means this seed creates another block when fully grown
public record CropBlockData(@NotNull Material seedMaterial, @NotNull Material cropBlockMaterial, int maximumAge, boolean createAnotherBlock) {}
